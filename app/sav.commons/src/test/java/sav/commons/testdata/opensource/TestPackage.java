/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.commons.testdata.opensource;

import static sav.common.core.utils.ResourceUtils.appendPath;
import static sav.commons.testdata.TestDataConstants.TEST_DATA_FOLDER;
import static sav.commons.testdata.TestDataConstants.TEST_PROJECT_LIBS;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author LLT
 *
 */
public class TestPackage {
	private String projectPath;
	public String libsPath;
	public final List<String> analyzingClasses = new ArrayList<String>();
	public final List<String> testClasses = new ArrayList<String>();
	public final List<String> classPaths = new ArrayList<String>();
	public final List<String> failTestMethods = new ArrayList<String>();
	public static final TestPackage JAVA_PARSER = javaParser();
	public static final TestPackage GOOGLE_COLLECTIONS = googleCollections();
	public static final TestPackage APACHE_COMMONS_MATH = apacheCommonsMath();
	
	private TestPackage(String prjName) {
		projectPath = appendPath(TEST_DATA_FOLDER, prjName);
		libsPath = appendPath(projectPath, TEST_PROJECT_LIBS); 
		if (!new File(libsPath).exists()) {
			libsPath = null;
		}
	}
	
	private static TestPackage apacheCommonsMath() {
		return createNew("apache-commons-math")
				.prjClassPaths("target/classes")
				.prjClassPaths("target/test-classes")
				.testClasses(
						"org.apache.commons.math3.analysis.differentiation.DerivativeStructureTest");
	}

	private static TestPackage googleCollections() {
		return createNew("google-collections")
				.prjClassPaths("build/eclipse-bin")
				.testClasses("")
				.testClassMethods("");
	}

	private static TestPackage createNew(String prjName) {
		return new TestPackage(prjName);
	}

	private static TestPackage javaParser() {
		return createNew("javaparser")
				.prjClassPaths("bin")
				.analyzingClasses("japa.parser.ast.visitor.DumpVisitor",
						"japa.parser.JavaParser",
						"japa.parser.ASTParser",
						"japa.parser.ast.test.Helper")
				.testClasses("japa.parser.ast.test.TestDumper")
				.testClassMethods("japa.parser.ast.test.TestDumper.testCommentsIssue46");
	}

	private TestPackage testClassMethods(String... failTestMethods) {
		this.failTestMethods.addAll(Arrays.asList(failTestMethods));
		return this;
	}

	private TestPackage analyzingClasses(String... analyzingClasses) {
		this.analyzingClasses.addAll(Arrays.asList(analyzingClasses));
		return this;
	}

	private TestPackage testClasses(String... testClasses) {
		this.testClasses.addAll(Arrays.asList(testClasses));
		return this;
	}

	private TestPackage prjClassPaths(String... classPathFragments) {
		for (String classPath : classPathFragments) {
			classPaths.add(appendPath(projectPath, classPath));
		}
		return this;
	}
	
}
