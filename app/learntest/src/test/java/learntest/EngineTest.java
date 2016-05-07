package learntest;

import org.junit.Before;
import org.junit.Test;

import icsetlv.DefaultValues;
import learntest.main.Engine;
import learntest.testcase.TestcasesExecutorwithLoopTimes;
import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.strategies.dto.AppJavaClassPath;

public class EngineTest extends AbstractTest {
	private Engine engine;

	@Before
	public void prepareEngine() {
		AppJavaClassPath app = initAppClasspath();
		app.addClasspath(TestConfiguration.getTestTarget("learntest"));
		app.setTestTarget("./src/test/java");
		engine = new Engine(app);
		engine.setTcExecutor(new TestcasesExecutorwithLoopTimes(DefaultValues.DEBUG_VALUE_RETRIEVE_LEVEL));
	}

	@Test
	public void testMutipleSelection() throws Exception {
		engine.setTarget("D:/git/Ziyuan/app/learntest/src/test/java/testdata/MutipleSelection.java", 
				"MutipleSelection", "testdata.MutipleSelection","test");
		engine.run();
	}

}
