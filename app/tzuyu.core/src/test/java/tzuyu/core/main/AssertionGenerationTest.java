package tzuyu.core.main;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import faultLocalization.SpectrumBasedSuspiciousnessCalculator.SpectrumAlgorithm;
import sav.common.core.Constants;
import sav.common.core.utils.CollectionUtils;
import sav.commons.TestConfiguration;
// import sav.commons.testdata.assertion.TestInput;
// import sav.commons.testdata.assertion.TestInput2;

public class AssertionGenerationTest extends TzuyuCoreTest {
	
	@Before
	public void setup() throws Exception {
		List<String> projectClasspath = testContext.getAppData().getAppClasspaths();
		projectClasspath.add(TestConfiguration.getTarget("slicer.javaslicer"));
		projectClasspath.add(TestConfiguration.getTzAssembly(Constants.SAV_COMMONS_ASSEMBLY));
		appData.setSuspiciousCalculAlgo(SpectrumAlgorithm.OCHIAI);
		app = new AssertionGeneration(testContext, appData);
	}
	
	@Test
	public void test1() throws Exception {
		AssertionGenerationParams params = initAssertionGenerationParams(
				"sav.commons.testdata.assertion.TestInput", //TestInput.class.getName(), 
				"foo", null,
				CollectionUtils.listOf("sav.commons.testdata.assertion"),
				new ArrayList<String>(),
				// CollectionUtils.listOf("sav.commons.testdata.assertion.TestInput1"),
				false);

		app.genAssertion(params);
	}
	
	@Test
	public void test2() throws Exception {
		AssertionGenerationParams params = initAssertionGenerationParams(
				"sav.commons.testdata.assertion.TestInput2", 
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
		params.setNumberOfTestCases(10);
		params.setRankToExamine(0);
		
		return params;
	}

}
