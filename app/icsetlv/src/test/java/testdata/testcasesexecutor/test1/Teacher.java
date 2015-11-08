package testdata.testcasesexecutor.test1;

import java.util.ArrayList;

public class Teacher {

	public Teacher(String name, ArrayList<Student> studentList) {
		super();
		this.name = name;
		this.studentList = studentList;
	}
	
	

	public Teacher(String name) {
		super();
		this.name = name;
	}

	public void addStudent(Student s){
		studentList.add(s);
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Student> getStudentList() {
		return studentList;
	}

	public void setStudentList(ArrayList<Student> studentList) {
		this.studentList = studentList;
	}

	public String name;
	public ArrayList<Student> studentList = new ArrayList<>();
}
