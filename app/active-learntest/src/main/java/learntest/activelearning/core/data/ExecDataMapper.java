package learntest.activelearning.core.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import icsetlv.common.dto.BreakpointValue;
import sav.common.core.utils.ArrayTypeUtils;
import sav.strategies.dto.execute.value.ArrayValue;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;
import sav.strategies.dto.execute.value.ReferenceValue;

public class ExecDataMapper {
	private Map<String, Integer> posMap = new HashMap<>(); // map between ExecVar.varId & its start position
	private Map<String, Integer> requireSlotsMap = new HashMap<>(); // map between ExecVar.varId & its required slots.
	private int k;
	private int size;
	
	public ExecDataMapper(List<ExecVar> params, int k) {
		for (ExecVar var : params) {
			calculateRequireSlot(var);
		}
		int pos = 0;
		for (ExecVar var : params) {
			pos = assignPos(var, pos);
		}
		this.size = pos;
	}
	
	private int calculateRequireSlot(ExecVar var) {
		ExecVarType type = var.getType();
		int size = 0;
		if (type == ExecVarType.STRING) {
			size = k;
		} else if (type == ExecVarType.ARRAY) {
			int eleSlots = calculateRequireSlot(var.getChildren().get(0));
			String valueType = var.getValueType();
			size = (int) Math.pow(k, ArrayTypeUtils.getArrayDimension(valueType));
			size = size * eleSlots + 2; // for isNull & length
		} else if (type == ExecVarType.REFERENCE) {
			for (ExecVar field : var.getChildren()) {
				size += calculateRequireSlot(field);
			}
			size = size + 1; // for isNull
		} else {
			size = 1;
		}
		return size;
	}
	
	private int assignPos(ExecVar var, int pos) {
		posMap.put(var.getVarId(), pos);
		ExecVarType type = var.getType();
		int childPos = pos;
		if (type == ExecVarType.ARRAY) {
			childPos += 2;
		} else if (type == ExecVarType.REFERENCE) {
			childPos += 1;
		}
		for (ExecVar child : var.getChildren()) {
			childPos = assignPos(child, childPos);
		}
		return pos + requireSlotsMap.get(var.getVarId());
	}
	
	public String[] toDatapoint(BreakpointValue bkpValue) {
		String[] dp = new String[size];
		for (ExecValue execVal : bkpValue.getChildren()) {
			fillDatapoint(execVal, dp, posMap.get(execVal.getVarId()));
		}
		return dp;
	}

	private void fillDatapoint(ExecValue execVal, String[] dp, int pos) {
		ExecVarType type = execVal.getType();
		if (type == ExecVarType.STRING) {
			String strVal = execVal.getStrVal();
			if (strVal == null) {
				dp[pos++] = "0";
			} else {
				dp[pos++] = "1";
				dp[pos++] = String.valueOf(strVal.length());
				for (int i = 0; i < (k - 2); i++) {
					dp[pos++] = String.valueOf(strVal.charAt(i));
				}
			}
		} else if (type == ExecVarType.ARRAY) {
			ArrayValue arrayValue = (ArrayValue) execVal;
			dp[pos++] = arrayValue.isNull() ? "0" : "1";
			dp[pos++] = String.valueOf(arrayValue.getLengthValue()); 
			int maxEleSize = (int) Math.pow(k, ArrayTypeUtils.getArrayDimension(arrayValue.getValueType()));
			for (int i = 0; i < maxEleSize; i++) {
				ExecValue elementValue = arrayValue.getElementByFlattenLocation(i); // TODO-LLT: impl the function.
				if (elementValue != null) {
					fillDatapoint(elementValue, dp, pos);
				}
				// TODO: calculate next pos.
			}
		} else if (type == ExecVarType.REFERENCE) {
			ReferenceValue refValue = (ReferenceValue) execVal;
			dp[pos++] = refValue.isNull() ? "0" : "1";
			for (ExecValue fieldValue : refValue.getChildren()) {
				fillDatapoint(fieldValue, dp, posMap.get(fieldValue.getVarId()));
			}
		} else {
			dp[pos] = execVal.getStrVal();
		}
	}
	
//	public BreakpointValue toHierachyBreakpointValue(double[] solution) {
//		BreakpointValue value = new BreakpointValue();
//		for (int i = 0; i < vars.size(); i++) {
//			value.append(vars.get(i).getVarId(), 0, vars.get(i), solution[i]);
//		}
//		return value;
//	}
	
}
