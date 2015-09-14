/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import sav.commons.TestConfiguration;
import sav.commons.testdata.opensource.TestPackage;
import sav.commons.testdata.opensource.TestPackage.TestDataColumn;
import sav.strategies.dto.BreakPoint;

/**
 * @author LLT
 *
 */
public class JavaSlicerPackageTest extends AbstractJavaSlicerTest {
	
	@Override
	public void setup() throws Exception {
		TestPackage.loadTestData(TestConfiguration.getEtcResources("testdata-javaslicer.csv"));
		super.setup();
	}

	public void run(TestPackage pkg, BreakPoint bkp) throws Exception {
		run(pkg, Arrays.asList(bkp));
	}
	
	public void run(TestPackage pkg, List<BreakPoint> bkps) throws Exception {
		updateSystemClasspath(pkg.getClassPaths());
		appClasspath = initAppClasspath(pkg);
		slicer.setFiltering(pkg.getValues(TestDataColumn.ANALYZING_CLASSES),
				pkg.getValues(TestDataColumn.ANALYZING_PACKAGES));
		slicer.slice(appClasspath, bkps, testClassMethods);
	}
	
	@Test
	public void testJodaTimeIssue194() throws Exception {
		testClassMethods = Arrays.asList("org.joda.time.issues.TestIssue194.test");
		run(TestPackage.getPackage("joda-time", "194"),
				new BreakPoint("org.joda.time.issues.TestIssue194", 
						"test", 36));
	}
	
	@Test
	public void testJodaTimeIssue187() throws Exception {
		testClassMethods = Arrays.asList("org.joda.time.issues.TestIssue187.test");
		run(TestPackage.getPackage("joda-time", "187"),
				new BreakPoint("org.joda.time.format.PeriodFormatter", 
						"print", 241));
	}
	
	@Test
	public void testCommonsLangS1() throws Exception {
		testClassMethods = Arrays.asList("org.apache.commons.lang3.SampleProgramTest.test5");
		run(TestPackage.getPackage("commons-lang", "s1"), new BreakPoint(
				"org.apache.commons.lang3.SampleProgramTest", "test5", 53));
	}
	
	@Test
	public void testCommonsLangS2() throws Exception {
		testClassMethods = Arrays.asList("org.apache.commons.lang3.AnnotationUtilsTest.testToString");
		run(TestPackage.getPackage("commons-lang", "s2"), new BreakPoint("org.apache.commons.lang3.AnnotationUtilsTest", "testToString", 507));
	}
	
}
