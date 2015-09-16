package tzuyu.core.main;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import faultLocalization.SpectrumBasedSuspiciousnessCalculator.SpectrumAlgorithm;
import sav.common.core.Constants;
import sav.common.core.utils.CollectionUtils;
import sav.commons.TestConfiguration;
import sav.commons.testdata.assertion.PrimitiveAssertionTest;
// import sav.commons.testdata.assertion.TestInput;
// import sav.commons.testdata.assertion.TestInput2;
import sav.commons.testdata.assertion.StackAssertionTest;

public class AssertionGenerationTest extends TzuyuCoreTest {
	
	@Before
	public void setup() throws Exception {
		testContext.getAppData().addClasspath(TestConfiguration.getTarget("slicer.javaslicer"));
		appData.setSuspiciousCalculAlgo(SpectrumAlgorithm.OCHIAI);
		app = new AssertionGeneration(testContext, appData);
		// appData.setJavaHome("D:/_1_Projects/Tzuyu/tools/jdk1.6.0_26-64b");
	}
	
	@Test
	public void test1() throws Exception {
		AssertionGenerationParams params = initAssertionGenerationParams(
				PrimitiveAssertionTest.class.getName(),
				"foo", null,
				CollectionUtils.listOf("sav.commons.testdata.assertion"),
				new ArrayList<String>(),
				false);

		app.genAssertion(params);
	}
	
	@Test
	public void test2() throws Exception {
		AssertionGenerationParams params = initAssertionGenerationParams(
				StackAssertionTest.class.getName(), 
				"foo", null,
				CollectionUtils.listOf("sav.commons.testdata.assertion"),
				new ArrayList<String>(), false);

		app.genAssertion(params);
	}

	private AssertionGenerationParams initAssertionGenerationParams(String testingClassName, String methodName, String verificationMethod,
			List<String> testingPackages, List<String> junitClassNames, boolean useSlicer) {
		AssertionGenerationParams params = new AssertionGenerationParams();
		
		params.setTestingClassNames(CollectionUtils.listOf(testingClassName));
		params.setMethodName(methodName);
		params.setVerificationMethod(verificationMethod);
		params.setTestingPkgs(testingPackages);
		params.setJunitClassNames(junitClassNames);
		params.setUseSlicer(useSlicer);
		params.setGenTest(true);
		params.setRunMutation(false);
		params.setMachineLearningEnable(true);
		params.setValueRetrieveLevel(3);
		params.setNumberOfTestCases(100);
		params.setRankToExamine(0);
		
		return params;
	}

}
