package learntest;

import org.junit.Test;

import learntest.core.gentest.GentestParams;
import learntest.core.gentest.TestGenerator;
import sav.common.core.SystemVariables;
import sav.commons.AbstractTest;
import sav.strategies.dto.AppJavaClassPath;

public class TestGeneratorTest extends AbstractTest {

	@Test
	public void test() throws Exception {
		AppJavaClassPath appClasspath = new AppJavaClassPath();
		appClasspath.getPreferences().set(SystemVariables.PROJECT_CLASSLOADER, null);
		GentestParams params = new GentestParams();
		new TestGenerator(null).generateRandomTestcases(params);
	}
	
}
