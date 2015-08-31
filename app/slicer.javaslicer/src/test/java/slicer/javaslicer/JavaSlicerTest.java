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
import sav.commons.testdata.assertion.TestInput;
import sav.commons.testdata.assertion.TestInput1;
import sav.commons.testdata.opensource.TestPackage;
import sav.commons.testdata.opensource.TestPackage.TestDataColumn;
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
		slicer.setVmConfig(vmConfig);
	}
	
	public void testJodaTimeIssue194() throws Exception {
		testClassMethods = Arrays.asList("org.joda.time.format.TestIssue194.test");
		run(TestPackage.getPackage("joda-time", "194"),
				new BreakPoint("org.joda.time.format.TestIssue194", 
						"test", 36));
	}
	
	public void testJodaTimeIssue187() throws Exception {
		testClassMethods = Arrays.asList("org.joda.time.issues.TestIssue187.test");
		run(TestPackage.getPackage("joda-time", "187"),
				new BreakPoint("org.joda.time.format.PeriodFormatter", 
						"print", 241));
	}
	
	public void testJavaParserIssue46() throws Exception {
		slicer.setFiltering(null, Arrays.asList("japa.parser"));
		run(Arrays.asList(new BreakPoint(
				"java.lang.String", "concat", 32)));
	}
	
	public void run(TestPackage pkg, BreakPoint bkp) throws Exception {
		run(pkg, Arrays.asList(bkp));
	}
	
	public void run(TestPackage pkg, List<BreakPoint> bkps) throws Exception {
		updateSystemClasspath(pkg.getClassPaths());
		vmConfig.addClasspaths(pkg.getClassPaths());
		vmConfig.addClasspaths(pkg.getLibFolders());
		slicer.setFiltering(pkg.getValues(TestDataColumn.ANALYZING_CLASSES),
				pkg.getValues(TestDataColumn.ANALYZING_PACKAGES));
		run(bkps);
	}
	
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
		run(Arrays.asList(new BreakPoint(
				"org.apache.commons.lang3.SampleProgramTest", "test5", 53)));
	}
	
	@Test
	public void testSampleProgram() throws Exception {
		String targetClass = SamplePrograms.class.getName();
		String testClass = SampleProgramTest.class.getName();
		BreakPoint bkp2 = new BreakPoint(testClass, "test2", 26);
		List<BreakPoint> breakpoints = Arrays.asList(bkp2);
		analyzedClasses = Arrays.asList(targetClass);
		testClassMethods = JunitUtils.extractTestMethods(Arrays
				.asList(testClass));
		run(breakpoints);
	}
	
	@Test
	public void testTestInput() throws Exception {
		String targetClass = TestInput.class.getName();
		String testClass = TestInput1.class.getName();
		BreakPoint bkp2 = new BreakPoint(targetClass, "foo", 6);
		List<BreakPoint> breakpoints = Arrays.asList(bkp2);
		analyzedClasses = Arrays.asList(targetClass);
		testClassMethods = JunitUtils.extractTestMethods(Arrays
				.asList(testClass));
		run(breakpoints);
	}
	
	@Test
	public void testSampleProgramEx() throws Exception {
		String targetClass = SamplePrograms.class.getName();
		String testClass = SampleProgramTest.class.getName();
		BreakPoint bkp2 = new BreakPoint(testClass, "test6", 62);
		List<BreakPoint> breakpoints = Arrays.asList(bkp2);
		analyzedClasses = Arrays.asList(targetClass);
		testClassMethods = Arrays.asList(testClass + ".test6");
		run(breakpoints);
	}
	
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
		run(Arrays.asList(new BreakPoint(
				"org.apache.commons.lang3.AnnotationUtilsTest", "testToString", 507)));
	}
	
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
		if (result.isEmpty()) {
			System.out.println("EMPTY RESULT!!");
		}
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
		slicer.slice("/tmp/javaSlicer.trace", breakpoints, new StopTimer("test"), Arrays.asList("faultLocaliation.sample.SampleProgramTest.test1"));
	}
}
