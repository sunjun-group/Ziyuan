package slicer.javaslicer.testdata;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SliceCollectorTestdata2 {
	
	private boolean tag = false;
	
	public String removeTag(String htmlText){
		String output = "";
		
		for(char c: htmlText.toCharArray()){
			if(c == '<'){
				tag = true;
			}
			else if(c == '>'){
				tag = false;
			}
			else if(!tag){
				output += c;
			}
		}
		
		return output;
		
	}
	
	@Test
	public void testSum() {
		SliceCollectorTestdata2 sampleProgram = new SliceCollectorTestdata2();
		String input = "<b>test</b>";
		
		String output = sampleProgram.removeTag(input);
		assertEquals("test", output);
	}
}
