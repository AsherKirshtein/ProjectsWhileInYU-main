package registrar.model;

import java.util.HashSet;
import java.util.Set;

import edu.yu.registrar.model.Department;

public class Professor extends Employee{
    private Department department;
    private Set<CourseOffering> coursesTaught;

    protected Professor(){
    }
    public Professor(String firstName, String lastName, int id, Department department) {
        super(firstName, lastName, id, Role.PROFESSOR, department.getSchool());
        this.department = department;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public boolean addCourseTaught(CourseOffering co){
        if(this.coursesTaught == null){
            this.coursesTaught = new HashSet<>();
        }
        return this.coursesTaught.add(co);
    }

    public boolean removeCourseTaught(CourseOffering co){
        if(this.coursesTaught == null){
            return false;
        }
        return this.coursesTaught.remove(co);
    }

    public Set<CourseOffering> getCoursesTaught() {
        HashSet<CourseOffering> cos =  new HashSet<>();
        if(this.coursesTaught != null) {
            cos.addAll(this.coursesTaught);
        }
        return cos;
    }
}