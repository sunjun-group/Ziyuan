package learntest.activelearning.core.data;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.execute.value.BooleanValue;
import sav.strategies.dto.execute.value.CharValue;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.IntegerValue;

public class DpAttribute {
	private int idx;
	private ExecValue value;
	private boolean isPadding = false;
	private DpAttribute paddingConditionElement = null;
	private List<DpAttribute> paddingDependentees;
	private boolean isModifiable = true;
	
	public DpAttribute(ExecValue value, boolean isPadding, DpAttribute paddingCondition,
			int idx) {
		this.value = value;
		this.isPadding = isPadding;
		setPaddingConditionElement(paddingCondition);
		this.idx = idx;
	}
	
	public boolean isPadding() {
		return isPadding;
	}

	public void setPadding(boolean isPadding) {
		this.isPadding = isPadding;
	}

	public DpAttribute getPaddingCondition() {
		return paddingConditionElement;
	}

	public void setPaddingConditionElement(DpAttribute paddingController) {
		this.paddingConditionElement = paddingController;
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
			if (clonedAtt.paddingConditionElement != null) {
				clonedAtt.paddingConditionElement = clone[clonedAtt.paddingConditionElement.idx];
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
				paddingConditionElement, idx);
		clone.paddingDependentees = paddingDependentees;
		return clone;
	}
	
}
