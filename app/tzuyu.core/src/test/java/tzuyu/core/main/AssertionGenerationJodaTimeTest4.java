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
public class AssertionGenerationJodaTimeTest4 extends AbstractTzPackageTest {
	protected AssertionGeneration gen;
	protected AssertionGenerationParams params;

	private static Logger log = LoggerFactory.getLogger(AssertionGenerationJodaTimeTest.class);

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
	public void testPeriod_Constructors4() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Period_Constructors4");
		params.setMethodName("fieldDifference(LReadablePartial;LReadablePartial;)LPeriod;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPeriodType1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "PeriodType1");
		params.setMethodName("forFields([LDurationFieldType;)LPeriodType;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPeriod_Basics1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Period_Basics1");
		params.setMethodName("withPeriodType(LPeriodType;)LPeriod;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPeriod_Basics2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Period_Basics2");
		params.setMethodName("withFields(LReadablePeriod;)LPeriod;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPeriod_Basics3() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Period_Basics3");
		params.setMethodName("withField(LDurationFieldType;I)LPeriod;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPeriod_Basics4() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Period_Basics4");
		params.setMethodName("withFieldAdded(LDurationFieldType;I)LPeriod;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPeriod_Basics5() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Period_Basics5");
		params.setMethodName("plus(LReadablePeriod;)LPeriod;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPeriod_Basics6() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Period_Basics6");
		params.setMethodName("minus(LReadablePeriod;)LPeriod;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPeriod_Basics7() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Period_Basics7");
		params.setMethodName("plusYears(I)LPeriod;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPeriod_Basics8() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Period_Basics8");
		params.setMethodName("minusYears(I)LPeriod;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPeriod_Basics9() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Period_Basics9");
		params.setMethodName("multipliedBy(I)LPeriod;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPeriod_Basics10() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Period_Basics10");
		params.setMethodName("negated()LPeriod;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPeriod_Basics12() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Period_Basics12");
		params.setMethodName("toStandardWeeks()LWeeks;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPeriod_Basics15() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Period_Basics15");
		params.setMethodName("toStandardDays()LDays;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPeriod_Basics18() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Period_Basics18");
		params.setMethodName("toStandardHours()LHours;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPeriod_Basics21() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Period_Basics21");
		params.setMethodName("toStandardMinutes()LMinutes;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPeriod_Basics24() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Period_Basics24");
		params.setMethodName("toStandardSeconds()LSeconds;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPeriod_Basics26() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Period_Basics26");
		params.setMethodName("toStandardDuration()LDuration;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPeriod_Basics28() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Period_Basics28");
		params.setMethodName("normalizedStandard()LPeriod;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPeriod_Basics29() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Period_Basics29");
		params.setMethodName("normalizedStandard(LPeriodType;)LPeriod;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
}