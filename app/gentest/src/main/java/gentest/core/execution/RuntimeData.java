/**
 * Copyright TODO
 */
package gentest.core.execution;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LLT
 *
 */
public class RuntimeData {
	private int firstVarId;
	/* 
	 * list to store executed value for each varId 
	 * variableValuesList.get(varId) = value of variable with id varId
	 * */
	private List<Object> variableValuesList;
	
	public RuntimeData() {
		this(0);
	}
	
	public RuntimeData(int firstVarId) {
		variableValuesList = new ArrayList<Object>();
	}

	public void reset() {
		variableValuesList.clear();
	}
	
	private int translate(int varId) {
		return varId - firstVarId;
	}

	public void addExecData(int varIdx, Object value) {
		int varId = translate(varIdx);
		if (varId < 0 || varId > variableValuesList.size()) {
			throw new IllegalArgumentException("varId is invalid!!");
		}
		if (varId == variableValuesList.size()) {
			variableValuesList.add(value);
		} else {
			variableValuesList.set(varId, value);
		}
	}

	public Object getExecData(int varIdx) {
		int varId = translate(varIdx);
		if (isInvalid(varId)) {
			return null;
		}
		return variableValuesList.get(varId);
	}

	private boolean isInvalid(int varId) {
		return varId < 0 || varId >= variableValuesList.size();
	}
	
	public void setFirstVarId(int firstVarId) {
		this.firstVarId = firstVarId;
	}
}
