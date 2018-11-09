package learntest.activelearning.core.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import icsetlv.common.dto.BreakpointValue;
import sav.common.core.SavRtException;
import sav.strategies.dto.execute.value.ArrayValue;
import sav.strategies.dto.execute.value.ArrayValue.ArrValueElement;
import sav.strategies.dto.execute.value.BooleanValue;
import sav.strategies.dto.execute.value.CharValue;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarHelper;
import sav.strategies.dto.execute.value.ExecVarType;
import sav.strategies.dto.execute.value.IntegerValue;
import sav.strategies.dto.execute.value.PrimitiveValue;
import sav.strategies.dto.execute.value.ReferenceValue;
import sav.strategies.dto.execute.value.StringValue;

public class LearnDataSetMapper {
	private Map<String, Integer> posMap = new HashMap<>(); // map between ExecVar.varId & its start position
	private Map<String, Integer> requireSlotsMap = new HashMap<>(); // map between ExecVar.varId & its required slots.
	private int arrSizeThreshold;
	private int size;
	private List<ExecVar> methodInputs;
	private Map<String, PrimitiveValue> defaultPaddingValues = new HashMap<>();
	private Map<String, ExecVar> varMap = new HashMap<>();
	
	LearnDataSetMapper(List<ExecVar> params, int arrSizeThreshold) {
		this.methodInputs = params;
		this.arrSizeThreshold = arrSizeThreshold;
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
			/* isNotNull, length, charArray(definedArraySize - 2) elements */
			size = arrSizeThreshold;
		} else if (type == ExecVarType.ARRAY) {
			/* isNull, length, arrayElements */
			int arrElementSlots = 0;
			for (ExecVar arrEleVar : var.getChildren()) {
				arrElementSlots += calculateRequireSlot(arrEleVar);
			}
			size = 1/*isNull*/ + 1/*length*/ + arrElementSlots;
		} else if (type == ExecVarType.REFERENCE) {
			for (ExecVar field : var.getChildren()) {
				size += calculateRequireSlot(field);
			}
			size = size + 1; // for isNull
		} else { // primitive type
			size = 1;
		}
		requireSlotsMap.put(var.getVarId(), size);
		varMap.put(var.getVarId(), var);
		return size;
	}
	
	private int assignPos(ExecVar var, int pos) {
		posMap.put(var.getVarId(), pos);
		ExecVarType type = var.getType();
		int childPos = pos;
		if (type == ExecVarType.ARRAY) {
			posMap.put(var.getIsNullChildId(), childPos++);
			posMap.put(var.getLengthChildId(), childPos++);
		} else if (type == ExecVarType.REFERENCE) {
			posMap.put(var.getIsNullChildId(), childPos++);
		}
		for (ExecVar child : var.getChildren()) {
			childPos = assignPos(child, childPos);
		}
		return pos + requireSlotsMap.get(var.getVarId());
	}

	public DpAttribute[] getDatapoint(BreakpointValue bkpValue) {
		DpAttribute[] dp = initDpAttributes();
		for (ExecValue value : bkpValue.getChildren()) {
			fillDatapoint(value, dp, posMap.get(value.getVarId()));
		}
		return dp;
	}
	
	public DpAttribute[] initDpAttributes() {
		DpAttribute[] dp = new DpAttribute[size];
		for (ExecVar var : methodInputs) {
			initDatapoint(dp, var, null);
		}
		return dp;
	}
	
	private void initDatapoint(DpAttribute[] dp, ExecVar var, DpAttribute paddingController) {
		ExecVarType type = var.getType();
		String varId = var.getVarId();
		int sPos = posMap.get(varId);
		int pos = sPos; 
		if (type == ExecVarType.STRING) {
			/* isNull, length, charArray */
			DpAttribute isNullAttr = new DpAttribute(new BooleanValue(var.getIsNullChildId(), true), true, paddingController, pos++);
			isNullAttr.setObjectNullPaddingCondition();
			DpAttribute lengthAttr = new DpAttribute(new IntegerValue(var.getLengthChildId(), 0), true, isNullAttr, pos++);
			lengthAttr.setLengthPaddingCondition();
			dp[isNullAttr.getIdx()] = isNullAttr;
			dp[lengthAttr.getIdx()] = lengthAttr;
			for (int i = 0; i < (arrSizeThreshold - 2); i++, pos++) {
				dp[pos] = new DpAttribute(new CharValue(var.getElementId(i), Character.MIN_VALUE), true, lengthAttr, pos);
			}
		} else if (type == ExecVarType.ARRAY) {
			/* isNull, length, arrayElements */
			DpAttribute isNullAttr = new DpAttribute(new BooleanValue(var.getIsNullChildId(), true), true, paddingController, pos++);
			isNullAttr.setObjectNullPaddingCondition();
			DpAttribute lengthAttr = new DpAttribute(new IntegerValue(var.getLengthChildId(), 0), true, isNullAttr, pos++);
			lengthAttr.setLengthPaddingCondition();
			dp[isNullAttr.getIdx()] = isNullAttr;
			dp[lengthAttr.getIdx()] = lengthAttr;
			for (ExecVar arrEleVar : var.getChildren()) {
				initDatapoint(dp, arrEleVar, lengthAttr);
			}
		} else if (type == ExecVarType.REFERENCE) {
			/* isNotNull, fields */
			DpAttribute isNullAttr = new DpAttribute(new BooleanValue(var.getIsNullChildId(), true), true, paddingController, pos++);
			isNullAttr.setObjectNullPaddingCondition();
			dp[isNullAttr.getIdx()] = isNullAttr;
			for (ExecVar arrEleVar : var.getChildren()) {
				initDatapoint(dp, arrEleVar, isNullAttr);
			}
		} else {
			PrimitiveValue defaultPaddingValue = defaultPaddingValues.get(varId);
			if (defaultPaddingValue == null) {
				defaultPaddingValue = PrimitiveValue.valueOf(var, 0);
				defaultPaddingValues.put(varId, defaultPaddingValue);
			}
			dp[pos] = new DpAttribute(defaultPaddingValue, true, paddingController, pos);
			pos++;
		}
	}
	
	private int fillDatapoint(ExecValue execVal, DpAttribute[] dp, int spos) {
		ExecVarType type = execVal.getType();
		int pos = spos;
		DpAttribute paddingCond = null;
		if (type == ExecVarType.STRING) {
			/* isNull, length, charArray */
			String strVal = execVal.getStrVal();
			DpAttribute isNullAttr = dp[pos++];
			if (strVal != null) {
				isNullAttr.setBoolean(false);
				DpAttribute lengthAttr = dp[pos++].setInt(strVal.length());
				int realEleSize = Math.min(strVal.length(), (arrSizeThreshold - 2));
				int i = 0;
				for (; i < realEleSize; i++) {
					dp[pos++].setChar(strVal.charAt(i));
				}
				paddingCond = lengthAttr;
			} else {
				paddingCond = isNullAttr;
			}
		} else if (type == ExecVarType.ARRAY) {
			/* isNull, length, arrayElements */
			ArrayValue arrayValue = (ArrayValue) execVal;
			DpAttribute isNullAttr = dp[pos++];
			if (!arrayValue.isNull()) {
				isNullAttr.setBoolean(false);
				DpAttribute lengthAttr = dp[pos++].setInt(arrayValue.getLength());
				int realEleSize = Math.min(arrayValue.getLength(), (arrSizeThreshold));
				int i = 0;
				ArrValueElement[] arrElements = arrayValue.getElementArray(realEleSize);
				for (; i < realEleSize; i++) {
					ArrValueElement ele = arrElements[i];
					ExecValue eleValue = null;
					if (ele == null) {
						ExecVar eleVar = varMap.get(ExecVarHelper.getArrayElementID(execVal.getVarId(), i));
						if (eleVar.getType() == ExecVarType.STRING) {
							ele = new ArrValueElement(i, new StringValue(eleVar.getVarId(), null));
						} else if (eleVar.getType() == ExecVarType.REFERENCE) {
							ele = new ArrValueElement(i, new ReferenceValue(eleVar.getVarId(), true));
						} else if (eleVar.getType() == ExecVarType.ARRAY) {
							ele = new ArrValueElement(i, new ArrayValue(eleVar.getVarId(), true));
						}
					} else {
						eleValue = ele.getValue();
					}
					pos = fillDatapoint(eleValue, dp, posMap.get(ele.getValue().getVarId()));
				}
				paddingCond = lengthAttr;
			} else {
				paddingCond = isNullAttr;
			}
		} else if (type == ExecVarType.REFERENCE) {
			DpAttribute isNullAttr = dp[pos++];
			ReferenceValue refValue = (ReferenceValue) execVal;
			if (refValue.isNull()) {
				paddingCond = isNullAttr;
			} else {
				isNullAttr.setBoolean(false);
				for (ExecValue fieldValue : refValue.getChildren()) {
					pos = fillDatapoint(fieldValue, dp, posMap.get(fieldValue.getVarId()));
				}
			}
		} else {
			dp[pos].setValue(execVal); 
			dp[pos].setPadding(false);
			pos++;
		}
		if (paddingCond != null) {
			int endPos = requireSlotsMap.get(execVal.getVarId()) + spos;
			while(pos < endPos) {
				dp[pos++].setPaddingConditionElement(paddingCond);
			}
		}
		return pos;
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
	
	private ExecValue appendValue(ExecVar var, ExecValue parent, double[] dp) {
		ExecVarType type = var.getType();
		String varId = var.getVarId();
		ExecValue value = null;
		int pos = posMap.get(varId);
		if (type == ExecVarType.STRING) {
			/* isNotNull, length, charArray */
			double isNotNull = dp[pos++];
			if (isNotNull >= 0) {
				int length = (int) dp[pos++];
				length = Math.min(length, arrSizeThreshold);
				char[] content = new char[length];
				for (int i = 0; i < length; i++) {
					content[i] = (char) dp[pos++];
				}
				value = new StringValue(varId, String.valueOf(content));
			} else {
				value = new StringValue(varId, null);
			}
		} else if (type == ExecVarType.ARRAY) {
			/* isNotNull, length, arrayElements */
			double isNotNull = dp[pos++];
			if (isNotNull >= 0) {
				ArrayValue arrValue = new ArrayValue(varId, false);
				value = arrValue;
				int length = (int) dp[pos++];
				arrValue.setLength(length);
				int size = Math.min(length, arrSizeThreshold);
				for (int i = 0; i < size; i++) {
					ExecVar eleVar = var.getChildren().get(i);
					appendValue(eleVar, arrValue, dp);
				}
			} else {
				value = new ArrayValue(varId, true);
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
			value.setValueType(var.getValueType());
			if (parent != null) {
				parent.add(value);
			}
		}
		return value;
	}

	/**
	 * TODO-LLT separate vector properties & its values.
	 */
	public List<DpAttribute[]> toDpAttributeVector(List<ExecVar> varList, List<double[]> values) {
		List<DpAttribute[]> list = new ArrayList<>(values.size());
		for (double[] value : values) {
			DpAttribute[] dp = initDpAttributes();
			int i = 0;
			for (ExecVar var : varList) {
				double varValue = value[i++];
				DpAttribute dpAttribute = dp[posMap.get(var.getVarId())];
				dpAttribute.setValue(PrimitiveValue.valueOf(var, varValue));
				dpAttribute.setModifiable(true);
			}
			DpAttribute.updatePaddingInfo(dp);
			list.add(dp);
		}
		
		return list;
	}

}
