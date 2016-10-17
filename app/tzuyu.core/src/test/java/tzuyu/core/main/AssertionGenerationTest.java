package tzuyu.core.main;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import libsvm.svm;
import libsvm.svm_print_interface;
import sav.common.core.SystemVariablesUtils;
import sav.common.core.utils.CollectionUtils;
import sav.commons.testdata.assertion.ArrayAssertionTest;
import sav.commons.testdata.assertion.CompositeAssertionTest;
import sav.commons.testdata.assertion.NeqAssertionTest;
import sav.commons.testdata.assertion.MultipleAssertionsTest;
import sav.commons.testdata.assertion.MultipleMethodsTest;
import sav.commons.testdata.assertion.PrimitiveAssertion;
import sav.commons.testdata.assertion.StackAssertionTest;

public class AssertionGenerationTest extends AbstractTzTest {

	AssertionGeneration app;
	
	@Before
	public void setup() throws Exception {
		String jarPath = SystemVariablesUtils.updateSavJunitJarPath(appData);
		appData.addClasspath(jarPath);
		app = new AssertionGeneration(testContext);
		svm.svm_set_print_string_function(new svm_print_interface() {

			@Override
			public void print(String s) {
				// TODO Auto-generated method stub

			}
		});
	}
	
	@Test
	public void test1() throws Exception {
		long startTime = System.currentTimeMillis();
		
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("test.PrimitiveAssertion1");
		
		AssertionGenerationParams params = initAssertionGenerationParams(PrimitiveAssertion.class.getName(), "foo",
				null, CollectionUtils.listOf("sav.commons.testdata.assertion"), junitClassNames, false);

		app.genAssertion(params);
		
		long endTime = System.currentTimeMillis();
	
		System.out.println(endTime - startTime);
	}

	@Test
	public void test2() throws Exception {
		long startTime = System.currentTimeMillis();
		
		AssertionGenerationParams params = initAssertionGenerationParams(StackAssertionTest.class.getName(), "foo",
				null, CollectionUtils.listOf("sav.commons.testdata.assertion"), new ArrayList<String>(), false);

		app.genAssertion(params);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}

	@Test
	public void test3() throws Exception {
		long startTime = System.currentTimeMillis();
		
		AssertionGenerationParams params = initAssertionGenerationParams(ArrayAssertionTest.class.getName(), "foo",
				null, CollectionUtils.listOf("sav.commons.testdata.assertion"), new ArrayList<String>(), false);

		app.genAssertion(params);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(endTime - startTime);
	}

	@Test
	public void test4() throws Exception {
		AssertionGenerationParams params = initAssertionGenerationParams(CompositeAssertionTest.class.getName(), "foo",
				null, CollectionUtils.listOf("sav.commons.testdata.assertion"), new ArrayList<String>(), false);

		app.genAssertion(params);
	}

	@Test
	public void test5() throws Exception {
		AssertionGenerationParams params = initAssertionGenerationParams(MultipleAssertionsTest.class.getName(), "foo",
				null, CollectionUtils.listOf("sav.commons.testdata.assertion"), new ArrayList<String>(), false);

		app.genAssertion(params);
	}
	
	@Test
	public void test6() throws Exception {
		AssertionGenerationParams params = initAssertionGenerationParams(MultipleMethodsTest.class.getName(), "foo",
				null, CollectionUtils.listOf("sav.commons.testdata.assertion"), new ArrayList<String>(), false);

		List<String> methodNames = new ArrayList<String>();
		methodNames.add("foo");
		methodNames.add("foo2");
		params.setListOfMethods(methodNames);
		
		app.genAssertion(params);
	}

	@Test
	public void test7() throws Exception {
		AssertionGenerationParams params = initAssertionGenerationParams(NeqAssertionTest.class.getName(), "foo",
				null, CollectionUtils.listOf("sav.commons.testdata.assertion"), new ArrayList<String>(), false);

		app.genAssertion(params);
	}
	
	private AssertionGenerationParams initAssertionGenerationParams(String testingClassName, String methodName,
			String verificationMethod, List<String> testingPackages, List<String> junitClassNames, boolean useSlicer) {
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
