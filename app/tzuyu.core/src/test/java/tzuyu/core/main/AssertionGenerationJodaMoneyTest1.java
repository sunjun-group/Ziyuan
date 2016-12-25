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
public class AssertionGenerationJodaMoneyTest1 extends AbstractTzPackageTest {
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
	public void testBigMoney1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney1");
		params.setMethodName("of(LCurrencyUnit;LBigDecimal;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney2");
		params.setMethodName("of(LCurrencyUnit;D)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney3() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney3");
		params.setMethodName("ofScale(LCurrencyUnit;LBigDecimal;I)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney4() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney4");
		params.setMethodName("ofScale(LCurrencyUnit;LBigDecimal;ILRoundingMode;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney5() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney5");
		params.setMethodName("ofScale(LCurrencyUnit;JI)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney6() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney6");
		params.setMethodName("ofMajor(LCurrencyUnit;J)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney7() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney7");
		params.setMethodName("ofMinor(LCurrencyUnit;J)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney8() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney8");
		params.setMethodName("zero(LCurrencyUnit;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney9() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney9");
		params.setMethodName("zero(LCurrencyUnit;I)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney10() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney10");
		params.setMethodName("of(LBigMoneyProvider;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney11() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney11");
		params.setMethodName("total(LBigMoneyProvider;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney12() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney12");
		params.setMethodName("total(LIterable;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney13() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney13");
		params.setMethodName("total(LCurrencyUnit;LBigMoneyProvider;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney14() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney14");
		params.setMethodName("total(LCurrencyUnit;LIterable;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney15() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney15");
		params.setMethodName("parse(LString;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney16() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney16");
		params.setMethodName("nonNull(LBigMoney;LCurrencyUnit;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney18() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney18");
		params.setMethodName("withCurrencyUnit(LCurrencyUnit;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney19() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney19");
		params.setMethodName("withScale(I)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney20() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney20");
		params.setMethodName("withCurrencyScale()LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney21() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney21");
		params.setMethodName("getAmountMajorLong()J");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney22() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney22");
		params.setMethodName("getAmountMajorInt()I");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney23() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney23");
		params.setMethodName("getAmountMinorLong()J");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney24() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney24");
		params.setMethodName("getAmountMinorInt()I");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney25() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney25");
		params.setMethodName("withAmount(LBigDecimal;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney26() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney26");
		params.setMethodName("plus(LIterable;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney27() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney27");
		params.setMethodName("plus(LBigMoneyProvider;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney28() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney28");
		params.setMethodName("plus(LBigDecimal;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney29() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney29");
		params.setMethodName("plusRetainScale(LBigMoneyProvider;LRoundingMode;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney30() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney30");
		params.setMethodName("plusRetainScale(LBigDecimal;LRoundingMode;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney31() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney31");
		params.setMethodName("plusRetainScale(DLRoundingMode;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney32() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney32");
		params.setMethodName("minus(LIterable;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney33() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney33");
		params.setMethodName("minus(LBigMoneyProvider;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney34() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney34");
		params.setMethodName("minus(LBigDecimal;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney35() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney35");
		params.setMethodName("minusRetainScale(LBigMoneyProvider;LRoundingMode;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney36() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney36");
		params.setMethodName("minusRetainScale(LBigDecimal;LRoundingMode;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney37() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney37");
		params.setMethodName("minusRetainScale(DLRoundingMode;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney38() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney38");
		params.setMethodName("multipliedBy(LBigDecimal;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney39() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney39");
		params.setMethodName("multiplyRetainScale(LBigDecimal;LRoundingMode;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney40() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney40");
		params.setMethodName("multiplyRetainScale(DLRoundingMode;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney41() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney41");
		params.setMethodName("dividedBy(LBigDecimal;LRoundingMode;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney42() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney42");
		params.setMethodName("dividedBy(DLRoundingMode;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney43() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney43");
		params.setMethodName("convertedTo(LCurrencyUnit;LBigDecimal;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney44() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney44");
		params.setMethodName("convertRetainScale(LCurrencyUnit;LBigDecimal;LRoundingMode;)LBigMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney45() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney45");
		params.setMethodName("isSameCurrency(LBigMoneyProvider;)Z");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney46() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney46");
		params.setMethodName("compareTo(LBigMoneyProvider;)I");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney47() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney47");
		params.setMethodName("isEqual(LBigMoneyProvider;)Z");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney48() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney48");
		params.setMethodName("isGreaterThan(LBigMoneyProvider;)Z");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testBigMoney49() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "BigMoney49");
		params.setMethodName("isLessThan(LBigMoneyProvider;)Z");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
}