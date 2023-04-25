package controller;

import entity.CourseStudentsSessionLink;
import entity.CoursesPendingList;
import entity.Session;
import entity.Students;
import java.util.AbstractMap.SimpleEntry;
import model.PendingCourseModel;
import model.SessionModel;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the controller for handling pending course add/drop requests.
 */

public class PendingCourseController {

    PendingCourseModel pendingCourseModel;
    SessionModel sessionModel;

    public PendingCourseController() {
        pendingCourseModel = new PendingCourseModel();
        sessionModel = new SessionModel();
    }
    /**
     * Retrieves all pending requests in the system.
     *
     * @return A list of all pending add/drop course requests.
     */
    public List<CoursesPendingList> getAllPendingRequests(){
        return pendingCourseModel.getAllPendingRequests();
    }

    /**
     * Registers a pending course request in the system.
     *
     * @param pendingRequest The pending request to be registered.
     * @return True if the request was registered successfully, false otherwise.
     */
    public boolean registerPendingSession(CoursesPendingList pendingRequest) {
        return pendingCourseModel.registerPendingCourse(pendingRequest);
    }
    /**
     * Removes a pending request from the system.
     *
     * @param pendingRequest The pending request to be removed.
     * @return True if the request was removed successfully, false otherwise.
     */

    public boolean removePendingRequest(CoursesPendingList pendingRequest) {
        // Remove a pending request from the courses_pending_list table
        return pendingCourseModel.deletePendingRequest(pendingRequest);
    }
    /**
     * Processes all pending requests in the system, attempting to add/drop them again.
     * If successful, the request is removed from the pending list.
     */
    public List<CoursesPendingList> getPendingRequestsBySessionId(int sessionId) {
        // Retrieve all pending requests for a specific session
        return pendingCourseModel.getPendingRequestsBySessionId(sessionId);

    }

    /**
     * Finds matching add/drop requests in the pending list to enable swapping.
     *
     * @return A list of pairs of matching add/drop requests.
     */
    public void processPendingRequests() {
        List<CoursesPendingList> pendingRequests = getAllPendingRequests();

        for (CoursesPendingList request : pendingRequests) {
            int sessionId = request.getSession().getIdSession();
            String studentId = request.getStudent().getIdStudent();
            int operation = request.isOperation()?1:0;

            boolean success = false;

            if (operation == 1) {
                Session session = new Session();
                session.setIdSession(sessionId);
                Students students = new Students();
                students.setIdStudent(studentId);
                CourseStudentsSessionLink link = new CourseStudentsSessionLink();
                link.setStudent(students);
                link.setSession(session);
                success = sessionModel.registerSession(link);
            } else {
                Session session = new Session();
                session.setIdSession(sessionId);
                Students students = new Students();
                students.setIdStudent(studentId);
                CourseStudentsSessionLink link = new CourseStudentsSessionLink();
                link.setStudent(students);
                link.setSession(session);
                success = sessionModel.dropSession(link);
            }

            if (success) {
                pendingCourseModel.deletePendingRequest(request);
            }
        }
    }


    public List<SimpleEntry<CoursesPendingList, CoursesPendingList>> findMatchingRequests() {
        List<CoursesPendingList> pendingAddRequests = new ArrayList<>();
        List<CoursesPendingList> pendingDropRequests = new ArrayList<>();
        List<SimpleEntry<CoursesPendingList, CoursesPendingList>> matchingRequests = new ArrayList<>();

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
                    SimpleEntry<CoursesPendingList, CoursesPendingList> match = new SimpleEntry<>(addRequest, dropRequest);
                    matchingRequests.add(match);
                }
            }
        }

        return matchingRequests;
    }

    /**
     * Performs an add/drop swap operation between two students for a specific session.
     *
     * @param sessionId The ID of the session to perform the swap in.
     * @param request1  The first student's add/drop request.
     * @param request2  The second student's add/drop request.
     * @return True if the swap was successful, false otherwise.
     */
    public boolean addDropSwap(int sessionId, CoursesPendingList request1, CoursesPendingList request2) {
        String studentId1 = request1.getStudent().getIdStudent();
        String studentId2 = request2.getStudent().getIdStudent();
        Session session = new Session();
        session.setIdSession(sessionId);
        Students students = new Students();
        students.setIdStudent(studentId1);
        CourseStudentsSessionLink link1 = new CourseStudentsSessionLink();
        link1.setSession(session);
        link1.setStudent(students);
        CourseStudentsSessionLink link2 = new CourseStudentsSessionLink();
        Students students2 = new Students();
        students2.setIdStudent(studentId2);
        link2.setSession(session);
        link2.setStudent(students2);
        boolean success1 = sessionModel.dropSession(link1);
        boolean success2 = sessionModel.registerSession(link2);
        return success1 && success2;
    }

    /**
     * Finds matching add/drop requests in the pending list, performs swaps, and removes successful requests.
     */
    public void findMatchingRequestsAndSwap() {
        List<SimpleEntry<CoursesPendingList, CoursesPendingList>> matchingRequests = pendingCourseModel.findMatchingRequests();
        for (SimpleEntry<CoursesPendingList, CoursesPendingList> entry : matchingRequests) {
            CoursesPendingList request1 = entry.getKey();
            CoursesPendingList request2 = entry.getValue();
            int sessionId = request1.getSession().getIdSession();
            boolean success = addDropSwap(sessionId, request1, request2);
            if (success) {
                pendingCourseModel.deletePendingRequest(request1);
                pendingCourseModel.deletePendingRequest(request2);
            }
        }
    }
}
