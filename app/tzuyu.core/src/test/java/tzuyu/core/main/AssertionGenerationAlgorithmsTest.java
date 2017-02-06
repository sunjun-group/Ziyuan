/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

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
public class AssertionGenerationAlgorithmsTest extends AbstractTzPackageTest {
	protected AssertionGeneration gen;
	protected AssertionGenerationParams params;

	private static Logger log = LoggerFactory.getLogger(AssertionGenerationAlgorithmsTest.class);

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
		params.setNumberOfTestCases(10);
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

	/**
	 * test part
	 */

	@Test
	public void testAlg2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "2");
		params.setMethodName("calculateScalarProduct");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info("Running time: {}", (endTime - startTime));
	}

	@Test
	public void testAlg3a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "3a");
		params.setMethodName("sumBinaryNumbersCheating");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg3b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "3b");
		params.setMethodName("sumBinaryNumbersBasedOnPowersOfTwo");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg3c() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "3c");
		params.setMethodName("sumBinaryNumbersBasedOnCount");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg5a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "5a");
		params.setMethodName("divideIterative");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg5b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "5b");
		params.setMethodName("divideRecursive");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg6() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "6");
		params.setMethodName("merge");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg7a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "7a");
		params.setMethodName("getRecursive");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg7b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "7b");
		params.setMethodName("getIterative");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg7c() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "7c");
		params.setMethodName("getRecursiveWithCaching");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg8a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "8a");
		params.setMethodName("splitSorting");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg8b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "8b");
		params.setMethodName("splitSwappingIterative");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg8c() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "8c");
		params.setMethodName("splitSwappingRecursive");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg9a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "9a");
		params.setMethodName("getIterative");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg9b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "9b");
		params.setMethodName("getRecursive");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg9c() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "9c");
		params.setMethodName("getTailRecursive");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg10a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "10a");
		params.setMethodName("removeUsingSet");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg10b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "10b");
		params.setMethodName("removeUsingSorting");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg12a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "12a");
		params.setMethodName("moveSorting");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg12b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "12b");
		params.setMethodName("moveUsingTwoPointers");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg13a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "13a");
		params.setMethodName("getUsingQueue");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg13b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "13b");
		params.setMethodName("getWithoutAdditionalDataStructures");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg14a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "14a");
		params.setMethodName("getRecursive");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg14b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "14b");
		params.setMethodName("getIterative");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg15a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "15a");
		params.setMethodName("getRecursive");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg15b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "15b");
		params.setMethodName("getIterative");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg16a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "16a");
		params.setMethodName("getRecursive");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg16b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "16b");
		params.setMethodName("getIterative");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg17a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "17a");
		params.setMethodName("areEqualsRecursive");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg17b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "17b");
		params.setMethodName("areEqualsIterative");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg18a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "18a");
		params.setMethodName("checkRecursive");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg18b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "18b");
		params.setMethodName("checkIterative");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg19() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "19");
		params.setMethodName("get");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg21a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "21a");
		params.setMethodName("getRecursive");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg21b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "21b");
		params.setMethodName("getIterative");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg22a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "22a");
		params.setMethodName("reverseIterative");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg22b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "22b");
		params.setMethodName("reverseRecursive");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg23() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "23");
		params.setMethodName("remove");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg24() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "24");
		params.setMethodName("transform");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg25() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "25");
		params.setMethodName("get");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg26() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "26");
		params.setMethodName("evaluate");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg27() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "27");
		params.setMethodName("reverse");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg28a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "28a");
		params.setMethodName("find");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg28b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "28b");
		params.setMethodName("findLinearComplexityOrder");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg29() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "29");
		params.setMethodName("check");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg30() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "30");
		params.setMethodName("calculate");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg31a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "31a");
		params.setMethodName("findIterative");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg31b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "31b");
		params.setMethodName("findRecursive");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg32() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "32");
		params.setMethodName("evaluate");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg33() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "33");
		params.setMethodName("evaluate");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg34() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "34");
		params.setMethodName("evaluate");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg35() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "35");
		params.setMethodName("evaluate");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg36() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "36");
		params.setMethodName("find");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg37() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "37");
		params.setMethodName("calculate");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg38() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "38");
		params.setMethodName("calculate");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg39() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "39");
		params.setMethodName("multiply");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg40() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "40");
		params.setMethodName("move");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg41() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "41");
		params.setMethodName("go");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg42() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "42");
		params.setMethodName("apply");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg43() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "43");
		params.setMethodName("calculate");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg44() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "44");
		params.setMethodName("find");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg45() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "45");
		params.setMethodName("find");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg46a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "46a");
		params.setMethodName("serialize");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg46b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "46b");
		params.setMethodName("deserialize");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg47() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "47");
		params.setMethodName("calculate");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg48() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "48");
		params.setMethodName("contains");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg49() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "49");
		params.setMethodName("get");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg50a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "50a");
		params.setMethodName("evaluate");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg50b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "50b");
		params.setMethodName("evaluate2");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg51a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "51a");
		params.setMethodName("reverseIterative");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg51b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "51b");
		params.setMethodName("reverseRecursive");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg52a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "52a");
		params.setMethodName("replace");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg52b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "52b");
		params.setMethodName("replace2");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg53a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "53a");
		params.setMethodName("compress");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg53b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "53b");
		params.setMethodName("compressRecursive");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg54() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "54");
		params.setMethodName("rotate");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg55() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "55");
		params.setMethodName("rewrite");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg56() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "56");
		params.setMethodName("check");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg57a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "57a");
		params.setMethodName("remove");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg57b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "57b");
		params.setMethodName("remove2");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg58a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "58a");
		params.setMethodName("find");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg58b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "58b");
		params.setMethodName("find2");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg58c() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "58c");
		params.setMethodName("find3");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg59() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "59");
		params.setMethodName("delete");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg60() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "60");
		params.setMethodName("split");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg61a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "61a");
		params.setMethodName("sum");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg61b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "61b");
		params.setMethodName("sumReverse");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg62a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "62a");
		params.setMethodName("checkReversing");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg62b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "62b");
		params.setMethodName("checkIterative");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg64a() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "64a");
		params.setMethodName("get");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg64b() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "64b");
		params.setMethodName("get2");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg72() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "72");
		params.setMethodName("find");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg74() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "74");
		params.setMethodName("sort");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg75() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "75");
		params.setMethodName("sort");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg76() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "76");
		params.setMethodName("sort");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

	@Test
	public void testAlg79() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("algorithms-master", "79");
		params.setMethodName("sort");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();

		log.info((endTime - startTime) + "");
	}

}
