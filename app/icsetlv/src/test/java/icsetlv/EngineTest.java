package icsetlv;

import icsetlv.common.dto.BkpInvariantResult;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import testdata.CalculatorTestFailed;
import testdata.CalculatorTestPassed;

public class EngineTest extends AbstractTest {

	private static final String MODULE_NAME = "icsetlv";
	private static final int DEBUG_PORT = 8787;
	private Engine engine;

	@Before
	public void prepareEngine() {
		final TestConfiguration config = TestConfiguration.getInstance();
		
		engine = new Engine().setPort(DEBUG_PORT)
				.setJavaHome(TestConfiguration.JAVA_HOME)
				.addToClassPath(config.getJavaBin())
				.addToClassPath(TestConfiguration.getTzAssembly("sav-commons"))
				.addToClassPath(TestConfiguration.getTestTarget(MODULE_NAME));
		
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
