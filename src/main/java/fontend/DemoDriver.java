package fontend;

import context.ContextState;
import controller.*;
import entity.*;

import java.util.List;
import java.util.Scanner;

/**
 * This is a demo view to make sure the basic function is working, but i am not sure will the matching add drop is going to work
 */

public class DemoDriver {
    private Scanner kb;
    private User currentUser;
    private AuthController authController;
    private CourseController courseController;
    private SessionController sessionController;
    private StudentController studentController;
    private PendingCourseController pendingCourseController;


    public DemoDriver() {
        kb = new Scanner(System.in);
        authController = new AuthController();
        courseController = new CourseController();
        sessionController = new SessionController();
        studentController = new StudentController();
        pendingCourseController = new PendingCourseController();
        currentUser = null;
    }

    public void login() {
        log("Please enter your username:");
        String username = kb.nextLine();
        log("Please enter your password:");
        String password = kb.nextLine();
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        currentUser = authController.auth(user);
        if (currentUser != null) {
            log("Login successful. Welcome!");
        } else {
            log("Invalid username or password. Please try again.");
        }
    }


    public void main() {
        while (true) {
            log("Enter a command:");
            log("1. View course information");
            log("2. Register for a course");
            log("3. Drop a course");
            log("4. Add/drop a course");

            int command = kb.nextInt();
            kb.nextLine(); // Consume newline character

            switch (command) {
                case 1:
                    // View course information
                    List<Courses> courses = courseController.getAllCourses();
                    for (Courses course : courses) {
                        System.out.printf("%-10s %-25s %-10s %-25s\n", "idCourses", "name", "teacherName", "Course Code");
                        System.out.printf("%-10d %-25s %-10s %-25s\n", course.getIdCourses(), course.getName(), course.getTeacher().getName(), course.getCourseCode());
                    }
                    break;
                case 2:
                    // Register for a course
                    Students student;
                    boolean bIsPending = false;
                    log("Enter the course code: ");
                    String courseCode = kb.nextLine();
                    List<Session> sessions = sessionController.getSessionsByCourseCode(courseCode);
                    // TODO: Print the table
                    for (Session session : sessions) {
                        System.out.printf("%-10s %-25s %-10s %-25s\n", "idCourses", "name", "teacherName", "Course Code");
                        System.out.printf("%-10s %-25s %-10s %-25s\n", session.getSessionCode(), session.getWeekTime(), session.getTime(), session.getCapacity());
                    }
                    log("Enter the session Code: ");
                    String sessionCode = kb.nextLine();
                    Session session = sessionController.getSessionBySessionCode(sessionCode);
                    log(String.valueOf(session.getRemainingCapacity()));
                    int remain = session.getRemainingCapacity();
                    if (remain <= 0) {
                        log("The desired course was full. He will be assigned into pending list.");
                        bIsPending = true;
                    }
                    do {
                        log("Enter the student ID: ");
                        String studentId = kb.nextLine();
                        student = studentController.getStudentByStudentId(studentId);

                        if (student == null) {
                            log("Can't find student, please try again");
                        } else if (sessionController.isStudentReachedMaxRegistrationAmount(student.getIdStudent())) {
                            log("This student reached maximum registration account");
                        }
                    } while (student == null);
                    if (bIsPending) {
                        CoursesPendingList registration = new CoursesPendingList();
                        registration.setStudent(student);
                        registration.setSession(session);
                        registration.setOperation(true);
                        if (pendingCourseController.registerPendingSession(registration)) {
                            log("Success");
                        } else {
                            log("Failed");
                        }
                    } else {
                        CourseStudentsSessionLink registration = new CourseStudentsSessionLink();
                        registration.setStudent(student);
                        registration.setSession(session);
                        if (sessionController.registerSession(registration)) {
                            log("Success");
                        } else {
                            log("Failed");
                        }
                    }
                    break;
                case 3:
                    // Drop a course
                    // KEEP IN MIND THAT NO ADD/DROP WAS IMPLEMENTED IN HERE.
                    System.out.print("Enter the course code: ");
                    String courseCodeInput = kb.nextLine();
                    List<Session> targetSessions = sessionController.getSessionsByCourseCode(courseCodeInput);
                    for (Session session2 : targetSessions) {
                        System.out.printf("%-10s %-25s %-10s %-25s\n", "Session Code", "Week Time", "Time", "Capacity");
                        System.out.printf("%-10s %-25s %-10s %-25s\n", session2.getSessionCode(), session2.getWeekTime(), session2.getTime(), session2.getCapacity());
                    }
                    log("Enter the session Code: ");
                    String sessionCodeInput = kb.nextLine();
                    Session session3 = sessionController.getSessionBySessionCode(sessionCodeInput);
                    Students student2;
                    do {
                        log("Enter the student ID: ");
                        String studentId = kb.nextLine();
                        student2 = studentController.getStudentByStudentId(studentId);
                        if (student2 == null) {
                            log("Can't find student, please try again");
                        }
                    } while (student2 == null);
                    CourseStudentsSessionLink courseStudentsSessionLink = new CourseStudentsSessionLink();
                    courseStudentsSessionLink.setSession(session3);
                    courseStudentsSessionLink.setStudent(student2);
                    boolean isDropped = sessionController.dropSession(courseStudentsSessionLink);
                    if (isDropped) {
                        log("Session is dropped successfully.");
                    }
                    break;

//                case 4:
//                    // Add/drop a course
//                    System.out.print("Enter the course code to add: ");
//                    String courseToAdd = kb.nextLine();
//                    System.out.print("Enter the session ID to add: ");
//                    String sessionToAdd = kb.nextLine();
//                    System.out.print("Enter the course code to drop: ");
//                    String courseToDrop = kb.nextLine();
//                    courseController.addDropCourse(user, courseToAdd, sessionToAdd, courseToDrop);
//                    break;
                default:
                    log("Invalid command.");
            }
        }
    }

    public static void log(String msg) {
        System.out.println(msg);
    }


}
