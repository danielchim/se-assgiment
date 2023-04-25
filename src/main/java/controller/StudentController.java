package controller;

import entity.Students;
import model.StudentModel;

public class StudentController {
    StudentModel studentModel;

    /**
     * Initializes a new StudentController with a new StudentModel.
     */
    public StudentController() {
        this.studentModel = new StudentModel();
    }
    /**
     * Retrieves a student by their student ID.
     *
     * @param studentCode The student ID to search for.
     * @return The student with the specified student ID, or null if not found.
     */
    public Students getStudentByStudentId(String studentCode){
        return studentModel.findById(studentCode);
    }
    /**
     * Retrieves the number of sessions a student is registered for.
     *
     * @param studentCode The student ID to count registered sessions for.
     * @return The number of sessions the student is registered for.
     */
    public int getStudentRegisteredSession(String studentCode){
        return studentModel.getStudentRegisteredSession(studentCode);
    }
}
