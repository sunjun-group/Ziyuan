/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sav.common.core.Constants;
import sav.common.core.SavException;
import sav.common.core.utils.JunitUtils;
import sav.common.core.utils.StopTimer;
import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.commons.testdata.SampleProgramTest;
import sav.commons.testdata.SamplePrograms;
import sav.commons.testdata.opensource.TestPackage;
import sav.commons.utils.TestConfigUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.vm.VMConfiguration;


/**
 * @author LLT
 *
 */
public class JavaSlicerTest extends AbstractTest {
	private JavaSlicer slicer;
	private List<String> analyzedClasses;
	private List<String> testClassMethods;
	private VMConfiguration vmConfig;
	
	@Before
	public void setup() {
		slicer = new JavaSlicer();
		vmConfig = initVmConfig();
		vmConfig.addClasspath(TestConfiguration
				.getTzAssembly(Constants.TZUYU_JAVASLICER_ASSEMBLY));
		slicer.setTracerJarPath(TestConfigUtils.getTracerLibPath());
		slicer.setVmConfig(vmConfig);
	}
	
	@Test
	public void testJavaParserIssue46() throws Exception {
		setupTestPackage(TestPackage.JAVA_PARSER);
		slicer.setFiltering(null, Arrays.asList("japa.parser"));
		run(Arrays.asList(new BreakPoint(
				"java.lang.String", "concat", 32)));
//				"japa.parser.ast.test.TestDumper", "testCommentsIssue46", 46)));
	}
	
	public void setupTestPackage(TestPackage pkg) throws Exception {
		updateSystemClasspath(pkg.classPaths);
		testClassMethods = pkg.failTestMethods;
		vmConfig.addClasspaths(pkg.classPaths);
	}
	
	@Test
	public void testCommonsLang2() throws Exception {
		String prjFolder = TestConfiguration.TESTCASE_BASE + "/commons-lang";
		String projClasses = prjFolder + "/trunk/target/classes";
		String projTestClasses = prjFolder  + "/trunk/target/test-classes";
		String libs = prjFolder + "/bin/libs";
		TestConfigUtils.addToSysClassLoader(new File(projClasses));
		TestConfigUtils.addToSysClassLoader(new File(projTestClasses));
		analyzedClasses = Arrays.asList("org.apache.commons.lang3.SamplePrograms");
		testClassMethods = Arrays.asList("org.apache.commons.lang3.SampleProgramTest.test5");
		vmConfig.addClasspath(projClasses);
		vmConfig.addClasspath(projTestClasses);
		vmConfig.addClasspath(libs);
//		run(Arrays.asList(new BreakPoint(
//				"org.apache.commons.lang3.AnnotationUtils", "toString", 210)));
		run(Arrays.asList(new BreakPoint(
				"org.apache.commons.lang3.SampleProgramTest", "test5", 53)));
	}
	
	@Test
	public void testSampleProgram() throws Exception {
		String sampleProgramsClassName = SamplePrograms.class.getName();
		String sampleProgramTestClassName = SampleProgramTest.class.getName();
		BreakPoint bkp1 = new BreakPoint(sampleProgramTestClassName, "test5", 53);
		BreakPoint bkp2 = new BreakPoint(sampleProgramsClassName, "Max", 10);
		List<BreakPoint> breakpoints = Arrays.asList(bkp1);
		analyzedClasses = Arrays.asList(sampleProgramsClassName);
		testClassMethods = JunitUtils.extractTestMethods(Arrays
				.asList(sampleProgramTestClassName));
		run(breakpoints);
	}
	
	@Test
	public void testCommonsLang() throws Exception {
		String prjFolder = TestConfiguration.TESTCASE_BASE + "/commons-lang";
		String projClasses = prjFolder + "/trunk/target/classes";
		String projTestClasses = prjFolder  + "/trunk/target/test-classes";
		String libs = prjFolder + "/bin/libs";
		TestConfigUtils.addToSysClassLoader(new File(projClasses));
		TestConfigUtils.addToSysClassLoader(new File(projTestClasses));
		analyzedClasses = Arrays.asList("org.apache.commons.lang3.AnnotationUtils");
		testClassMethods = Arrays.asList("org.apache.commons.lang3.AnnotationUtilsTest.testToString");
		vmConfig.addClasspath(projClasses);
		vmConfig.addClasspath(projTestClasses);
		vmConfig.addClasspath(libs);
//		run(Arrays.asList(new BreakPoint(
//				"org.apache.commons.lang3.AnnotationUtils", "toString", 210)));
		run(Arrays.asList(new BreakPoint(
				"org.apache.commons.lang3.AnnotationUtilsTest", "testToString", 507)));
	}
	
	@Test
	public void testOnTestdata() throws Exception {
		String jtopasSrc = TestConfigUtils.getConfig("jtopas.src");
		analyzedClasses = Arrays.asList(
				"de.susebox.java.io.ExtIOException",
				"de.susebox.java.lang.ExtIndexOutOfBoundsException",
				"de.susebox.java.util.InputStreamTokenizer",
				"de.susebox.jtopas.PluginTokenizer",
				"de.susebox.java.util.AbstractTokenizer");
		testClassMethods = Arrays.asList("de.susebox.java.util.TestInputStreamTokenizer.testLinkParsing");
		String jtopasTest = TestConfigUtils.getConfig("jtopas.test");
		TestConfigUtils.addToSysClassLoader(new File(jtopasSrc));
		TestConfigUtils.addToSysClassLoader(new File(jtopasTest));
		vmConfig.addClasspath(jtopasTest);
		vmConfig.addClasspath(jtopasSrc);
		run(Arrays.asList(new BreakPoint(
				"de.susebox.java.util.AbstractTokenizer", "isKeyword", 772)));
	}

	private void run(List<BreakPoint> breakpoints) throws SavException, IOException,
			InterruptedException, ClassNotFoundException {
		slicer.setFiltering(analyzedClasses, null);
		List<BreakPoint> result = slicer.slice(breakpoints, testClassMethods);
		for (BreakPoint bkp : result) {
			System.out.println(bkp);
		}
	}

	@Test
	public void testInnerSlice() throws InterruptedException, SavException {
		List<BreakPoint> breakpoints = Arrays.asList(new BreakPoint(
				"faultLocaliation.sample.SampleProgramTest", "test5", 53),
				new BreakPoint("faultLocaliation.sample.SampleProgram", 
						"Max", 26));
		slicer.slice("/tmp/javaSlicer.trace", breakpoints, new StopTimer("test"));
	}
}
