package registrar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import edu.yu.registrar.model.Department;
import registrar.model.Course;
import registrar.model.CourseOffering;
import registrar.model.CourseOffering.Semester;
import registrar.model.Professor;
import registrar.model.School;
import registrar.query.CourseOfferingsSearch;
import registrar.query.CourseStore;
import registrar.query.CoursesSearch;
import registrar.query.DepartmentsSearch;
import registrar.query.MajorsSearch;
import registrar.query.ProfessorsSearch;
import registrar.query.SchoolsSearch;
import registrar.query.StudentsSearch;

public class SearchTest
{
    private School yc = new School("Yeshiva College");
    private School morningProgram = new School("Morning Program");
    private Department compSci= new Department("Computer Science", yc);
    private Department writing = new Department("Writing", yc);
    private Department bmp = new Department("BMP", morningProgram);
    private Department math = new Department("Math", yc);
    private Department ibc = new Department("IBC", morningProgram); 
    private Course firstYearWriting = new Course("First-Year-Writing", writing, 1);
    private Course bmpshiur = new Course("Rav Berman Shiur", bmp, 2);
    private Course dataStructures = new Course("Data Structures", compSci, 3);
    private Course linearAlgebra = new Course("Linear Algebra", math, 4);
    private Course mathForCompSci = new Course("Math for Computer Science", compSci, 5);
    private Course introToCompSci = new Course("Intro to Computer Science", compSci, 6);
    private Course calculus1 = new Course("Claculus 1", math, 7);
    private Course prayer = new Course("Explanation of Prayer", ibc, 8);
    private Course contemporaryTopics = new Course("Contemporary Topics of Jewish Law", ibc, 9);
    private Course chaburaSeminar = new Course("Chabura Seminar", ibc, 10);
    private Professor diament = new Professor("Judah", "Diament", 101, compSci);
    private Professor berman = new Professor("Eitan Moshe", "Berman", 102, bmp);
    private Professor wymore = new Professor("Ben", "Wymore", 103, compSci);
    private Professor musser = new Professor("Maxwell", "Musser", 104, math);
    private Professor kelly = new Professor("Van", "Kelly", 105, compSci);
    private Professor puretz = new Professor("David", "Puretz", 106, writing);
    private Professor chen = new Professor("Wenxiong", "Chen", 107, math);
    private Professor rofeh = new Professor("Beny", "Rofeh", 108, ibc);
    private Professor blech = new Professor("Benjamin", "Blech", 109, ibc);
    private Professor djavaheri = new Professor("Mordechai", "Djavaheri", 110, ibc);
    
    @Test 
    public void proffesorSearchTest()
    {
        ProfessorsSearch dia = new ProfessorsSearch(101);
        dia.setDepartment("compSci");
        dia.setFirstName("Judah");
        dia.setLastname("Diament");
        dia.setSchool("Yeshiva College");
        assertEquals(dia.getDepartment(),"compSci");
        assertEquals(dia.getFirstName(),"Judah");
        assertEquals(dia.getLastname(),"Diament");
        assertEquals(dia.getSchool(),"Yeshiva College");
    }
    @Test
    public void studentSearchTest()
    {
        StudentsSearch search = new StudentsSearch(100);
        search.setFirstName("Asher");
        search.setLastname("Kirshtein");
        search.setMajor("compSci");
        GregorianCalendar graduation = new GregorianCalendar(2022, 6, 25);
        search.setGraduationDate(graduation);
        CourseOffering intro = new CourseOffering(this.introToCompSci, 2020, Semester.FALL);
        CourseOffering calc = new CourseOffering(this.calculus1, 2020, Semester.FALL);
        CourseOffering chaburah = new CourseOffering(this.chaburaSeminar, 2020, Semester.FALL);
        CourseOffering contempTopics = new CourseOffering(this.contemporaryTopics, 2020, Semester.FALL);
        CourseOffering prayer = new CourseOffering(this.prayer, 2020, Semester.FALL);
        Set<CourseOffering> coursesTaken = new HashSet<>();
        coursesTaken.add(intro);
        coursesTaken.add(calc);
        coursesTaken.add(chaburah);
        coursesTaken.add(contempTopics);
        coursesTaken.add(prayer);
        search.setCoursesTaken(coursesTaken);
        assertEquals(search.getFirstName(), "Asher");
        assertEquals(search.getLastname(), "Kirshtein");
        assertEquals(search.getCoursesTaken(), coursesTaken);
        assertEquals(search.getMajor(), "compSci");
        assertEquals(search.getGraduationDate(), graduation);
        assertEquals(search.getUserID(), 100);
    }
    @Test
    public void schoolsSearchTest()
    {
        SchoolsSearch search = new SchoolsSearch(100);
        search.setDeanFirstName("Bernie");
        search.setDeanLastName("Madolf");
        search.setName("SYMS");
        assertEquals("SYMS", search.getName());
        assertEquals("Bernie", search.getDeanFirstName());
        assertEquals("Madolf", search.getDeanLastName());
    }
    @Test 
    public void majorsSearchTest()
    {
        MajorsSearch search = new MajorsSearch(100);
        search.setSchool("Yeshiva College");
        search.setName("CompSci");
        Set<Course> courses = new HashSet<>();
        courses.add(this.introToCompSci);
        courses.add(this.calculus1);
        courses.add(this.dataStructures);
        courses.add(this.linearAlgebra);
        courses.add(this.mathForCompSci);
        search.setRequiredCourses(courses);
        assertEquals("Yeshiva College", search.getSchool());
        assertEquals("CompSci", search.getName());
        assertEquals(courses, search.getRequiredCourses());
    }
    @Test
    public void departementsSearchTest() 
    {
        DepartmentsSearch search = new DepartmentsSearch(100);
        search.setName("CompSci");
        search.setSchool("Yeshiva College");
        assertEquals(search.getName(), "CompSci");
        assertEquals(search.getSchool(), "Yeshiva College");  
    }
    @Test
    public void coursesSearchTest()
    {
        CoursesSearch search = new CoursesSearch(1000);
        search.setName("Calculus");
        search.setSchool("Yeshiva College");
        assertEquals(search.getName(), "Calculus");
        assertEquals(search.getSchool(), "Yeshiva College");  
    }
    @Test
    public void courseOfferingsSearchTest()
    {
        CourseOfferingsSearch search = new CourseOfferingsSearch(1000);
        search.setName("Calculus");
        search.setSchool("Yeshiva College");
        assertEquals(search.getName(), "Calculus");
        assertEquals(search.getSchool(), "Yeshiva College");  
    }
    @Test
    public void totalSearchTest()
    {
        CourseStore banner = new CourseStore();
        CourseOfferingsSearch search = new CourseOfferingsSearch(1000);
        search.setName("Calculus");
        search.setSchool("Yeshiva College");

        assertEquals(1, banner.getAvailableCourses(search).size());

        DepartmentsSearch departments = new DepartmentsSearch(2000);
        departments.setName("CompSci");
        departments.setSchool("Yeshiva College");

        assertEquals(1, banner.getCourseByDepartment(departments));

        assertEquals(0, banner.totalSearch().size());
    }
}
