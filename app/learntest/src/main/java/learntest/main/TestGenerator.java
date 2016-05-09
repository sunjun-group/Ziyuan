package learntest.main;

import gentest.builder.RandomTraceGentestBuilder;
import gentest.junit.TestsPrinter;
import gentest.main.GentestConstants;
import sav.common.core.SavException;
import sav.commons.TestConfiguration;

public class TestGenerator {
	
	private static String prefix = "test";
	
	public void gentest() throws ClassNotFoundException, SavException {
		RandomTraceGentestBuilder builder = new RandomTraceGentestBuilder(10);
		builder.queryMaxLength(1).testPerQuery(GentestConstants.DEFAULT_TEST_PER_QUERY);
		builder.forClass(Class.forName(LearnTestConfig.className)).method(LearnTestConfig.methodName);
		TestsPrinter printer = new TestsPrinter(LearnTestConfig.pkg, LearnTestConfig.pkg, 
				prefix, LearnTestConfig.typeName, TestConfiguration.getTestScrPath(LearnTestConfig.MODULE));
		printer.printTests(builder.generate());
	}
	
}
