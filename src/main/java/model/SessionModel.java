package model;

import context.ContextState;
import entity.CourseStudentsSessionLink;
import entity.Courses;
import entity.Session;
import entity.Teachers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionModel {
    String dbUrl;
    String dbUsername;
    String dbPassword;

    public SessionModel() {
        ContextState.loadProperties();
        dbUrl = ContextState.getProperty("datasource.url");
        dbUsername = ContextState.getProperty("datasource.username");
        dbPassword = ContextState.getProperty("datasource.password");
    }
    public List<Session> getSessionsByCourseCode(String courseCode) {
        String sql = "SELECT s.idSession, s.Courses_idCourses, s.weekTime, s.time, s.sessionCode ,s.capacity " +
                "FROM courses c " +
                "INNER JOIN session s ON c.idCourses = s.Courses_idCourses " +
                "WHERE c.course_code = ?";
        List<Session> sessions = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idSession = rs.getInt("idSession");
                    String sessionCode = rs.getString("sessionCode");
                    String weekTime = rs.getString("weekTime");
                    String time = rs.getString("time");
                    int capacity = rs.getInt("capacity");
                    Session session = new Session();
                    session.setIdSession(idSession);
                    session.setSessionCode(sessionCode);
                    session.setWeekTime(weekTime);
                    session.setTime(time);
                    session.setCapacity(capacity);
                    sessions.add(session);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    public Session getSessionBySessionCode(String sessionCode) {
        Session session = null;
        String sql = "SELECT s.idSession, s.Courses_idCourses, s.weekTime, s.time, s.capacity, s.remainingCapacity FROM courses c INNER JOIN session s ON c.idCourses = s.Courses_idCourses WHERE s.sessionCode = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionCode);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    session = new Session();
                    int idSession = rs.getInt("idSession");
                    Courses course = new Courses();
                    course.setIdCourses(rs.getInt("Courses_idCourses"));
                    String weekTime = rs.getString("weekTime");
                    String time = rs.getString("time");
                    int capacity = rs.getInt("capacity");
                    int remainingCap =  rs.getInt("remainingCapacity");
                    session.setIdSession(idSession);
                    session.setWeekTime(weekTime);
                    session.setTime(time);
                    session.setCapacity(capacity);
                    session.setRemainingCapacity(remainingCap);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return session;
    }

    /**
     *
     * @param courseStudentsSessionLink
     * @return
     */
    public boolean registerSession(CourseStudentsSessionLink courseStudentsSessionLink) {
        String selectSql = "SELECT remainingCapacity FROM session WHERE idSession = ?";
        String updateSql = "UPDATE session SET remainingCapacity = ? WHERE idSession = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            conn.setAutoCommit(false);
            selectStmt.setInt(1, courseStudentsSessionLink.getSession().getIdSession());
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    int remainingCapacity = rs.getInt("remainingCapacity");
                    if (remainingCapacity == 0) {
                        return false; // session is full
                    }
                    // Deduct 1 from the remaining capacity
                    int updatedRemainingCapacity = remainingCapacity - 1;
                    updateStmt.setInt(1, updatedRemainingCapacity);
                    updateStmt.setInt(2, courseStudentsSessionLink.getSession().getIdSession());
                    int updatedRows = updateStmt.executeUpdate();
                    if (updatedRows == 1) {
                        String insertSql = "INSERT INTO courses_students_session_link (Students_idStudents, Session_idSession) VALUES (?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                            insertStmt.setString(1, courseStudentsSessionLink.getStudent().getIdStudent());
                            insertStmt.setInt(2, courseStudentsSessionLink.getSession().getIdSession());
                            int insertedRows = insertStmt.executeUpdate();
                            if (insertedRows == 1) {
                                conn.commit();
                                return true; // session registered successfully
                            }
                        }
                    }
                } else {
                    return false; // session not found
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param courseStudentsSessionLink the DTO for storing the registered session and the student
     * @param operation true = add and false = drop
     * @return
     */
    public boolean registerPendingCourse(CourseStudentsSessionLink courseStudentsSessionLink, boolean operation) {
        String sql = "INSERT INTO courses_pending_list (Students_idStudents, Session_idSession, operation) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseStudentsSessionLink.getStudent().getIdStudent());
            stmt.setInt(2, courseStudentsSessionLink.getSession().getIdSession());
            stmt.setInt(3, operation?1:2);
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

    public int countCoursesByStudentId(String studentId) {
        String sql = "SELECT COUNT(*) FROM courses_students_session_link WHERE Students_idStudents = ?";
        int count = 0;
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public boolean hasRegisteredSession(CourseStudentsSessionLink courseStudentsSessionLink) {
        String sql = "SELECT * FROM courses_students_session_link WHERE Students_idStudents = ? AND Session_idSession = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseStudentsSessionLink.getStudent().getIdStudent());
            stmt.setInt(2, courseStudentsSessionLink.getSession().getIdSession());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean dropSession(CourseStudentsSessionLink courseStudentsSessionLink) {
        String sql = "DELETE FROM courses_students_session_link WHERE Session_idSession = ? AND Students_idStudents = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set parameters
            stmt.setInt(1, courseStudentsSessionLink.getSession().getIdSession());
            stmt.setString(2, courseStudentsSessionLink.getStudent().getIdStudent());

            // Execute the update statement
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Decrement the remaining capacity of the session
                String updateSql = "UPDATE session SET remainingCapacity = session.remainingCapacity + 1 WHERE idSession = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, courseStudentsSessionLink.getSession().getIdSession());
                    updateStmt.executeUpdate();
                }
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

