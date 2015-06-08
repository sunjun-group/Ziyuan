/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.util.ArrayList;
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
import faultLocalization.SpectrumBasedSuspiciousnessCalculator.SpectrumAlgorithm;

/**
 * @author LLT
 * 
 */
public class TzuyuCoreTest extends AbstractTzTest {
	private TzuyuCore app;
	
	@Before
	public void setup() throws Exception {
		List<String> projectClasspath = testContext.getAppData().getAppClasspaths();
		projectClasspath.add(TestConfiguration.getTarget("slicer.javaslicer"));
//		projectClasspath.add(config.getJunitLib());
		projectClasspath.add(TestConfiguration.getTzAssembly(Constants.SAV_COMMONS_ASSEMBLY));
		appData.setSuspiciousCalculAlgo(SpectrumAlgorithm.OCHIAI);
		app = new TzuyuCore(testContext, appData);
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
		TzuyuCore app = new TzuyuCore(testContext, appData);
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
		List<String> testingClasses = new ArrayList<String>();
		testingClasses.add(program);
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add(junit);
		app.faultLocalization(testingClasses, junitClassNames, false);
//		app.faultLocalization2(testingClasses, Arrays.asList("sav.commons.testdata.simplePrograms"), junitClassNames, false);
	}
	
	@Test
	public void testNoLoop() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.calculator.CalculatorTestPassed");
		junitClassNames.add("sav.commons.testdata.calculator.CalculatorTestFailed");
		app.faultLocate("sav.commons.testdata.calculator.Calculator", "getSum", "validateGetSum",
				null, junitClassNames, false);
	}

	@Test
	@Ignore("For testing external codes")
	public void testExternalNoLoop() throws Exception {
		final List<String> appClasspaths = appData.getAppClasspaths();
		appClasspaths.add("/Users/npn/dev/projects/data/target/test-classes");
		appClasspaths.add("/Users/npn/dev/projects/data/target/classes");
		appClasspaths.add(TestConfiguration.getTzAssembly(Constants.TZUYU_JAVASLICER_ASSEMBLY));
		
		appData.setAppSrc("/Users/npn/dev/projects/data/src/main/java");
		appData.setAppTarget("/Users/npn/dev/projects/data/target/test-classes");

		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("simpleTestData.CalculatorTestFailed");
		junitClassNames.add("simpleTestData.CalculatorTestPassed");
		app.faultLocate("simpleTestData.Calculator", "getMax", "validateGetMax",
				null, junitClassNames, false);
	}

	@Test
	public void testWhileLoopWith2Bugs() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.search1.SearchIndexEqualValueTest");
		app.faultLocate("sav.commons.testdata.search1.SearchIndexEqualValue", "search", "validate",
				null, junitClassNames, false);
	}
	
	@Test
	public void testArray() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.calculator.GetSumArrayTest");
		app.faultLocate("sav.commons.testdata.calculator.Calculator", "getSumArray", "validateGetSumArray",
				null, junitClassNames, false);
	}
	
	@Test
	public void testSimpleForLoop() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.calculator.LoopInvariantTest");
		app.faultLocate("sav.commons.testdata.calculator.Calculator", "loopInvariant", "validateLoopInvariant",
				null, junitClassNames, false);
	}
	
	@Test
	public void testClass() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.calculator.SumTest");
		app.faultLocate("sav.commons.testdata.calculator.Sum", "getSum", "validateGetSum",
				null, junitClassNames, false);
	}
	
	@Test
	public void testLoopInvariant() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.loopinvariant.LoopTest");
		app.faultLocate("sav.commons.testdata.loopinvariant.Loop", "testLoop", "validateTestLoop",
				null, junitClassNames, false);
	}
	
	@Test
	@Ignore("For testing with Guava codes")
	public void testGuava1() throws Exception {
		//  b2c6fb17ab4fbac8cd4014fe68799166f015a2c3
		final List<String> appClasspaths = appData.getAppClasspaths();
		appClasspaths.add("/Users/npn/dev/projects/guava/guava/target/classes");
		appClasspaths.add("/Users/npn/dev/projects/guava/guava-tests/test");
		appClasspaths.add("/Users/npn/dev/projects/guava/guava-tests/target/test-classes");
		appClasspaths.add(TestConfiguration.getTzAssembly(Constants.TZUYU_JAVASLICER_ASSEMBLY));
//		appData.setAppSrc("/Users/npn/dev/projects/guava/guava-tests/test");
		appData.setAppSrc("/Users/npn/dev/projects/guava/guava/src");
//		appData.setAppTarget("/Users/npn/dev/projects/guava/guava-tests/target/test-classes");
		appData.setAppTarget("/Users/npn/dev/projects/guava/guava/target/classes");

		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("com.google.common.cache.AbstractCacheTest");
		app.faultLocate("com.google.common.cache.AbstractCache", "getAllPresent", "dummyValidate",
				null, junitClassNames, false);
	}

	@Test
	@Ignore("For testing with Oryx codes")
	public void testOryx() throws Exception {
//		--> 11e2e168a179ad670e528b0f45abf60bf3a5abda
//		--> apply patch 14e8a456744bf12e829452381983f1ab9dff92ac
//		--> revert changes on StringLongMapping.java
		final List<String> appClasspaths = appData.getAppClasspaths();
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/classes");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/src/test/java");
		appClasspaths.add("/Users/npn/dev/projects/oryx/common/target/test-classes");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/test-classes");
		appClasspaths.add(TestConfiguration.getTzAssembly(Constants.TZUYU_JAVASLICER_ASSEMBLY));
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/test-classes");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/commons-math3-3.2.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/oryx-common-0.4.0-SNAPSHOT-tests.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/config-1.2.0.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/oryx-common-0.4.0-SNAPSHOT.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/guava-11.0.2.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/hamcrest-core-1.3.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/jaxb-impl-2.2.6.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/jsr305-1.3.9.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/junit-4.11.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/pmml-manager-1.0.22.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/pmml-model-1.0.22.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/pmml-schema-1.0.22.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/slf4j-api-1.7.6.jar");
		appClasspaths.add("/Users/npn/dev/projects/oryx/als-common/target/dependencyLibs/slf4j-jdk14-1.7.6.jar");
		appData.setAppSrc("/Users/npn/dev/projects/oryx/als-common/src/main/java");
		appData.setAppTarget("/Users/npn/dev/projects/oryx/als-common/target/classes");

		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("com.cloudera.oryx.als.common.StringLongMappingTest");
		app.faultLocate("com.cloudera.oryx.als.common.StringLongMapping", "toLong", "validate",
				null, junitClassNames, false);
	}
	
	
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
