package javacocoWrapper;

import java.util.ArrayList;

public class Program {
	public static void main(final String[] args) throws Exception {
		String testingClassName1 = SamplePrograms.class.getName();
		String testingClassName2 = SampleProgramTest.class.getName();
		String testingClassName3 = RequestExecution.class.getName();
		
		ArrayList<String> testingClassNames = new ArrayList<String>();
		testingClassNames.add(testingClassName1);
		testingClassNames.add(testingClassName2);
		testingClassNames.add(testingClassName3);
		
		JavaCoCo javacoco = new JavaCoCo(System.out);
		javacoco.run(testingClassNames, SampleProgramTest.class);
	}

}
