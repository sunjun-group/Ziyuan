/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tools.testdatapackage;

import java.io.IOException;
import java.util.Map.Entry;

import sav.commons.testdata.opensource.TestPackage;
import sav.commons.testdata.opensource.TestPackage.TestDataColumn;
import tools.commons.ClassAppender;

/**
 * @author LLT
 * read from testdata.csv, generate corresponding test for each defined package,
 * generated code will be appended to the current junit test class.
 */
public class PkgTestGenerator extends ClassAppender {

	public static void main(String[] args) {
		try {
			PkgTestGenerator generator = new PkgTestGenerator();
			generator.appendJavaFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected String getGeneratedContent() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, TestPackage> entry : TestPackage.getAllTestData()
				.entrySet()) {
			String projName = entry.getValue().getValue(TestDataColumn.PROJECT_NAME);
			String bugNo = entry.getValue().getValue(TestDataColumn.BUG_NUMBER);
			String key = entry.getKey().replace("-", "")
					.replace(":", "");
			sb.append(String.format("	\n	@Test\n	public void test%s() throws Exception {\n		TestPackage testPkg = TestPackage.getPackage(\"%s\", \"%s\");\n		setUseSlicer(true);\n		runTest(testPkg);\n	}", 
					key, projName,
					bugNo));
//			sb.append(String.format("	\n	@Test\n	public void test%sv1() throws Exception {\n		TestPackage testPkg = TestPackage.getPackage(\"%s\", \"%s\");\n		fixture.useSlicer(false);\n		runTest1(testPkg);\n	}", 
//					key, projName,
//					bugNo));
			sb.append("\n");
//			sb.append(String.format("	\n	@Test\n	public void test%sv2() throws Exception {\n		TestPackage testPkg = TestPackage.getPackage(\"%s\", \"%s\");\n		runTest2(testPkg);\n	}", 
//					key, projName,
//					bugNo));
//			sb.append("\n\n");
		}
		return sb.toString();
	}

	@Override
	protected String getClassPath() {
		return TRUNK + "app/tzuyu.core/src/test/java/tzuyu/core/main/FixturePackageLocalTest.java";
	}
	
}
