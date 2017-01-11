/* Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icsetlv.variable.VarNameVisitor.VarNameCollectionMode;
import libsvm.svm;
import libsvm.svm_print_interface;
import sav.common.core.SystemVariablesUtils;
import sav.commons.testdata.opensource.TestPackage;
import sav.commons.testdata.opensource.TestPackage.TestDataColumn;

/**
 * @author LLT
 *
 */
public class AssertionGenerationJodaTimeTest extends AbstractTzPackageTest {
	protected AssertionGeneration gen;
	protected AssertionGenerationParams params;

	private static Logger log = LoggerFactory.getLogger(AssertionGenerationJodaTimeTest.class);

	@Before
	public void setup() {
		super.setup();

		String jarPath = SystemVariablesUtils.updateSavJunitJarPath(appData);
		appData.addClasspath(jarPath);

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
		
		gen = new AssertionGeneration(context);

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
	public void testAlgorithmsTotal() throws Exception {
		long startTime = System.currentTimeMillis();
		
		String testDataFile = "/Users/HongLongPham/Workspace/Tzuyu/etc/testdata.csv";
		int begin = 4;
		int end = 7;
		
		CSVFormat format = CSVFormat.EXCEL.withHeader(TestDataColumn.allColumns());
		CSVParser parser = CSVParser.parse(new File(
				testDataFile), Charset.forName("UTF-8"), format);
		List<CSVRecord> records = parser.getRecords();
		
		for (int i = begin; i <= end; i++) {
			CSVRecord record = records.get(i);
			
			String projectName = record.get(0);
			String id = record.get(1);
			String methodName = record.get(11);
			
			System.out.println(projectName);
			System.out.println(id);
			System.out.println(methodName);
			
			if (projectName.equals("algorithms-master")) {
				TestPackage testPkg = TestPackage.getPackage(projectName, id);
				params.setMethodName(methodName);
				genAssertion(testPkg);
			}
			
		}
		

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testDateMidnight_Basics3() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "DateMidnight_Basics3");
		params.setMethodName("withMonthOfYear(I)LDateMidnight;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDateMidnight_Basics5() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "DateMidnight_Basics5");
		params.setMethodName("withField(LDateTimeFieldType;I)LDateMidnight;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDateMidnight_Basics6() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "DateMidnight_Basics6");
		params.setMethodName("withFieldAdded(LDurationFieldType;I)LDateMidnight;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDateMidnight_Basics7() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "DateMidnight_Basics7");
		params.setMethodName("property(LDateTimeFieldType;)LProperty;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDateMidnight_Constructors1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "DateMidnight_Constructors1");
		params.setMethodName("now(LDateTimeZone;)LDateMidnight;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDateMidnight_Constructors2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "DateMidnight_Constructors2");
		params.setMethodName("now(LChronology;)LDateMidnight;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDateTime_Basics3() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "DateTime_Basics3");
		params.setMethodName("withMonthOfYear(I)LDateTime;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDateTime_Basics5() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "DateTime_Basics5");
		params.setMethodName("withDate(III)LDateTime;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDateTime_Basics7() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "DateTime_Basics7");
		params.setMethodName("withTime(IIII)LDateTime;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDateTime_Basics9() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "DateTime_Basics9");
		params.setMethodName("withField(LDateTimeFieldType;I)LDateTime;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDateTime_Basics10() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "DateTime_Basics10");
		params.setMethodName("withFieldAdded(LDurationFieldType;I)LDateTime;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDateTime_Basics11() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "DateTime_Basics11");
		params.setMethodName("property(LDateTimeFieldType;)LProperty;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDateTime_Constructors1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "DateTime_Constructors1");
		params.setMethodName("now(LDateTimeZone;)LDateTime;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDateTime_Constructors2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "DateTime_Constructors2");
		params.setMethodName("now(LChronology;)LDateTime;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDateTimeUtils1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "DateTimeUtils1");
		params.setMethodName("isContiguous(LReadablePartial;)Z");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDateTimeZone1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "DateTimeZone1");
		params.setMethodName("setDefault(LDateTimeZone;)V");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDateTimeZone2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "DateTimeZone2");
		params.setMethodName("forID(LString;)LDateTimeZone;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDateTimeZone3() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "DateTimeZone3");
		params.setMethodName("forOffsetHours(I)LDateTimeZone;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDateTimeZone4() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "DateTimeZone4");
		params.setMethodName("forOffsetHoursMinutes(II)LDateTimeZone;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDays1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Days1");
		params.setMethodName("standardDaysIn(LReadablePeriod;)LDays;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDays2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Days2");
		params.setMethodName("parseDays(LString;)LDays;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDays3() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Days3");
		params.setMethodName("toStandardHours()LHours;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDays4() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Days4");
		params.setMethodName("toStandardMinutes()LMinutes;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDays5() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Days5");
		params.setMethodName("toStandardSeconds()LSeconds;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDays6() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Days6");
		params.setMethodName("plus(I)LDays;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDays7() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Days7");
		params.setMethodName("plus(LDays;)LDays;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDays8() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Days8");
		params.setMethodName("minus(I)LDays;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDays9() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Days9");
		params.setMethodName("minus(LDays;)LDays;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDays10() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Days10");
		params.setMethodName("multipliedBy(I)LDays;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDays11() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Days11");
		params.setMethodName("dividedBy(I)LDays;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDays12() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Days12");
		params.setMethodName("negated()LDays;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDuration_Basics2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Duration_Basics2");
		params.setMethodName("toStandardDays()LDays;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDuration_Basics3() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Duration_Basics3");
		params.setMethodName("toStandardHours()LHours;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDuration_Basics4() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Duration_Basics4");
		params.setMethodName("toStandardMinutes()LMinutes;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDuration_Basics5() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Duration_Basics5");
		params.setMethodName("toStandardSeconds()LSeconds;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDuration_Basics6() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Duration_Basics6");
		params.setMethodName("negated()LDuration;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testDurationField1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "DurationField1");
		params.setMethodName("subtract(JJ)J");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testHours1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Hours1");
		params.setMethodName("standardHoursIn(LReadablePeriod;)LHours;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testHours2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Hours2");
		params.setMethodName("parseHours(LString;)LHours;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testHours3() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Hours3");
		params.setMethodName("toStandardMinutes()LMinutes;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testHours4() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Hours4");
		params.setMethodName("toStandardSeconds()LSeconds;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testHours5() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Hours5");
		params.setMethodName("plus(I)LHours;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testHours6() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Hours6");
		params.setMethodName("plus(LHours;)LHours;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testHours7() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Hours7");
		params.setMethodName("minus(I)LHours;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testHours8() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Hours8");
		params.setMethodName("minus(LHours;)LHours;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testHours9() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Hours9");
		params.setMethodName("multipliedBy(I)LHours;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testHours10() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Hours10");
		params.setMethodName("dividedBy(I)LHours;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testHours11() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Hours11");
		params.setMethodName("negated()LHours;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testInterval_Basics1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Interval_Basics1");
		params.setMethodName("withStartMillis(J)LInterval;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testInterval_Basics2() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Interval_Basics2");
		params.setMethodName("withStart(LReadableInstant;)LInterval;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testInterval_Basics3() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Interval_Basics3");
		params.setMethodName("withEndMillis(J)LInterval;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testInterval_Basics4() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Interval_Basics4");
		params.setMethodName("withEnd(LReadableInstant;)LInterval;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testInterval_Basics5() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Interval_Basics5");
		params.setMethodName("withDurationAfterStart(LReadableDuration;)LInterval;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testInterval_Basics6() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Interval_Basics6");
		params.setMethodName("withDurationBeforeEnd(LReadableDuration;)LInterval;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testInterval_Basics7() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Interval_Basics7");
		params.setMethodName("withPeriodAfterStart(LReadablePeriod;)LInterval;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testInterval_Basics8() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Interval_Basics8");
		params.setMethodName("withPeriodBeforeEnd(LReadablePeriod;)LInterval;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testInterval_Constructors1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "Interval_Constructors1");
		params.setMethodName("parseWithOffset(LString;)LInterval;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testLocalDate_Basics1() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "LocalDate_Basics1");
		params.setMethodName("get(LDateTimeFieldType;)I");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testLocalDate_Basics4() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "LocalDate_Basics4");
		params.setMethodName("getValue(I)I");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testLocalDate_Basics5() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "LocalDate_Basics5");
		params.setMethodName("compareTo(LReadablePartial;)I");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testLocalDate_Basics9() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "LocalDate_Basics9");
		params.setMethodName("withField(LDateTimeFieldType;I)LLocalDate;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	@Test
	public void testLocalDate_Basics10() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "LocalDate_Basics10");
		params.setMethodName("withFieldAdded(LDurationFieldType;I)LLocalDate;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testLocalDate_Basics11() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "LocalDate_Basics11");
		params.setMethodName("withMonthOfYear(I)LLocalDate;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testLocalDate_Basics12() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "LocalDate_Basics12");
		params.setMethodName("toLocalDateTime(LLocalTime;)LLocalDateTime;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testLocalDate_Basics13() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "LocalDate_Basics13");
		params.setMethodName("toDateTime(LLocalTime;LDateTimeZone;)LDateTime;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}
	
	@Test
	public void testLocalDate_Basics14() throws Exception {
		long startTime = System.currentTimeMillis();

		TestPackage testPkg = TestPackage.getPackage("joda-time-master", "LocalDate_Basics14");
		params.setMethodName("property(LDateTimeFieldType;)LProperty;");
		appData.addClasspath(
				"/Users/HongLongPham/Workspace/mvn-repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar");
		genAssertion(testPkg);

		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

}