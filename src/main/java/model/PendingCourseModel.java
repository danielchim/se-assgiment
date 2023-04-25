package model;

import context.ContextState;
import entity.CourseStudentsSessionLink;
import entity.CoursesPendingList;
import entity.Session;
import entity.Students;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;


public class PendingCourseModel {
    String dbUrl;
    String dbUsername;
    String dbPassword;

    public PendingCourseModel() {
        ContextState.loadProperties();
        dbUrl = ContextState.getProperty("datasource.url");
        dbUsername = ContextState.getProperty("datasource.username");
        dbPassword = ContextState.getProperty("datasource.password");
    }

    public List<CoursesPendingList> getAllPendingRequests() {
        String sql = "SELECT * FROM courses_pending_list";
        List<CoursesPendingList> requests = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("idCourses_pending_list");
                    int sessionId = rs.getInt("Session_idSession");
                    String studentId = rs.getString("Students_idStudents");
                    int operation = rs.getInt("operation");
                    CoursesPendingList request = new CoursesPendingList();
                    request.setIdCoursesStudentsLink(id);
                    Students students = new Students();
                    students.setIdStudent(studentId);
                    request.setStudent(students);
                    Session session = new Session();
                    session.setIdSession(sessionId);
                    request.setSession(session);
                    request.setOperation(operation == 1);
                    requests.add(request);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    /**
     *
     * @param courseStudentsSessionLink the DTO for storing the registered session and the student + operation
     * @return
     */
    public boolean registerPendingCourse(CoursesPendingList courseStudentsSessionLink) {
        String sql = "INSERT INTO courses_pending_list (Students_idStudents, Session_idSession, operation) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseStudentsSessionLink.getStudent().getIdStudent());
            stmt.setInt(2, courseStudentsSessionLink.getSession().getIdSession());
            stmt.setInt(3, courseStudentsSessionLink.isOperation()?0:1);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<CoursesPendingList> getPendingRequestsBySessionId(int sessionId) {
        List<CoursesPendingList> pendingRequests = new ArrayList<>();

        String sql = "SELECT * FROM courses_pending_list WHERE Session_idSession = ?";

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword); // Assuming you have a getConnection() method to get a connection to your database
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, sessionId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int idCoursesPendingList = resultSet.getInt("idCourses_pending_list");
                String studentsIdStudents = resultSet.getString("Students_idStudents");
                int operation = resultSet.getInt("operation");

                CoursesPendingList pendingRequest = new CoursesPendingList();
                pendingRequest.setIdCoursesStudentsLink(idCoursesPendingList);
                Session session = new Session();
                session.setIdSession(sessionId);
                pendingRequest.setSession(session);
                Students students = new Students();
                students.setIdStudent(studentsIdStudents);
                pendingRequest.setStudent(students);
                pendingRequest.setOperation(operation == 1);
                pendingRequests.add(pendingRequest);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pendingRequests;
    }

    public boolean deletePendingRequest(CoursesPendingList list) {
        String sql = "DELETE FROM courses_pending_list WHERE idCourses_pending_list = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, list.getIdCoursesStudentsLink());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean addDropSwap(String studentId, int sessionId1, String studentId2, int sessionId2) {
        String sql = "SELECT COUNT(*) FROM courses_students_session_link WHERE Students_idStudents = ? AND Session_idSession = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt1 = conn.prepareStatement(sql);
             PreparedStatement stmt2 = conn.prepareStatement(sql)) {
            // Check if both students have registered for the requested sessions
            stmt1.setString(1, studentId);
            stmt1.setInt(2, sessionId1);
            ResultSet rs1 = stmt1.executeQuery();
            if (!rs1.next() || rs1.getInt(1) == 0) {
                return false;
            }
            stmt2.setString(1, studentId2);
            stmt2.setInt(2, sessionId2);
            ResultSet rs2 = stmt2.executeQuery();
            if (!rs2.next() || rs2.getInt(1) == 0) {
                return false;
            }

            // Update the courses_students_session_link table
            String updateSql = "UPDATE courses_students_session_link SET Students_idStudents = ? WHERE Session_idSession = ? AND Students_idStudents = ?";
            try (PreparedStatement updateStmt1 = conn.prepareStatement(updateSql);
                 PreparedStatement updateStmt2 = conn.prepareStatement(updateSql)) {
                conn.setAutoCommit(false);
                updateStmt1.setString(1, studentId2);
                updateStmt1.setInt(2, sessionId1);
                updateStmt1.setString(3, studentId);
                int updatedRows1 = updateStmt1.executeUpdate();
                updateStmt2.setString(1, studentId);
                updateStmt2.setInt(2, sessionId2);
                updateStmt2.setString(3, studentId2);
                int updatedRows2 = updateStmt2.executeUpdate();
                if (updatedRows1 == 1 && updatedRows2 == 1) {
                    // Commit the transaction and return true
                    conn.commit();
                    return true;
                } else {
                    // Rollback the transaction and return false
                    conn.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<AbstractMap.SimpleEntry<CoursesPendingList, CoursesPendingList>> findMatchingRequests() {
        List<CoursesPendingList> pendingAddRequests = new ArrayList<>();
        List<CoursesPendingList> pendingDropRequests = new ArrayList<>();
        List<AbstractMap.SimpleEntry<CoursesPendingList, CoursesPendingList>> matchingRequests = new ArrayList<>();

        // Separate pending requests into add and drop lists
        for (CoursesPendingList request : getAllPendingRequests()) {
            if (request.isOperation()) { // Assuming 1 is for add
                pendingAddRequests.add(request);
            } else { // Assuming 0 is for drop
                pendingDropRequests.add(request);
            }
        }

        // Compare each pending add request with each pending drop request
        for (CoursesPendingList addRequest : pendingAddRequests) {
            for (CoursesPendingList dropRequest : pendingDropRequests) {
                if (addRequest.getSession().getIdSession() == dropRequest.getSession().getIdSession()) {
                    AbstractMap.SimpleEntry<CoursesPendingList, CoursesPendingList> match = new AbstractMap.SimpleEntry<>(addRequest, dropRequest);
                    matchingRequests.add(match);
                }
            }
        }

        return matchingRequests;
    }
}
