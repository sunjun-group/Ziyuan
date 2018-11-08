package learntest.activelearning.core.data;

import java.util.ArrayList;
import java.util.List;

import learntest.activelearning.core.data.condition.Condition;
import learntest.activelearning.core.data.condition.LengthCondition;
import learntest.activelearning.core.data.condition.ObjectNullCondition;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.execute.value.BooleanValue;
import sav.strategies.dto.execute.value.CharValue;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.IntegerValue;

public class DpAttribute {
	private int idx;
	private ExecValue value;
	private boolean isPadding = false;
	private DpAttribute paddingController = null;
	private List<DpAttribute> paddingDependentees;
	private Condition paddingCondition; // apply for its dependentees.
	
	private boolean isModifiable = true;
	
	public DpAttribute(ExecValue value, boolean isPadding, DpAttribute paddingController,
			int idx) {
		this.value = value;
		this.isPadding = isPadding;
		setPaddingConditionElement(paddingController);
		this.idx = idx;
	}
	
	public boolean isPadding() {
		return isPadding;
	}

	public void setPadding(boolean isPadding) {
		this.isPadding = isPadding;
	}

	public DpAttribute getPaddingConditionElement() {
		return paddingController;
	}

	public void setPaddingConditionElement(DpAttribute paddingController) {
		this.paddingController = paddingController;
		if (paddingController != null) {
			if (paddingController.paddingDependentees == null) {
				paddingController.paddingDependentees = new ArrayList<>();
			}
			paddingController.paddingDependentees.add(this);
		}
	}
	
	public List<DpAttribute> getPaddingDependentees() {
		return CollectionUtils.nullToEmpty(paddingDependentees);
	}

	public ExecValue getValue() {
		return value;
	}

	public void setValue(ExecValue value) {
		this.value = value;
	}

	public boolean isModifiable() {
		return isModifiable;
	}

	public void setModifiable(boolean isModifiable) {
		this.isModifiable = isModifiable;
	}

	public Boolean getBoolean() {
		if (!(value instanceof BooleanValue)) {
			throw new IllegalArgumentException(String.format("expect BooleanValue, get %s", value.getClass().getName()));
		}
		return ((BooleanValue) value).getBooleanVal();
	}
	
	public DpAttribute setBoolean(boolean newValue) {
		setValue(new BooleanValue(value.getVarId(), newValue));
		setPadding(false);
		return this;
	}

	public void setChar(char newValue) {
		setValue(new CharValue(value.getVarId(), newValue));
		setPadding(false);
	}

	public DpAttribute setInt(int newValue) {
		setValue(new IntegerValue(value.getVarId(), newValue));
		setPadding(false);
		return this;
	}
	
	public Integer getInt() {
		if (!(value instanceof IntegerValue)) {
			throw new IllegalArgumentException(String.format("expect IntegerValue, get %s", value.getClass().getName()));
		}
		return ((IntegerValue) value).getIntegerVal();
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
	
	public int getIdx() {
		return idx;
	}
	
	public static DpAttribute[] deepClone(DpAttribute[] dpAttribute) {
		DpAttribute[] clone = new DpAttribute[dpAttribute.length];
		for (int i = 0; i < dpAttribute.length; i++) {
			clone[i] = dpAttribute[i].clone();
		}
		for (DpAttribute clonedAtt : clone) {
			if (clonedAtt.paddingController != null) {
				clonedAtt.paddingController = clone[clonedAtt.paddingController.idx];
			}
			if (!clonedAtt.getPaddingDependentees().isEmpty()) {
				List<DpAttribute> clonedDependentees = new ArrayList<>(clonedAtt.paddingDependentees.size());
				for (DpAttribute dependentee : clonedAtt.getPaddingDependentees()) {
					clonedDependentees.add(clone[dependentee.idx]);
				}
				clonedAtt.paddingDependentees = clonedDependentees;
			}
		}
		return clone;
	}
	
	public DpAttribute clone() {
		DpAttribute clone = new DpAttribute(value.clone(), isPadding, 
				paddingController, idx);
		clone.paddingDependentees = paddingDependentees;
		return clone;
	}

	public Condition getPaddingCondition() {
		return paddingCondition;
	}

	public void setObjectNullPaddingCondition() {
		this.paddingCondition = new ObjectNullCondition(this);
	}
	
	public void setLengthPaddingCondition() {
		this.paddingCondition = new LengthCondition(this);
	}
	
	public static void updatePaddingInfo(DpAttribute[] dpAttributes) {
		for (DpAttribute dpAttribute : dpAttributes) {
			/* only update from the root element */
			if (dpAttribute.paddingController != null && dpAttribute.paddingCondition != null) {
				dpAttribute.paddingCondition.updatePadding();
			}
		}
	}
}
