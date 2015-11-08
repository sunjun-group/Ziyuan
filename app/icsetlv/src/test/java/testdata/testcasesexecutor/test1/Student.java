package testdata.testcasesexecutor.test1;

import java.util.ArrayList;

public class Student {
	public String name;
	public ArrayList<Teacher> teacherList = new ArrayList<>();

	
	
	public Student(String name) {
		super();
		this.name = name;
	}

	public Student(String name, ArrayList<Teacher> teacherList) {
		super();
		this.name = name;
		this.teacherList = teacherList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Teacher> getTeacherList() {
		return teacherList;
	}

	public void setTeacherList(ArrayList<Teacher> teacherList) {
		this.teacherList = teacherList;
	}

	public void addTeacher(Teacher t){
		teacherList.add(t);
	}
}
