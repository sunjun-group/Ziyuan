/**
 * Copyright TODO
 */
package gentest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LLT
 *
 */
public class RuntimeData {
	/* map to store executed value for each varId */
	private Map<Integer, Object> variableValues;
	
	public RuntimeData() {
		variableValues = new HashMap<Integer, Object>();
	}

	public void reset() {
		variableValues.clear();
	}

	public void addExecData(int varId, Object value) {
		variableValues.put(varId, value);
	}

	public Object getExecData(int varId) {
		return variableValues.get(varId);
	}
}
