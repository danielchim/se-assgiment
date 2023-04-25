package model;

import context.ContextState;
import entity.Courses;
import entity.Teachers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseModel {
    String dbUrl;
    String dbUsername;
    String dbPassword;

    public CourseModel() {
        ContextState.loadProperties();
        dbUrl = ContextState.getProperty("datasource.url");
        dbUsername = ContextState.getProperty("datasource.username");
        dbPassword = ContextState.getProperty("datasource.password");
    }
    public List<Courses> getAllCourses() {

        String sql = "SELECT c.idCourses, c.name, c.enabled, c.course_code, t.name AS teacherName " +
                "FROM courses c INNER JOIN teachers t ON c.Teachers_idTeachers = t.idTeachers";
        List<Courses> courses = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("idCourses");
                    String name = rs.getString("name");
                    boolean enabled = rs.getBoolean("enabled");
                    String teacherName = rs.getString("teacherName");
                    String courseCode = rs.getString("course_code");
                    Courses course = new Courses();
                    Teachers teacher = new Teachers();
                    teacher.setName(teacherName);
                    course.setCourseCode(courseCode);
                    course.setIdCourses(id);
                    course.setName(name);
                    course.setEnabled(enabled);
                    course.setTeacher(teacher);
                    courses.add(course);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public Courses getCourseByCode(String courseCode) {
        ContextState.loadProperties();
        String dbUrl = ContextState.getProperty("datasource.url");
        String dbUsername = ContextState.getProperty("datasource.username");
        String dbPassword = ContextState.getProperty("datasource.password");
        String sql = "SELECT c.idCourses, c.name, c.enabled, c.course_code,t.name AS teacherName " +
                "FROM courses c INNER JOIN teachers t ON c.Teachers_idTeachers = t.idTeachers " +
                "WHERE c.course_code = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    boolean enabled = rs.getBoolean("enabled");
                    String teacherName = rs.getString("teacherName");
                    courseCode = rs.getString("course_code");
                    Courses course = new Courses();
                    Teachers teacher = new Teachers();
                    teacher.setName(teacherName);
                    course.setCourseCode(courseCode);
                    course.setName(name);
                    course.setEnabled(enabled);
                    course.setTeacher(teacher);
                    return course;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
