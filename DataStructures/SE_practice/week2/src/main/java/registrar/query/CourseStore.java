package registrar.query;

import java.util.List;

import registrar.model.Course;

// Week 2: Due Tuesday, May 4, 11:59pm
// The task: Do steps 4 and 5 listed below. You are NOT implementing the logic; you are only writing the public API and JUnit tests.
// The requirements:
// 1) use the code given to you as you starting point - the data model in the model package, and the queries in the query package.
// 2) define a public API that defines how users submit a query and gets results.
//    -You have been given subclasses of Query that a user can use to capture what he is searching for.
//    -If a field is left blank/null in a Query object, that indicates that the user doesn't care about that field for the search.
//    -If a user searches for something that he may view but not update, the public API should return an instance of one of the Immutable subclasses you were given
//    -If a user searches for something that he may only view PART of, the public API should return an instance of one of the Immutable subclasses with only the data he is allowed to use actually present; the data he can't view should be null.
//    -If a user searches for something he may not view at all, the public API should return null
//    -You don't have to deal with the permutation of a user being able to only write/change part of the object he searched for - we assume he can either write all of what he was given or nothing of what he was given.
// 3) pursuant to what we discussed in our lecture on testing, once the public API has been defined, write JUnit tests that will check if an eventual implementation of the public API meets the requirements listed above.

// You should not spend much more than the 6 hours of class this is replacing on this assignment. You need the rest of your available time for stage 5 of the semester project!

// I plan to be available on Zoom during class hours so you can ask any questions you have.

public class CourseStore 
{
    CoursesSearch coursesSearch;
    CourseOfferingsSearch courseOfferingsSearch;
    DepartmentsSearch departmentsSearch;
    MajorsSearch majorsSearch;
    ProfessorsSearch professorsSearch;
    SchoolsSearch schoolsSearch;
    StudentsSearch studentsSearch;
    
    //Idea: maybe overload every message to just be named search so that they don't have to worry about different names for different searches
    public CourseStore()
    {

    }
    
    //Search through the query and find all of the courses they are searching for
    //Sets our search variable above 
     //returns a list of courses
    public List<Course> courseSearch(CoursesSearch courses)
    {
        return null;
    }
    //Gets all of the courses taken in that semester
    //Sets our search variable above 
    //returns a list of courses
    public List<Course> getAvailableCourses(CourseOfferingsSearch courses)
    {
        return null;
    }
    //Gets all the courses within a certain department
    //Sets our search variable above 
    //returns a list of courses
    public List<Course> getCourseByDepartment(DepartmentsSearch courses)
    {
        return null;
    }
    //Gets all of the courses the major requires you to take
    //Sets our search variable above 
    //returns a list of courses
    public List<Course> getCourseByMajor(MajorsSearch courses)
    {
        return null;
    }
    //Gets all the courses a certain professor is teaching
    //Sets our search variable above 
    //returns a list of courses
    public List<Course> getCourseByProfessor(ProfessorsSearch courses)
    {
        return null;
    }
    //Gets all the courses within a certain school
    //Sets our search variable above 
    //returns a list of courses
    public List<Course> getCourseBySchool(SchoolsSearch courses)
    {
        return null;
    }
    //Gets all the courses based on what the student is taking: the students schedule
    //Sets our search variable above 
    //returns a list of courses
    public List<Course> getCourseByStudents(StudentsSearch courses)
    {
        return null;
    }
    //Compares all of the values in each of the set values and if the course matches all of the values in the searched requirements we add it to the list
    public List<Course> totalSearch()
    {
        return null;
    }
}


