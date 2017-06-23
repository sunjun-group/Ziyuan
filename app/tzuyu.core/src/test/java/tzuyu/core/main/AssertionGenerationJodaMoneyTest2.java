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
public class AssertionGenerationJodaMoneyTest2 extends AbstractTzPackageTest {
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
	public void testMoney1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money1");
		params.setMethodName("of(LCurrencyUnit;LBigDecimal;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money2");
		params.setMethodName("of(LCurrencyUnit;LBigDecimal;LRoundingMode;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney3() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money3");
		params.setMethodName("of(LCurrencyUnit;D)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney4() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money4");
		params.setMethodName("of(LCurrencyUnit;DLRoundingMode;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney5() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money5");
		params.setMethodName("ofMajor(LCurrencyUnit;J)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney6() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money6");
		params.setMethodName("ofMinor(LCurrencyUnit;J)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney7() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money7");
		params.setMethodName("zero(LCurrencyUnit;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney8() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money8");
		params.setMethodName("of(LBigMoneyProvider;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney9() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money9");
		params.setMethodName("of(LBigMoneyProvider;LRoundingMode;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney10() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money10");
		params.setMethodName("total(LMoney;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney11() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money11");
		params.setMethodName("total(LIterable;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney12() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money12");
		params.setMethodName("total(LCurrencyUnit;LMoney;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney13() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money13");
		params.setMethodName("total(LCurrencyUnit;LIterable;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney14() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money14");
		params.setMethodName("parse(LString;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney15() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money15");
		params.setMethodName("nonNull(LMoney;LCurrencyUnit;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney17() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money17");
		params.setMethodName("withCurrencyUnit(LCurrencyUnit;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney18() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money18");
		params.setMethodName("withCurrencyUnit(LCurrencyUnit;LRoundingMode;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney19() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money19");
		params.setMethodName("getAmountMajorLong()J");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney20() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money20");
		params.setMethodName("getAmountMajorInt()I");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney21() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money21");
		params.setMethodName("getAmountMinorLong()J");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney22() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money22");
		params.setMethodName("getAmountMinorInt()I");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney23() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money23");
		params.setMethodName("withAmount(LBigDecimal;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney24() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money24");
		params.setMethodName("withAmount(LBigDecimal;LRoundingMode;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney25() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money25");
		params.setMethodName("withAmount(D)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney26() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money26");
		params.setMethodName("withAmount(DLRoundingMode;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney27() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money27");
		params.setMethodName("plus(LIterable;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney28() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money28");
		params.setMethodName("plus(LMoney;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney29() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money29");
		params.setMethodName("plus(LBigDecimal;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney30() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money30");
		params.setMethodName("plus(LBigDecimal;LRoundingMode;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney31() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money31");
		params.setMethodName("plus(D)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney32() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money32");
		params.setMethodName("plus(DLRoundingMode;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney33() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money33");
		params.setMethodName("minus(LIterable;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney34() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money34");
		params.setMethodName("minus(LMoney;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney35() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money35");
		params.setMethodName("minus(LBigDecimal;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney36() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money36");
		params.setMethodName("minus(LBigDecimal;LRoundingMode;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney37() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money37");
		params.setMethodName("minus(D)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney38() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money38");
		params.setMethodName("minus(DLRoundingMode;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney39() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money39");
		params.setMethodName("multipliedBy(LBigDecimal;LRoundingMode;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney40() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money40");
		params.setMethodName("multipliedBy(DLRoundingMode;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney41() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money41");
		params.setMethodName("dividedBy(LBigDecimal;LRoundingMode;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney42() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money42");
		params.setMethodName("dividedBy(DLRoundingMode;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney43() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money43");
		params.setMethodName("convertedTo(LCurrencyUnit;LBigDecimal;LRoundingMode;)LMoney;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney44() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money44");
		params.setMethodName("isSameCurrency(LBigMoneyProvider;)Z");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney45() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money45");
		params.setMethodName("compareTo(LBigMoneyProvider;)I");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney46() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money46");
		params.setMethodName("isEqual(LBigMoneyProvider;)Z");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney47() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money47");
		params.setMethodName("isGreaterThan(LBigMoneyProvider;)Z");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testMoney48() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-money-master", "Money48");
		params.setMethodName("isLessThan(LBigMoneyProvider;)Z");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
}