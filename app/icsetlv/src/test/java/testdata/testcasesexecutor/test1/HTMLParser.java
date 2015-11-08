package testdata.testcasesexecutor.test1;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.collections.map.HashedMap;

public class HTMLParser {
	private boolean tag = false;
	
//	private ArrayList<String> strList = new ArrayList<>();
//	private HashMap<String, String> map = new HashMap<>();
//	
//	private int[] array = new int[]{1, 2, 3};
	private Organization org = new Organization();
	public HTMLParser(){
		
//		strList.add("a");
//		strList.add("b");
//		strList.add("c");
//		
//		map.put("a", "aa");
//		map.put("b", "bb");
//		map.put("c", "cc");
	}
	
	
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
}
