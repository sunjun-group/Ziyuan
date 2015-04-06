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
				.addToClassPath("E:/Code/Tzuyu/trunk/etc/app_assembly/sav-commons.jar")
//				.addToClassPath(config.getJunitLib())
				.addToClassPath(TestConfiguration.getTestTarget(MODULE_NAME));
		
	}

	@Test
	public void doTest() throws Exception {
		engine.addNotExecutedTestcase("testdata.CalculatorTestPassed.testPassed1");
		engine.addNotExecutedTestcase("testdata.CalculatorTestPassed.testPassed2");
		engine.addNotExecutedTestcase("testdata.CalculatorTestPassed.testPassed3");
		engine.addNotExecutedTestcase("testdata.CalculatorTestPassed.testPassed4");
		engine.addNotExecutedTestcase("testdata.CalculatorTestFailed.testFailed1");
		engine.addNotExecutedTestcase("testdata.CalculatorTestFailed.testFailed2");
		engine.addNotExecutedTestcase("testdata.CalculatorTestFailed.testFailed3");
		engine.addNotExecutedTestcase("testdata.CalculatorTestFailed.testFailed4");
		engine.addNotExecutedTestcase("testdata.CalculatorTestFailed.testFailed5");
		engine.addNotExecutedTestcase("testdata.CalculatorTestFailed.testFailed6");
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
