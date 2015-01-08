/**
 * Copyright TODO
 */
package gentest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LLT
 *
 */
public class RuntimeData {
	/* map to store executed value for each varId */
	private List<Object> variableValuesList;
	
	public RuntimeData() {
		variableValuesList = new ArrayList<Object>();
	}

	public void reset() {
		variableValuesList.clear();
	}

	public void addExecData(int varId, Object value) {
		if (varId < 0 || varId > variableValuesList.size()) {
			throw new IllegalArgumentException("varId is invalid!!");
		}
		if (varId == variableValuesList.size()) {
			variableValuesList.add(value);
		} else {
			variableValuesList.set(varId, value);
		}
	}

	public Object getExecData(int varId) {
		if (isInvalid(varId)) {
			return null;
		}
		return variableValuesList.get(varId);
	}

	private boolean isInvalid(int varId) {
		return varId < 0 || varId >= variableValuesList.size();
	}
}
