/* Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
public class AssertionGenerationAllTest extends AbstractTzPackageTest {
	protected AssertionGeneration gen;
	protected AssertionGenerationParams params;

	private static Logger log = LoggerFactory.getLogger(AssertionGenerationAllTest.class);

	private String testDataFile = "/Users/HongLongPham/Workspace/Tzuyu/etc/testdata.csv";

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
	
	private void setTests(boolean hasTests) {
		if (hasTests)
			params.setJunitClassNames(junitClassNames);
		else
			params.setJunitClassNames(new ArrayList<String>());
	}
	
	private void setTests(boolean hasTests, int loop) {
		if (hasTests) {
			String tests = junitClassNames.get(0) + loop;
			params.setJunitClassNames(Arrays.asList(tests));
		}
		else
			params.setJunitClassNames(new ArrayList<String>());
	}

	public void genAssertion(TestPackage testPkg, boolean hasTests,
			boolean repeat, int loop) throws Exception {
		prepare(testPkg);

		params.setTestingClassNames(testingClassNames);
		params.setTestingPkgs(testingPackages);
		
		gen = new AssertionGeneration(context);
		
		if (repeat) {
			log.info("Loop {}\n", loop);
			setTests(hasTests, loop);
		} else {
			setTests(hasTests);
		}
		
		long startTime = System.currentTimeMillis();
		gen.genAssertion(params);
		long endTime = System.currentTimeMillis();
		
		log.info("Time = {}\n", endTime - startTime);
	}

	/**
	 * test part
	 */

	class Task implements Callable<String> {
		private String project;
		private String id;
		private String method;
		private boolean hasTests;
		private boolean repeat;
		private int loop;

		public Task(String project, String id, String method,
				boolean hasTests, boolean repeat, int loop) {
			this.project = project;
			this.id = id;
			this.method = method;
			this.hasTests = hasTests;
			this.repeat = repeat;
			this.loop = loop;
		}

		@Override
		public String call() throws Exception {
			TestPackage testPkg = TestPackage.getPackage(project, id);
			params.setMethodName(method);
			genAssertion(testPkg, hasTests, repeat, loop);

			return "Finish!";
		}
	}

	private void test(String projectName, int startIndex, int endIndex,
			boolean hasTests, boolean repeat) throws Exception {
		long startTime = System.currentTimeMillis();
		
		CSVFormat format = CSVFormat.EXCEL.withHeader(TestDataColumn.allColumns());
		CSVParser parser = CSVParser.parse(new File(
				testDataFile), Charset.forName("UTF-8"), format);
		List<CSVRecord> records = parser.getRecords();
		
		for (int i = startIndex; i <= endIndex; i++) {
			CSVRecord record = records.get(i);
			
			String project = record.get(0);
			String id = record.get(1);
			String method = record.get(11);
			
			if (project.equals(projectName)) {
				log.info("Project = {}\n", project);
				log.info("ID = {}\n", id);
				log.info("Method = {}\n", method);
				
				int loop = repeat ? 20 : 1;
				
				for (int j = 1; j <= loop; j++) {
					ExecutorService executor = Executors.newSingleThreadExecutor();
					Future<String> future = executor.submit(new Task(project, id, method,
							hasTests, repeat, j));
					
					try {
			            future.get(180, TimeUnit.SECONDS);
			        } catch (TimeoutException e) {
			            future.cancel(true);
			            log.info("Timeout!!!\n");
			        }
					
					executor.shutdownNow();
				}
			}
			
		}
		
		long endTime = System.currentTimeMillis();
		
		log.info("Total time = {}\n", endTime - startTime);
	}

	@Test
	public void testAlgorithmsAll1() throws Exception {
		test("algorithms-master", 4, 35, true, false);
	}
	
	@Test
	public void testAlgorithmsAll2() throws Exception {
		test("algorithms-master", 36, 77, true, false);
	}
	
	@Test
	public void testAlgorithmsAll2b() throws Exception {
		test("algorithms-master", 36, 63, true, false);
	}
	
	@Test
	public void testAlgorithmsAll2c() throws Exception {
		test("algorithms-master", 65, 77, true, false);
	}
	
	@Test
	public void testAlgorithmsAll3() throws Exception {
		test("algorithms-master", 78, 99, true, false);
	}
	
	@Test
	public void testJodaTimeAll1() throws Exception {
		test("joda-time-master", 118, 167, true, false);
	}
	
	@Test
	public void testJodaTimeAll2() throws Exception {
		test("joda-time-master", 168, 217, true, false);
	}
	
	@Test
	public void testJodaTimeAll3() throws Exception {
		test("joda-time-master", 218, 267, true, false);
	}
	
	@Test
	public void testJodaTimeAll4() throws Exception {
		test("joda-time-master", 268, 317, true, false);
	}
	
	@Test
	public void testJodaTimeAll5() throws Exception {
		test("joda-time-master", 318, 353, true, false);
	}
	
	@Test
	public void testJodaMoneyAll1() throws Exception {
		test("joda-money-master", 354, 400, true, false);
	}
	
	@Test
	public void testJodaMoneyAll2() throws Exception {
		test("joda-money-master", 401, 446, true, false);
	}
	
	@Test
	public void testSVCompAll1() throws Exception {
		test("svcomp", 447, 448, true, true);
	}
	
	@Test
	public void testSVCompAll2() throws Exception {
		test("svcomp", 449, 450, true, true);
	}
	
	@Test
	public void testSVCompAll3() throws Exception {
		test("svcomp", 451, 452, true, true);
	}
	
	@Test
	public void testSVCompAll4() throws Exception {
		test("svcomp", 453, 454, true, true);
	}
	
	@Test
	public void testSVCompAll5() throws Exception {
		test("svcomp", 455, 456, true, true);
	}
	
	@Test
	public void testJavaLibAll() throws Exception {
		test("java-library", 457, 509, true, false);
	}
	
	@Test
	public void testAll() throws Exception {
//		testAlgorithmsAll1();
//		testAlgorithmsAll2();
//		testAlgorithmsAll3();

//		testJodaTimeAll1();
//		testJodaTimeAll2();
//		testJodaTimeAll3();
//		testJodaTimeAll4();
//		testJodaTimeAll5();
//		
//		testJodaMoneyAll1();
//		testJodaMoneyAll2();
//		
		testSVCompAll1();
		testSVCompAll2();
		testSVCompAll3();
		testSVCompAll4();
		testSVCompAll5();
//		
//		testJavaLibAll();
		
//		testAlgorithmsAll2b();
//		testAlgorithmsAll2c();
	}
	
	@Test
	public void testSingle() throws Exception {
//		test("joda-time-master", 186, 186, true, false);
//		test("joda-time-master", 193, 193, true, false);
//		test("joda-time-master", 196, 196, true, false);
//		test("joda-time-master", 299, 299, true, false);
//		test("java-library", 507, 509, true, false);
//		test("joda-money-master", 365, 365, true, false);
		test("svcomp", 452, 452, true, true);
	}

}