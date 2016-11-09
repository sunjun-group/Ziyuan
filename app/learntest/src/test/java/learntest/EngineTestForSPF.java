package learntest;

import org.junit.Before;
import org.junit.Test;

import learntest.main.Engine;
import learntest.main.LearnTestConfig;
import sav.common.core.SavException;
import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.strategies.dto.AppJavaClassPath;

public class EngineTestForSPF extends AbstractTest {
	private Engine engine;

	@Before
	public void prepareEngine() throws ClassNotFoundException, SavException {
		AppJavaClassPath app = initAppClasspath();
		app.addClasspath(TestConfiguration.getTestTarget(LearnTestConfig.MODULE));
		engine = new Engine(app);
	}

	@Test
	public void test() throws Exception {
		engine.run(true);
	}
	
}
