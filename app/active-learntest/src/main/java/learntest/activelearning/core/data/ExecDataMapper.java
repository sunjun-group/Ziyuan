package learntest.activelearning.core.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gentest.core.value.generator.ArrayWalker;
import icsetlv.common.dto.BreakpointValue;
import sav.common.core.SavRtException;
import sav.common.core.utils.ArrayTypeUtils;
import sav.strategies.dto.execute.value.MultiDimArrayValue;
import sav.strategies.dto.execute.value.MultiDimArrayValue.ArrayValueElement;
import sav.strategies.dto.execute.value.PrimitiveValue;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarHelper;
import sav.strategies.dto.execute.value.ExecVarType;
import sav.strategies.dto.execute.value.ReferenceValue;
import sav.strategies.dto.execute.value.StringValue;

public class ExecDataMapper {
	private Map<String, Integer> posMap = new HashMap<>(); // map between ExecVar.varId & its start position
	private Map<String, Integer> requireSlotsMap = new HashMap<>(); // map between ExecVar.varId & its required slots.
	private int definedArraySize;
	private int size;
	private List<ExecVar> methodInputs;
	
	public ExecDataMapper(List<ExecVar> params, int k) {
		for (ExecVar var : params) {
			calculateRequireSlot(var);
		}
		int pos = 0;
		for (ExecVar var : params) {
			pos = assignPos(var, pos);
		}
		this.size = pos;
		this.methodInputs = params;
	}
	
	private int calculateRequireSlot(ExecVar var) {
		ExecVarType type = var.getType();
		int size = 0;
		if (type == ExecVarType.STRING) {
			size = definedArraySize;
		} else if (type == ExecVarType.ARRAY) {
			int arrElementSlots = 0;
			for (ExecVar arrEleVar : var.getChildren()) {
				arrElementSlots += calculateRequireSlot(arrEleVar);
			}
			String valueType = var.getValueType();
			int arrayDimension = ArrayTypeUtils.getArrayDimension(valueType);
			size = 1 + arrayDimension /*for isNull & arrayDimensionSize */ + arrElementSlots;
		} else if (type == ExecVarType.REFERENCE) {
			for (ExecVar field : var.getChildren()) {
				size += calculateRequireSlot(field);
			}
			size = size + 1; // for isNull
		} else { // primitive type
			size = 1;
		}
		requireSlotsMap.put(var.getVarId(), size);
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
		// TODO filling padding elements, 
		// & define ExecType
		return dp;
	}

	private void fillDatapoint(ExecValue execVal, String[] dp, int pos) {
		ExecVarType type = execVal.getType();
		if (type == ExecVarType.STRING) {
			/* isNotNull, length, charArray */
			String strVal = execVal.getStrVal();
			if (strVal == null) {
				dp[pos++] = "0";
			} else {
				dp[pos++] = "1";
				dp[pos++] = String.valueOf(strVal.length());
				for (int i = 0; i < (definedArraySize - 2); i++) {
					dp[pos++] = String.valueOf(strVal.charAt(i));
				}
			}
		} else if (type == ExecVarType.ARRAY) {
			/* isNotNull, dimension, arrayDimensionSize, (k^dim) arrayElements */
			MultiDimArrayValue arrayValue = (MultiDimArrayValue) execVal;
			dp[pos++] = arrayValue.isNull() ? "0" : "1";
			dp[pos++] = String.valueOf(arrayValue.getDimension());
			for (int i = 0; i < arrayValue.getDimension(); i++) {
				dp[pos++] = String.valueOf(arrayValue.getLength()[i]);
			}
			for (ArrayValueElement arrayElementValue : arrayValue.getElements()) {
				fillDatapoint(arrayElementValue.getValue(), dp, posMap.get(arrayElementValue.getVarId()));
			}
		} else if (type == ExecVarType.REFERENCE) {
			/* isNotNull, fields */
			ReferenceValue refValue = (ReferenceValue) execVal;
			dp[pos++] = refValue.isNull() ? "0" : "1";
			for (ExecValue fieldValue : refValue.getChildren()) {
				fillDatapoint(fieldValue, dp, posMap.get(fieldValue.getVarId()));
			}
		} else {
			dp[pos] = execVal.getStrVal();
		}
	}
	
	public BreakpointValue toHierachyBreakpointValue(double[] dp) {
		if (dp.length != size) {
			throw new SavRtException("invalid Datapoint array!!");
		}
		BreakpointValue bkpValue = new BreakpointValue();
		for (ExecVar var : methodInputs) {
			appendValue(var, bkpValue, dp);
		}
		return bkpValue;
	}
	
	private void appendValue(ExecVar var, ExecValue parent, double[] dp) {
		ExecVarType type = var.getType();
		String varId = var.getVarId();
		ExecValue value = null;
		int pos = posMap.get(varId);
		if (type == ExecVarType.STRING) {
			/* isNotNull, length, charArray */
			double isNotNull = dp[pos++];
			if (isNotNull >= 0) {
				int length = (int) dp[pos++];
				length = Math.min(length, definedArraySize);
				char[] content = new char[length];
				for (int i = 0; i < length; i++) {
					content[i] = (char) dp[pos++];
				}
				value = new StringValue(varId, String.valueOf(content));
			} else {
				value = new StringValue(varId, null);
			}
		} else if (type == ExecVarType.ARRAY) {
			/* isNotNull, dimension, arrayDimensionSize, (k^dim) arrayElements */
			double isNotNull = dp[pos++];
			if (isNotNull >= 0) {
				value = new MultiDimArrayValue(varId, false);
				int dimension = (int) dp[pos++];
				int[] arrDimSize = new int[dimension];
				for(int i = 0; i < dimension; i++) {
					arrDimSize[i] = (int) dp[pos++];
					arrDimSize[i] = Math.min(arrDimSize[i], definedArraySize);
				}
				Set<String> definedElementIds = new HashSet<>();
				int[] curLoc = ArrayWalker.next(null, arrDimSize);
				while (curLoc != null) {
					definedElementIds.add(ExecVarHelper.getArrayElementID(varId, curLoc));
				}
				for (ExecVar eleVar : var.getChildren()) {
					if (definedElementIds.contains(eleVar.getVarId())) {
						appendValue(eleVar, value, dp);
					}
				}
			} else {
				value = new MultiDimArrayValue(varId, true);
			}
		} else if (type == ExecVarType.REFERENCE) {
			/* isNotNull, fields */
			double isNotNull = dp[pos++];
			if (isNotNull >= 0) {
				value = new ReferenceValue(varId, false);
				for (ExecVar fieldVar : var.getChildren()) {
					appendValue(fieldVar, value, dp);
				}
			} else {
				value = new ReferenceValue(varId, true);
			}
		} else { // primitive type
			value = PrimitiveValue.valueOf(var, dp[pos]);
		}
		if (value != null) {
			parent.add(value);
		}
	}
	
}
