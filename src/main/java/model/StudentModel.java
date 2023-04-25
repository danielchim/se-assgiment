package model;

import context.ContextState;
import entity.Students;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentModel {
    String dbUrl;
    String dbUsername;
    String dbPassword;

    public StudentModel() {
        ContextState.loadProperties();
        dbUrl = ContextState.getProperty("datasource.url");
        dbUsername = ContextState.getProperty("datasource.username");
        dbPassword = ContextState.getProperty("datasource.password");
    }

    public void create(Students student) {

        String sql = "INSERT INTO students (idStudents, name, gender) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getIdStudent());
            stmt.setString(2, student.getName());
            stmt.setBoolean(3, student.isGender());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Students student) {
        String sql = "UPDATE students SET name = ?, gender = ? WHERE idStudents = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getName());
            stmt.setBoolean(2, student.isGender());
            stmt.setString(3, student.getIdStudent());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String studentId) {
        String sql = "DELETE FROM students WHERE idStudents = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Students findById(String studentId) {
        String sql = "SELECT * FROM students WHERE idStudents = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Students student = new Students();
                    student.setIdStudent(rs.getString("idStudents"));
                    student.setName(rs.getString("name"));
                    student.setGender(rs.getBoolean("gender"));
                    return student;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getStudentRegisteredSession(String studentCode) {
        String sql = "SELECT COUNT(*) FROM courses_students_session_link WHERE Students_idStudents = ?";
        int count = 0;
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentCode);
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

}
