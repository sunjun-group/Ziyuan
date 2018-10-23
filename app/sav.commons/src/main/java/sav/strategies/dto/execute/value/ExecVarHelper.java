package sav.strategies.dto.execute.value;

public class ExecVarHelper {
	private ExecVarHelper(){}
	
	public static String getArrayElementID(String varID, int idx) {
		return String.format("%s[%s]", varID, idx);
	}

	public static String getFieldId(String parentId, String fieldName) {
		return String.format("%s.%s", parentId, fieldName);
	}
}
