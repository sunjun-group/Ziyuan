/* Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icsetlv.variable.VarNameVisitor.VarNameCollectionMode;
import libsvm.svm;
import libsvm.svm_print_interface;
import sav.common.core.SystemVariablesUtils;
import sav.commons.testdata.opensource.TestPackage;

/**
 * @author LLT
 *
 */
public class AssertionGenerationSVCOMPTest extends AbstractTzPackageTest {
	protected AssertionGeneration gen;
	protected AssertionGenerationParams params;

	private static Logger log = LoggerFactory.getLogger(AssertionGenerationSVCOMPTest.class);

	@Before
	public void setup() {
		super.setup();

		String jarPath = SystemVariablesUtils.updateSavJunitJarPath(appData);
		appData.addClasspath(jarPath);

		gen = new AssertionGeneration(context);

		params = new AssertionGenerationParams();
		params.setMachineLearningEnable(true);
		params.setRankToExamine(10);
		params.setRunMutation(false);
		params.setUseSlicer(true);
		params.setValueRetrieveLevel(3);
		params.setNumberOfTestCases(20);
		params.setVarNameCollectionMode(VarNameCollectionMode.FULL_NAME);

		svm.svm_set_print_string_function(new svm_print_interface() {

			@Override
			public void print(String s) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void genAssertion(TestPackage testPkg) throws Exception {
		prepare(testPkg);

		params.setTestingClassNames(testingClassNames);
		params.setTestingPkgs(testingPackages);
		params.setJunitClassNames(junitClassNames);

		// params.setJunitClassNames(new ArrayList<String>());
		gen.genAssertion(params);
	}
	
	public void genAssertion(TestPackage testPkg, String className) throws Exception {
		prepare(testPkg);

		params.setTestingClassNames(testingClassNames);
		params.setTestingPkgs(testingPackages);
		
		for (int i = 1; i <= 20; i++) {
			params.setJunitClassNames(Arrays.asList("test." + className + i));
			gen.genAssertion(params);
		}
		
		gen.genAssertion(params);
	}

	/**
	 * test part
	 */

	@Test
	public void testExpLoop() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("svcomp", "ExpLoop");
		params.setMethodName("main1");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	 
	@Test
	public void testInvSqrt() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("svcomp", "InvSqrt");
		params.setMethodName("main1");
		genAssertion(testPkg, "InvSqrt");

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSqrtBiNewton() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("svcomp", "SqrtBiNewton");
		params.setMethodName("main1");
		genAssertion(testPkg, "SqrtBiNewton");

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSqrtConst() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("svcomp", "SqrtConst");
		params.setMethodName("main1");
		genAssertion(testPkg, "SqrtConst");

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSqrtInterval() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("svcomp", "SqrtInterval");
		params.setMethodName("main1");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSqrtNewton() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("svcomp", "SqrtNewton");
		params.setMethodName("main1");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSqrtPseudo() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("svcomp", "SqrtPseudo");
		params.setMethodName("main1");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSquare() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("svcomp", "Square");
		params.setMethodName("main1");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testZonotopeLoose() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("svcomp", "ZonotopeLoose");
		params.setMethodName("main1");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testZonotopeTight() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("svcomp", "ZonotopeTight");
		params.setMethodName("main1");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
}