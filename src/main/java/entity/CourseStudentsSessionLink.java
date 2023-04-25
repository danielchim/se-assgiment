package entity;

public class CourseStudentsSessionLink {
    int idCoursesStudentsLink;
    Students student;
    Session session;

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
