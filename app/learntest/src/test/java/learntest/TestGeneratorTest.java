package learntest;

import org.junit.Test;

import learntest.main.TestGenerator;
import sav.commons.AbstractTest;

public class TestGeneratorTest extends AbstractTest {

	@Test
	public void test() throws Exception {
		new TestGenerator().genTest();
	}
	
}
