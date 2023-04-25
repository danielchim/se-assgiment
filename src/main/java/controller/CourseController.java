package controller;

import entity.Courses;
import model.CourseModel;

import java.util.List;

/**
 * Course controller only supports reterive data, no add and remove is supported atm
 */

public class CourseController {
    CourseModel courseModel;

    public CourseController() {
        courseModel = new CourseModel();
    }

    public List<Courses> getAllCourses() {
        return courseModel.getAllCourses();
    }

    public Courses getCourseByCode(String courseCode) {
        return courseModel.getCourseByCode(courseCode);
    }

}
