package icsetlv;

import icsetlv.common.dto.BkpInvariantResult;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.strategies.dto.AppJavaClassPath;
import testdata.CalculatorTestFailed;
import testdata.CalculatorTestPassed;

public class EngineTest extends AbstractTest {
	private Engine engine;

	@Before
	public void prepareEngine() {
		AppJavaClassPath app = initAppClasspath();
		app.addClasspath(TestConfiguration.getTestTarget(ICSETLV));
		engine = new Engine(app);
	}

	@Test
	public void doTest() throws Exception {
		engine.addTestcases(CalculatorTestPassed.class.getName());
		engine.addTestcases(CalculatorTestFailed.class.getName());
		engine.addBreakPoint("testdata.Calculator", "getMax", 6, "x", "y");

		List<BkpInvariantResult> result = engine.run();
		printList(result);
	}

}
