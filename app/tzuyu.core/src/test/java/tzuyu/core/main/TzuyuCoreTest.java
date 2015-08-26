/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.util.List;

import org.junit.Before;

import sav.common.core.Constants;
import sav.common.core.utils.CollectionUtils;
import sav.commons.TestConfiguration;
import faultLocalization.SpectrumBasedSuspiciousnessCalculator.SpectrumAlgorithm;

/**
 * @author LLT
 * 
 */
public abstract class TzuyuCoreTest extends AbstractTzTest {
	protected TzuyuCore app;
	
	@Before
	public void setup() throws Exception {
		List<String> projectClasspath = testContext.getAppData().getAppClasspaths();
		projectClasspath.add(TestConfiguration.getTarget("slicer.javaslicer"));
//		projectClasspath.add(config.getJunitLib());
		projectClasspath.add(TestConfiguration.getTzAssembly(Constants.SAV_COMMONS_ASSEMBLY));
		appData.setSuspiciousCalculAlgo(SpectrumAlgorithm.OCHIAI);
		app = new TzuyuCore(testContext, appData);
	}
	
	protected FaultLocateParams initFaultLocateParams(String testingClassName, String methodName, String verificationMethod,
			List<String> testingPackages, List<String> junitClassNames, boolean useSlicer) {
		FaultLocateParams params = new FaultLocateParams();
		params.setTestingClassNames(CollectionUtils.listOf(testingClassName));
		params.setMethodName(methodName);
		params.setVerificationMethod(verificationMethod);
		params.setTestingPkgs(testingPackages);
		params.setJunitClassNames(junitClassNames);
		params.setUseSlicer(useSlicer);
		params.setGenTest(true);
		params.setRunMutation(true);
		params.setMachineLearningEnable(true);
		params.setValueRetrieveLevel(3);
		return params;
	}

	
//	@Test
//	public void runSearchInSortingMatrix1() throws Exception {
//		faultLocalization(SimplePrograms.class.getName(),
//				SearchInSortingMatrix1Test.class.getName());
//	}
//	
//	@Test
//	public void runMatchString() throws Exception {
//		faultLocalization(SimplePrograms.class.getName(),
//				MatchStringTest.class.getName());
//	}
//	
//	@Test
//	public void runFindInRotatedSortedArray() throws Exception {
//		faultLocalization(SimplePrograms.class.getName(),
//				FindInRotatedSortedArrayTest.class.getName());
//	}
//	
//	@Test
//	public void runSampleProgram() throws Exception {
//		faultLocalization(CoverageSample.class.getName(),
//				CoverageSampleTest.class.getName());
//	}
//	
//	
//	@Test
//	public void runDuplicateNumber() throws Exception {
//		faultLocalization(SimplePrograms.class.getName(),
//				DuplicateNumberTest.class.getName());
//	}
//	
//	@Test
//	public void runReverseWord() throws Exception {
//		faultLocalization(SimplePrograms.class.getName(),
//				ReverseWordTest.class.getName());
//	}
//	
//	@Test
//	public void test2() throws Exception{
//		TzuyuCore app = new TzuyuCore(testContext, appData);
//		List<String> testingClasses = new ArrayList<String>();
//		testingClasses.add(SimplePrograms.class.getName());
//		testingClasses.add(Integer.class.getName());
//		List<String> junitClassNames = new ArrayList<String>();
////		junitClassNames.add(DuplicateNumberTest.class.getName());
//		junitClassNames.add(ReverseWordTest.class.getName());
//		app.faultLocalization(testingClasses, junitClassNames, false);
//	}
//	
//	@Test
//	public void runPalindrome2() throws Exception {
//		faultLocalization(SimplePrograms.class.getName(),
//				Palindrome2Test.class.getName());
//	}
//	
//	@Test
//	public void runPalindrome1() throws Exception {
//		faultLocalization(SimplePrograms.class.getName(),
//				Palindrome1Test.class.getName());
//	}
//	
//	public void faultLocalization(String program, String junit) throws Exception {
//		List<String> testingClasses = new ArrayList<String>();
//		testingClasses.add(program);
//		List<String> junitClassNames = new ArrayList<String>();
//		junitClassNames.add(junit);
//		app.faultLocalization(testingClasses, junitClassNames, false);
////		app.faultLocalization2(testingClasses, Arrays.asList("sav.commons.testdata.simplePrograms"), junitClassNames, false);
//	}
	
	
	
	
}
