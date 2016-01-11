package microbat.codeanalysis.runtime.herustic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HeuristicIgnoringFieldRule {
	
	/**
	 * this map store <className, list<fieldName>>, specifying which fields will be
	 * ignored in which class.
	 */
	private static Map<String, ArrayList<String>> ignoringMap = new HashMap<>();
	
	static{
		String c1 = "java.util.ArrayList";
		
		ArrayList<String> fieldList1 = new ArrayList<>();
		fieldList1.add("serialVersionUID");
		fieldList1.add("DEFAULT_CAPACITY");
		fieldList1.add("EMPTY_ELEMENTDATA");
		fieldList1.add("DEFAULTCAPACITY_EMPTY_ELEMENTDATA");
		fieldList1.add("MAX_ARRAY_SIZE");
		fieldList1.add("modCount");
		
		ignoringMap.put(c1, fieldList1);
		
		
	}
	
	public static boolean isForIgnore(String className, String fieldName){
		ArrayList<String> fields = ignoringMap.get(className);
		if(fields != null){
			return fields.contains(fieldName);			
		}
		
		return false;
	}
}
