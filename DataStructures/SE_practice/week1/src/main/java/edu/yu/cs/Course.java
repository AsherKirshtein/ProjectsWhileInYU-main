package edu.yu.cs;

import java.util.List;

public class Course
{
    private Integer startTime;
    private Integer endTime;
    private String courseName;
    private Integer courseID;
    private Integer credits;
    private CourseType courseType;
    private List<DaysOfWeek> days;

    private List<Students> students;
    private Teacher teacher;
    
    public Course(String name, Integer id, int start, int end, int credits)
    {

    }

    public String getCourseName()
    {

    }

    public int getCourseID()
    {

    }
    public void adjustTime(int start, int end)
    {

    }
    
    public void assignTeacher(Teacher teacher)
    {

    }
    public void assignTeacher(int teacherID)
    {

    }

    public List<Students> getStudentsinCourse()
    {

    }

    public void changeCourseType(CourseType courseType)
    {

    }

    public void addStudent(Students student)
    {

    }
    public void addStudent(int studentID)
    {

    }

}
