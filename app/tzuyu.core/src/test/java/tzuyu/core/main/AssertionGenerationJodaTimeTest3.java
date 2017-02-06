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
public class AssertionGenerationJodaTimeTest3 extends AbstractTzPackageTest {
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
	public void testMutablePeriod_Updates1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "MutablePeriod_Updates1");
		params.setMethodName("setYears(I)V");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMutablePeriod_Updates2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "MutablePeriod_Updates2");
		params.setMethodName("set(LDurationFieldType;I)V");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMutablePeriod_Updates3() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "MutablePeriod_Updates3");
		params.setMethodName("add(LDurationFieldType;I)V");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMutablePeriod_Updates4() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "MutablePeriod_Updates4");
		params.setMethodName("setPeriod(IIIIIIII)V");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMutablePeriod_Updates5() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "MutablePeriod_Updates5");
		params.setMethodName("setPeriod(LReadablePeriod;)V");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMutablePeriod_Updates6() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "MutablePeriod_Updates6");
		params.setMethodName("add(IIIIIIII)V");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMutablePeriod_Updates7() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "MutablePeriod_Updates7");
		params.setMethodName("add(LReadablePeriod;)V");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMutablePeriod_Updates8() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "MutablePeriod_Updates8");
		params.setMethodName("mergePeriod(LReadablePeriod;)V");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPartial_Basics2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Partial_Basics2");
		params.setMethodName("getFieldType(I)LDateTimeFieldType;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPartial_Basics4() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Partial_Basics4");
		params.setMethodName("getValue(I)I");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPartial_Basics9() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Partial_Basics9");
		params.setMethodName("with(LDateTimeFieldType;I)LPartial;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPartial_Basics10() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Partial_Basics10");
		params.setMethodName("withField(LDateTimeFieldType;I)LPartial;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPartial_Basics11() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Partial_Basics11");
		params.setMethodName("withFieldAdded(LDurationFieldType;I)LPartial;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPartial_Basics12() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Partial_Basics12");
		params.setMethodName("withFieldAddWrapped(LDurationFieldType;I)LPartial;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPartial_Basics13() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Partial_Basics13");
		params.setMethodName("property(LDateTimeFieldType;)LProperty;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testPartial_Match1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Partial_Match1");
		params.setMethodName("isMatch(LReadablePartial;)Z");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSeconds1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Seconds1");
		params.setMethodName("standardSecondsIn(LReadablePeriod;)LSeconds;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSeconds2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Seconds2");
		params.setMethodName("parseSeconds(LString;)LSeconds;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSeconds3() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Seconds3");
		params.setMethodName("plus(I)LSeconds;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSeconds4() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Seconds4");
		params.setMethodName("plus(LSeconds;)LSeconds;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSeconds5() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Seconds5");
		params.setMethodName("minus(I)LSeconds;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSeconds6() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Seconds6");
		params.setMethodName("minus(LSeconds;)LSeconds;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSeconds7() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Seconds7");
		params.setMethodName("multipliedBy(I)LSeconds;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSeconds8() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Seconds8");
		params.setMethodName("dividedBy(I)LSeconds;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testSeconds9() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Seconds9");
		params.setMethodName("negated()LSeconds;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testTimeOfDay_Basics2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "TimeOfDay_Basics2");
		params.setMethodName("getFieldType(I)LDateTimeFieldType;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testTimeOfDay_Basics9() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "TimeOfDay_Basics9");
		params.setMethodName("withField(LDateTimeFieldType;I)LTimeOfDay;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testTimeOfDay_Basics10() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "TimeOfDay_Basics10");
		params.setMethodName("withFieldAdded(LDurationFieldType;I)LTimeOfDay;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testTimeOfDay_Basics11() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "TimeOfDay_Basics11");
		params.setMethodName("withHourOfDay(I)LTimeOfDay;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testTimeOfDay_Basics12() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "TimeOfDay_Basics12");
		params.setMethodName("property(LDateTimeFieldType;)LProperty;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testTimeOfDay_Constructors1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "TimeOfDay_Constructors1");
		params.setMethodName("fromCalendarFields(LCalendar;)LTimeOfDay;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testTimeOfDay_Constructors2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "TimeOfDay_Constructors2");
		params.setMethodName("fromDateFields(LDate;)LTimeOfDay;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testWeeks1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Weeks1");
		params.setMethodName("standardWeeksIn(LReadablePeriod;)LWeeks;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testWeeks2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Weeks2");
		params.setMethodName("parseWeeks(LString;)LWeeks;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testWeeks3() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Weeks3");
		params.setMethodName("toStandardDays()LDays;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testWeeks4() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Weeks4");
		params.setMethodName("toStandardHours()LHours;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testWeeks5() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Weeks5");
		params.setMethodName("toStandardMinutes()LMinutes;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testWeeks6() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Weeks6");
		params.setMethodName("toStandardSeconds()LSeconds;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testWeeks7() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Weeks7");
		params.setMethodName("plus(I)LWeeks;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testWeeks8() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Weeks8");
		params.setMethodName("plus(LWeeks;)LWeeks;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testWeeks9() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Weeks9");
		params.setMethodName("minus(I)LWeeks;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testWeeks10() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Weeks10");
		params.setMethodName("minus(LWeeks;)LWeeks;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testWeeks11() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Weeks11");
		params.setMethodName("multipliedBy(I)LWeeks;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testWeeks12() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Weeks12");
		params.setMethodName("dividedBy(I)LWeeks;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testWeeks13() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Weeks13");
		params.setMethodName("negated()LWeeks;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYears1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Years1");
		params.setMethodName("parseYears(LString;)LYears;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYears2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Years2");
		params.setMethodName("plus(I)LYears;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYears3() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Years3");
		params.setMethodName("plus(LYears;)LYears;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYears4() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Years4");
		params.setMethodName("minus(I)LYears;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYears5() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Years5");
		params.setMethodName("minus(LYears;)LYears;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYears6() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Years6");
		params.setMethodName("multipliedBy(I)LYears;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYears7() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Years7");
		params.setMethodName("dividedBy(I)LYears;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYears8() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Years8");
		params.setMethodName("negated()LYears;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYearMonth_Basics2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "YearMonth_Basics2");
		params.setMethodName("getFieldType(I)LDateTimeFieldType;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYearMonth_Basics9() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "YearMonth_Basics9");
		params.setMethodName("withChronologyRetainFields(LChronology;)LYearMonth;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYearMonth_Basics10() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "YearMonth_Basics10");
		params.setMethodName("withField(LDateTimeFieldType;I)LYearMonth;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYearMonth_Basics11() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "YearMonth_Basics11");
		params.setMethodName("withFieldAdded(LDurationFieldType;I)LYearMonth;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYearMonth_Basics12() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "YearMonth_Basics12");
		params.setMethodName("toLocalDate(I)LLocalDate;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYearMonth_Basics13() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "YearMonth_Basics13");
		params.setMethodName("withMonthOfYear(I)LYearMonth;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYearMonth_Basics14() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "YearMonth_Basics14");
		params.setMethodName("property(LDateTimeFieldType;)LProperty;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYearMonth_Constructors1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "YearMonth_Constructors1");
		params.setMethodName("fromCalendarFields(LCalendar;)LYearMonth;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYearMonth_Constructors2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "YearMonth_Constructors2");
		params.setMethodName("fromDateFields(LDate;)LYearMonth;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYearMonthDay_Basics2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "YearMonthDay_Basics2");
		params.setMethodName("getFieldType(I)LDateTimeFieldType;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYearMonthDay_Basics9() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "YearMonthDay_Basics9");
		params.setMethodName("withChronologyRetainFields(LChronology;)LYearMonthDay;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYearMonthDay_Basics10() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "YearMonthDay_Basics10");
		params.setMethodName("withField(LDateTimeFieldType;I)LYearMonthDay;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYearMonthDay_Basics11() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "YearMonthDay_Basics11");
		params.setMethodName("withFieldAdded(LDurationFieldType;I)LYearMonthDay;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYearMonthDay_Basics12() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "YearMonthDay_Basics12");
		params.setMethodName("withMonthOfYear(I)LYearMonthDay;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYearMonthDay_Basics13() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "YearMonthDay_Basics13");
		params.setMethodName("property(LDateTimeFieldType;)LProperty;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYearMonthDay_Constructors1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "YearMonthDay_Constructors1");
		params.setMethodName("fromCalendarFields(LCalendar;)LYearMonthDay;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testYearMonthDay_Constructors2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "YearMonthDay_Constructors2");
		params.setMethodName("fromDateFields(LDate;)LYearMonthDay;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
}