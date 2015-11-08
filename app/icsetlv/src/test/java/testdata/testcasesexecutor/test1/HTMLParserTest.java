package testdata.testcasesexecutor.test1;

import org.junit.Test;

public class HTMLParserTest {

	@Test
	public void test() {
		String input = "<b>test</b>";
		
		HTMLParser parser = new HTMLParser();
		String output = parser.removeTag(input);
		
		System.out.println(output);
	}

}
