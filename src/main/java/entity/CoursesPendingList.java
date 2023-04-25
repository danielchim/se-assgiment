package entity;

public class CoursesPendingList {
    int idCoursesStudentsLink;
    Students student;
    Session session;

    boolean operation;

    public boolean isOperation() {
        return operation;
    }

    public void setOperation(boolean operation) {
        this.operation = operation;
    }

    public int getIdCoursesStudentsLink() {
        return idCoursesStudentsLink;
    }

    public void setIdCoursesStudentsLink(int idCoursesStudentsLink) {
        this.idCoursesStudentsLink = idCoursesStudentsLink;
    }

    public Students getStudent() {
        return student;
    }

    public void setStudent(Students student) {
        this.student = student;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
