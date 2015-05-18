/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import sav.common.core.Constants;
import sav.commons.TestConfiguration;
import sav.commons.testdata.SampleProgramTest;
import sav.commons.testdata.SamplePrograms;
import sav.commons.testdata.simplePrograms.DuplicateNumberTest;
import sav.commons.testdata.simplePrograms.FindInRotatedSortedArrayTest;
import sav.commons.testdata.simplePrograms.MatchStringTest;
import sav.commons.testdata.simplePrograms.Palindrome1Test;
import sav.commons.testdata.simplePrograms.Palindrome2Test;
import sav.commons.testdata.simplePrograms.ReverseWordTest;
import sav.commons.testdata.simplePrograms.SearchInSortingMatrix1Test;
import sav.commons.testdata.simplePrograms.SimplePrograms;
import tzuyu.core.inject.ApplicationData;

/**
 * @author LLT
 * 
 */
public class TzuyuCoreTest extends AbstractTzTest {

	@Before
	public void setup() throws UnsupportedEncodingException {
		List<String> projectClasspath = testContext.getAppData().getAppClasspaths();
		projectClasspath.add(
				TestConfiguration.getTarget("slicer.javaslicer"));
		projectClasspath.add(config.getJunitLib());
	}
	
	@Test
	public void runSearchInSortingMatrix1() throws Exception {
		faultLocalization(SimplePrograms.class.getName(),
				SearchInSortingMatrix1Test.class.getName());
	}
	
	@Test
	public void runMatchString() throws Exception {
		faultLocalization(SimplePrograms.class.getName(),
				MatchStringTest.class.getName());
	}
	
	@Test
	public void runFindInRotatedSortedArray() throws Exception {
		faultLocalization(SimplePrograms.class.getName(),
				FindInRotatedSortedArrayTest.class.getName());
	}
	
	@Test
	public void runSampleProgram() throws Exception {
		faultLocalization(SamplePrograms.class.getName(),
				SampleProgramTest.class.getName());
	}
	
	
	@Test
	public void runDuplicateNumber() throws Exception {
		faultLocalization(SimplePrograms.class.getName(),
				DuplicateNumberTest.class.getName());
	}
	
	@Test
	public void runReverseWord() throws Exception {
		faultLocalization(SimplePrograms.class.getName(),
				ReverseWordTest.class.getName());
	}
	
	@Test
	public void test2() throws Exception{
		TzuyuCore app = new TzuyuCore(testContext);
		List<String> testingClasses = new ArrayList<String>();
		testingClasses.add(SimplePrograms.class.getName());
		testingClasses.add(Integer.class.getName());
		List<String> junitClassNames = new ArrayList<String>();
//		junitClassNames.add(DuplicateNumberTest.class.getName());
		junitClassNames.add(ReverseWordTest.class.getName());
		app.faultLocalization(testingClasses, junitClassNames, false);
	}
	
	@Test
	public void runPalindrome2() throws Exception {
		faultLocalization(SimplePrograms.class.getName(),
				Palindrome2Test.class.getName());
	}
	
	@Test
	public void runPalindrome1() throws Exception {
		faultLocalization(SimplePrograms.class.getName(),
				Palindrome1Test.class.getName());
	}
	
	public void faultLocalization(String program, String junit) throws Exception {
		TzuyuCore app = new TzuyuCore(testContext);
		List<String> testingClasses = new ArrayList<String>();
		testingClasses.add(program);
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add(junit);
		app.faultLocalization(testingClasses, junitClassNames, false);
//		app.faultLocalization2(testingClasses, Arrays.asList("sav.commons.testdata.simplePrograms"), junitClassNames, false);
	}
	
	@Test
	public void whenSpectrumAndMachineLearningUsed1() throws Exception{
		TzuyuCore app = new TzuyuCore(testContext);
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.calculator.CalculatorTestPassed");
		junitClassNames.add("sav.commons.testdata.calculator.CalculatorTestFailed");
		app.doSpectrumAndMachineLearning("sav.commons.testdata.calculator.Calculator", "getSum", "validateGetSum",
				null, junitClassNames, false);
	}

	@Test
	@Ignore("For testing external codes")
	public void whenSpectrumAndMachineLearningUsed2() throws Exception {
		final ApplicationData appData = testContext.getAppData();
		final List<String> appClasspaths = appData.getAppClasspaths();
		appClasspaths.add("/Users/npn/dev/projects/data/target/test-classes");
		appClasspaths.add("/Users/npn/dev/projects/data/target/classes");
		appClasspaths.add(TestConfiguration.getTzAssembly(Constants.TZUYU_JAVASLICER_ASSEMBLY));
		
		appData.setAppSrc("/Users/npn/dev/projects/data/src/main/java");
		appData.setAppTarget("/Users/npn/dev/projects/data/target/test-classes");

		final TzuyuCore app = new TzuyuCore(testContext);
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("simpleTestData.CalculatorTestFailed");
		junitClassNames.add("simpleTestData.CalculatorTestPassed");
		app.doSpectrumAndMachineLearning("simpleTestData.Calculator", "getMax", "validateGetMax",
				null, junitClassNames, false);
	}

	@Test
	public void whenSpectrumAndMachineLearningUsed3() throws Exception{
		TzuyuCore app = new TzuyuCore(testContext);
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.search1.SearchIndexEqualValueTest");
		app.doSpectrumAndMachineLearning("sav.commons.testdata.search1.SearchIndexEqualValue", "search", "validate",
				null, junitClassNames, false);
	}
	
//	@Test
//	@Ignore("For testing with Guava codes")
//	public void testGuava1() throws Exception {
//		//  b2c6fb17ab4fbac8cd4014fe68799166f015a2c3
//		final ApplicationData appData = testContext.getAppData();
//		final List<String> appClasspaths = appData.getAppClasspaths();
//		appClasspaths.add("/Users/npn/dev/projects/guava/guava/target/classes");
//		appClasspaths.add("/Users/npn/dev/projects/guava/guava-tests/test");
//		appClasspaths.add("/Users/npn/dev/projects/guava/guava-tests/target/test-classes");
//		appClasspaths.add(TestConfiguration.getTzAssembly(Constants.TZUYU_JAVASLICER_ASSEMBLY));
//		appData.setAppSrc("/Users/npn/dev/projects/guava/guava-tests/test");
//		appData.setAppTarget("/Users/npn/dev/projects/guava/guava-tests/target/test-classes");
//
//		final TzuyuCore app = new TzuyuCore(testContext);
//		List<String> testingClasses = Arrays.asList("com.google.common.cache.AbstractCache");
//		List<String> junitClassNames = Arrays.asList("com.google.common.cache.AbstractCacheTest");
//		List<String> testPackages = Arrays.asList("com.google.common.hash");
//		app.doSpectrumAndMachineLearning(testingClasses, null, junitClassNames, false);
//	}
//
//	@Test
//	@Ignore("For testing with Guava codes")
//	public void testGuava2() throws Exception {
//		//  6b3dd12bb8960885afedb1807660d923fe3bfce8
//		final ApplicationData appData = testContext.getAppData();
//		final List<String> appClasspaths = appData.getAppClasspaths();
//		appClasspaths.add("/Users/npn/dev/projects/guava/guava/target/classes");
//		appClasspaths.add("/Users/npn/dev/projects/guava/guava-tests/test");
//		appClasspaths.add("/Users/npn/dev/projects/guava/guava-tests/target/test-classes");
//		appClasspaths.add(TestConfiguration.getTzAssembly(Constants.TZUYU_JAVASLICER_ASSEMBLY));
//		appData.setAppSrc("/Users/npn/dev/projects/guava/guava-tests/test");
//		appData.setAppTarget("/Users/npn/dev/projects/guava/guava-tests/target/test-classes");
//
//		final TzuyuCore app = new TzuyuCore(testContext);
//		List<String> testingClasses = Arrays.asList("com.google.common.hash.HashCode");
//		List<String> testPackages = Arrays.asList("com.google.common.hash");
//		List<String> junitClassNames = Arrays.asList("com.google.common.hash.HashCodeTest");
//		app.doSpectrumAndMachineLearning(testingClasses, null, junitClassNames, false);
//	}
//	
//	@Test
//	@Ignore("For testing with Guava codes")
//	public void testGuava3() throws Exception {
//		//  dc931f9621ab1865e67fbf026590b6371e4a19f3
//		final ApplicationData appData = testContext.getAppData();
//		final List<String> appClasspaths = appData.getAppClasspaths();
//		appClasspaths.add("/Users/npn/dev/projects/guava/guava/target/classes");
//		appClasspaths.add("/Users/npn/dev/projects/guava/guava-tests/test");
//		appClasspaths.add("/Users/npn/dev/projects/guava/guava-tests/target/test-classes");
//		appClasspaths.add(TestConfiguration.getTzAssembly(Constants.TZUYU_JAVASLICER_ASSEMBLY));
//		appData.setAppSrc("/Users/npn/dev/projects/guava/guava-tests/test");
//		appData.setAppTarget("/Users/npn/dev/projects/guava/guava-tests/target/test-classes");
//
//		final TzuyuCore app = new TzuyuCore(testContext);
//		List<String> testingClasses = Arrays.asList("com.google.common.collect.ImmutableTable");
//		List<String> testPackages = Arrays.asList("com.google.common.collect");
//		List<String> junitClassNames = Arrays.asList("com.google.common.collect.ImmutableTableTest");
//		app.doSpectrumAndMachineLearning(testingClasses, null, junitClassNames, false);
//	}
}
