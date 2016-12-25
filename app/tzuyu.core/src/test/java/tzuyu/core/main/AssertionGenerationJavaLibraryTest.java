package tzuyu.core.main;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icsetlv.variable.VarNameVisitor.VarNameCollectionMode;
import libsvm.svm;
import libsvm.svm_print_interface;
import sav.common.core.SystemVariablesUtils;
import sav.commons.testdata.opensource.TestPackage;

public class AssertionGenerationJavaLibraryTest extends AbstractTzPackageTest {
	
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
//		params.setJunitClassNames(new ArrayList<String>());

		gen.genAssertion(params);
	}

	/**
	 * test part
	 */

	@Test
	public void testMath1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Math1");
		params.setMethodName("floorDivInt");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMath2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Math2");
		params.setMethodName("floorModInt");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMath3() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Math3");
		params.setMethodName("floorDivLong");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMath4() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Math4");
		params.setMethodName("floorModLong");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMath5() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Math5");
		params.setMethodName("addExactInt");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMath6() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Math6");
		params.setMethodName("subExactInt");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMath7() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Math7");
		params.setMethodName("mulExactInt");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMath8() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Math8");
		params.setMethodName("incExactInt");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMath9() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Math9");
		params.setMethodName("decExactInt");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMath10() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Math10");
		params.setMethodName("negExactInt");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMath11() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Math11");
		params.setMethodName("addExactLong");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMath12() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Math12");
		params.setMethodName("subExactLong");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMath13() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Math13");
		params.setMethodName("mulExactLong");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMath14() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Math14");
		params.setMethodName("incExactLong");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMath15() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Math15");
		params.setMethodName("decExactLong");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMath16() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Math16");
		params.setMethodName("negExactLong");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String1");
		params.setMethodName("getChars");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String2");
		params.setMethodName("getBytes1");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString3() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String3");
		params.setMethodName("getBytes2");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString4() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String4");
		params.setMethodName("getBytes3");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString5() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String5");
		params.setMethodName("contentEquals");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString6() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String6");
		params.setMethodName("compareTo");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString7() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String7");
		params.setMethodName("compareToIgnoreCase");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString8() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String8");
		params.setMethodName("regionMatches1");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString9() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String9");
		params.setMethodName("regionMatches2");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString10() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String10");
		params.setMethodName("startsWith1");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString11() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String11");
		params.setMethodName("startsWith2");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString12() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String12");
		params.setMethodName("endsWith");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString13() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String13");
		params.setMethodName("indexOf1");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString14() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String14");
		params.setMethodName("indexOf2");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString15() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String15");
		params.setMethodName("lastIndexOf1");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString16() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String16");
		params.setMethodName("lastIndexOf2");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString17() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String17");
		params.setMethodName("concat");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString18() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String18");
		params.setMethodName("matches");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString19() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String19");
		params.setMethodName("replaceFirst");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString20() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String20");
		params.setMethodName("replaceAll");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString21() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String21");
		params.setMethodName("split1");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString22() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String22");
		params.setMethodName("split2");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString23() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String23");
		params.setMethodName("toLowerCase");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString24() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String24");
		params.setMethodName("toUpperCase");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString25() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String25");
		params.setMethodName("valueOf1");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString26() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String26");
		params.setMethodName("valueOf2");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString27() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String27");
		params.setMethodName("valueOf3");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString28() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String28");
		params.setMethodName("copyValueOf1");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testString29() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "String29");
		params.setMethodName("copyValueOf2");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testArrayList1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "ArrayList1");
		params.setMethodName("forEach");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testArrayList2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "ArrayList2");
		params.setMethodName("replaceAll");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testArrayList3() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "ArrayList3");
		params.setMethodName("removeIf");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testLinkedList1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "LinkedList1");
		params.setMethodName("forEach");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testLinkedList2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "LinkedList2");
		params.setMethodName("replaceAll");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testLinkedList3() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "LinkedList3");
		params.setMethodName("removeIf");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testVector1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Vector1");
		params.setMethodName("forEach");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testVector2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Vector2");
		params.setMethodName("replaceAll");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testVector3() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Vector3");
		params.setMethodName("removeIf");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testArrays1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Arrays1");
		params.setMethodName("parallelPrefix1");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testArrays2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Arrays2");
		params.setMethodName("parallelPrefix2");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testArrays3() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("java-library", "Arrays3");
		params.setMethodName("parallelPrefix3");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

}
