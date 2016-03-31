package learntest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import icsetlv.DefaultValues;
import learntest.main.Engine;
import learntest.testcase.TestcasesExecutorwithLoopTimes;
import sav.common.core.Pair;
import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import testdata.MutipleSelectionTest;

public class EngineTest extends AbstractTest {
	private Engine engine;

	@Before
	public void prepareEngine() {
		AppJavaClassPath app = initAppClasspath();
		app.addClasspath(TestConfiguration.getTestTarget("learntest"));
		engine = new Engine(app);
		engine.setTcExecutor(new TestcasesExecutorwithLoopTimes(DefaultValues.DEBUG_VALUE_RETRIEVE_LEVEL));
	}
	
	/*@Test
	public void testCalculator() throws Exception{
		engine.setTarget("/testdata/Calculator.txt", "Calculator", "testdata.Calculator","getMax");
		engine.addTestcases(CalculatorTestPassed.class.getName());
		engine.addTestcases(CalculatorTestFailed.class.getName());
		engine.createCFG();
		List<Pair<Integer, Integer>> decisions = new ArrayList<Pair<Integer,Integer>>();
		decisions.add(new Pair<Integer, Integer>(7, 8));
		decisions.add(new Pair<Integer, Integer>(10, 11));
		engine.addBreakpoints(decisions);
		engine.run();
	}*/

	@Test
	public void testMutipleSelection() throws Exception {
		engine.setTarget("/testdata/MutipleSelection.txt", "MutipleSelection", "testdata.MutipleSelection","test");
		engine.addTestcases(MutipleSelectionTest.class.getName());
		engine.createCFG();
		List<Pair<Integer, Integer>> decisions = new ArrayList<Pair<Integer,Integer>>();
		decisions.add(new Pair<Integer, Integer>(6, 6));
		decisions.add(new Pair<Integer, Integer>(7, 8));
		decisions.add(new Pair<Integer, Integer>(10, 11));
		engine.setStructure(decisions);
		engine.addEntryBreakpoint(new BreakPoint("testdata.MutipleSelection", "test", 6));
		engine.run(true);
	}

}
