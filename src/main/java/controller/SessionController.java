package controller;

import entity.CourseStudentsSessionLink;
import entity.Session;
import model.SessionModel;

import java.util.List;
/**
 * This class provides the controller for handling sessions for each courses
 */
public class SessionController {
    SessionModel sessionModel;
    /**
     * Initializes a new SessionController with a new SessionModel.
     */
    public SessionController() {
        this.sessionModel = new SessionModel();
    }
    /**
     * Retrieves all sessions for a specific course code.
     *
     * @param courseCode The course code to retrieve sessions for.
     * @return A list of sessions associated with the specified course code.
     */
    public List<Session> getSessionsByCourseCode(String courseCode) {
        return sessionModel.getSessionsByCourseCode(courseCode);
    }

    /**
     * Retrieves a session by its session code.
     *
     * @param sessionCode The session code to search for.
     * @return The session with the specified session code, or null if not found.
     */
    public Session getSessionBySessionCode(String sessionCode) {
        return sessionModel.getSessionBySessionCode(sessionCode);
    }
    /**
     * Registers a student for a session if the student is not already registered.
     *
     * @param courseStudentsSessionLink The link between the student and the session to be registered.
     * @return True if the registration was successful, false otherwise.
     */
    public boolean registerSession(CourseStudentsSessionLink courseStudentsSessionLink){
        boolean bIsSuccess = false;
        if(!sessionModel.hasRegisteredSession(courseStudentsSessionLink)){
            bIsSuccess = sessionModel.registerSession(courseStudentsSessionLink);
        }
        return bIsSuccess;
    }
    /**
     * Checks if a student has reached the maximum allowed number of course registrations.
     *
     * @param idStudent The student ID to check the registration count for.
     * @return True if the student has reached the maximum number of course registrations, false otherwise.
     */
    public boolean isStudentReachedMaxRegistrationAmount(String idStudent){
        return sessionModel.countCoursesByStudentId(idStudent) == 3;
    }
    /**
     * Drops a student from a session.
     *
     * @param courseStudentsSessionLink The link between the student and the session to be dropped.
     * @return True if the drop was successful, false otherwise.
     */
    public boolean dropSession(CourseStudentsSessionLink courseStudentsSessionLink){
        return sessionModel.dropSession(courseStudentsSessionLink);
    }
}
