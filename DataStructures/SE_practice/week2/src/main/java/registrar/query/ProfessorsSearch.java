package registrar.query;

import java.util.Set;

import registrar.model.CourseOffering;

//Returns the classes with the desired Professor

public class ProfessorsSearch extends Query {
    private String school;
    private String department;
    private String firstName;
    private String lastName;
    private Set<CourseOffering> coursesTaught;

    public ProfessorsSearch(int userID) {
        super(userID);
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastname() {
        return this.lastName;
    }

    public void setLastname(String lastName) {
        this.lastName = lastName;
    }
}
