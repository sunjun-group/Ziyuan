package icsetlv;

import icsetlv.Engine.Result;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.commons.utils.TestConfigUtils;

public class EngineTest extends AbstractTest {

	private static final String MODULE_NAME = "icsetlv";
	private static final int DEBUG_PORT = 8787;
	private Engine engine;

	@Before
	public void prepareEngine() {
		final TestConfiguration config = TestConfiguration.getInstance();
		
		engine = new Engine().setPort(DEBUG_PORT)
				.setJavaHome(TestConfigUtils.getJavaHome())
//				.setLaunchClass(config.getJunitcore())
				.addToClassPath(config.getJavaBin())
				.addToClassPath(TestConfiguration.TRUNK + "/etc/app_assembly/sav-commons.jar")
//				.addToClassPath(config.getJunitLib())
				.addToClassPath(TestConfiguration.getTestTarget(MODULE_NAME));
		
	}

	@Test
	public void doTest() throws Exception {
		engine.addTestcase("testdata.CalculatorTestPassed.testPassed1");
		engine.addTestcase("testdata.CalculatorTestPassed.testPassed2");
		engine.addTestcase("testdata.CalculatorTestPassed.testPassed3");
		engine.addTestcase("testdata.CalculatorTestPassed.testPassed4");
		engine.addTestcase("testdata.CalculatorTestFailed.testFailed1");
		engine.addTestcase("testdata.CalculatorTestFailed.testFailed2");
		engine.addTestcase("testdata.CalculatorTestFailed.testFailed3");
		engine.addTestcase("testdata.CalculatorTestFailed.testFailed4");
		engine.addTestcase("testdata.CalculatorTestFailed.testFailed5");
		engine.addTestcase("testdata.CalculatorTestFailed.testFailed6");
//		engine.addPassedTestcase("testdata.CalculatorTestPassed");
//		engine.addFailedTestcase("testdata.CalculatorTestFailed");

		engine.addBreakPoint("testdata.Calculator", "getMax", 6, "x", "y");

		engine.run();
		final List<Result> results = engine.getResults();
		for (Result result : results) {
			System.out.println(result);
		}
	}

}
