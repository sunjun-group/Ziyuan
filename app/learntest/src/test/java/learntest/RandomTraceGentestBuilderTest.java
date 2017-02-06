package learntest;

import org.junit.Test;

import gentest.builder.RandomTraceGentestBuilder;
import gentest.junit.TestsPrinter;
import sav.common.core.SavException;

public class RandomTraceGentestBuilderTest {

	@Test
	public void test() throws SavException, ClassNotFoundException {
		RandomTraceGentestBuilder builder = new RandomTraceGentestBuilder(10);
		builder.queryMaxLength(1).testPerQuery(1);
		builder.forClass(Class.forName("testdata.MutipleSelection")).method("test");
		TestsPrinter printer = new TestsPrinter("testdata.test.mutipleselection", 
				"testdata.test.mutipleselection", "test", "MutipleSelection", "./src/test/java");
		printer.printTests(builder.generate());
	}
	
}
