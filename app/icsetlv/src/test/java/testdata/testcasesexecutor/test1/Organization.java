package testdata.testcasesexecutor.test1;

import java.util.ArrayList;


public class Organization {
	private ArrayList<Student> studentList = new ArrayList<>();
	private ArrayList<Teacher> teacherList = new ArrayList<>();
	
	public Organization(){
		Student s1 = new Student("s1");
		Student s2 = new Student("s2");
		Student s3 = new Student("s3");
		
		Teacher t1 = new Teacher("t1");
		Teacher t2 = new Teacher("t2");
		
		s1.addTeacher(t1);
		t1.addStudent(s1);
		
		s2.addTeacher(t1);
		t1.addStudent(s2);
		
		s2.addTeacher(t2);
		t2.addStudent(s2);
		
		s3.addTeacher(t2);
		t2.addStudent(s3);
		
		studentList.add(s1);
		studentList.add(s2);
		studentList.add(s3);
		
		teacherList.add(t1);
		teacherList.add(t2);
	}

	public ArrayList<Student> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<Student> studentList) {
		this.studentList = studentList;
	}

	public ArrayList<Teacher> getTeacherList() {
		return teacherList;
	}

	public void setTeacherList(ArrayList<Teacher> teacherList) {
		this.teacherList = teacherList;
	}
	
	
}
