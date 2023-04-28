package registrar.query;

import java.util.Set;

import registrar.model.Course;

//Returns the classes based on the requirements by major

public class MajorsSearch extends Query {
    private String school;
    private String name;
    private Set<Course> requiredCourses;

    public MajorsSearch(int userID) {
        super(userID);
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Course> getRequiredCourses() {
        return requiredCourses;
    }

    public void setRequiredCourses(Set<Course> requiredCourses) {
        this.requiredCourses = requiredCourses;
    }
}
