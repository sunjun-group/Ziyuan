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
				.setLaunchClass(config.getJunitcore())
				.addToClassPath(config.getJavaBin())
				.addToClassPath(config.getJunitLib())
				.addToClassPath(TestConfiguration.getTestTarget(MODULE_NAME));
	}

	@Test
	public void doTest() throws Exception {
		engine.addPassedTestcase("testdata.CalculatorTestPassed");
		engine.addFailedTestcase("testdata.CalculatorTestFailed");

		engine.addBreakPoint("testdata.Calculator", "getMax", 7, "x", "y", "max");

		engine.run();
		final List<Result> results = engine.getResults();
		for (Result result : results) {
			System.out.println(result);
		}
	}

}
