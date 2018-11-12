package sav.strategies.dto.execute.value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecVarHelper {
	private static Logger log = LoggerFactory.getLogger(ExecVarHelper.class);
	private ExecVarHelper(){}
	
	public static String getArrayElementID(String arrayVarId, int[] location) {
		StringBuilder sb = new StringBuilder(arrayVarId);
		for (int i = 0; i < location.length; i++) {
			sb.append("[").append(location[i]).append("]");
		}
		return sb.toString();
	}
	
	public static int[] getArrayElementLocation(String eleVarId, String arrayVarId, int dimension) {
		int[] loc = new int[dimension]; 
		String locStr = eleVarId.substring(arrayVarId.length());
		int s = locStr.indexOf("[");
		int i = 0;
		while (s >= 0) {
			int e = locStr.indexOf("]");
			int idx = -1;
			try {
				idx = Integer.valueOf(locStr.substring(s + 1, e));
			} catch(Exception ex) {
				log.debug(ex.getMessage());
			}
			loc[i++] = idx;
			locStr = locStr.substring(e + 1);
			s = locStr.indexOf("[");
		}
		return loc;
	}
	
	public static String getArrayElementID(String varID, int idx) {
		return getArrayChildID(varID, String.valueOf(idx));
	}
	
	public static String getArrayChildID(String varID, String childCode) {
		return String.format("%s[%s]", varID, childCode);
	}

	public static String getFieldId(String parentId, String fieldName) {
		return String.format("%s.%s", parentId, fieldName);
	}

	public static String getStringChildId(String varId, int charIdx) {
		return String.format("%s{%s}", varId, charIdx);
	}

}
