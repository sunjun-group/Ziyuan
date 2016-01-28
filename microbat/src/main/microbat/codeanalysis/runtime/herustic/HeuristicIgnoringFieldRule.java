package microbat.codeanalysis.runtime.herustic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.jdi.ClassType;
import com.sun.jdi.InterfaceType;

@SuppressWarnings("restriction")
public class HeuristicIgnoringFieldRule {
	
	/**
	 * this map store <className, list<fieldName>>, specifying which fields will be
	 * ignored in which class.
	 */
	private static Map<String, ArrayList<String>> ignoringMap = new HashMap<>();
	
	private static List<String> prefixExcludes = new ArrayList<>();
	
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
		
		String[] excArray = new String[]{"java.", "javax.", "sun.", "com.sun.", "org.junit."};
		for(String exc: excArray){
			prefixExcludes.add(exc);
		}
	}
	
	public static boolean isForIgnore(String className, String fieldName){
		ArrayList<String> fields = ignoringMap.get(className);
		if(fields != null){
			return fields.contains(fieldName);			
		}
		
		return false;
	}

	/**
	 * For some JDK class, we do not need its detailed fields. However, we may still be
	 * interested in the elements in Collection class.
	 * @param type
	 * @return
	 */
	public static boolean isNeedParsingFields(ClassType type) {
		String typeName = type.name();
		
		if(containPrefix(typeName, prefixExcludes)){
			for(InterfaceType interf: type.interfaces()){
				if(interf.name().contains("java.util.Collection")){
					return true;
				}
			}
			
			return false;
		}
		
		return true;
	}
	
	private static boolean containPrefix(String name, List<String> prefixList){
		for(String prefix: prefixList){
			if(name.startsWith(prefix)){
				return true;
			}
		}
		return false;
	}
}
