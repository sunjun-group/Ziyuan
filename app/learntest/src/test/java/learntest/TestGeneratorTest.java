package learntest;

import org.junit.Test;

import learntest.main.TestGenerator;
import sav.common.core.SystemVariables;
import sav.commons.AbstractTest;
import sav.strategies.dto.AppJavaClassPath;

public class TestGeneratorTest extends AbstractTest {

	@Test
	public void test() throws Exception {
		AppJavaClassPath appClasspath = new AppJavaClassPath();
		appClasspath.getPreferences().set(SystemVariables.PROJECT_CLASSLOADER, null);
		new TestGenerator(null).genTest();
	}
	
}
