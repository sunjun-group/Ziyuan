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
	
	public DpAttribute(ExecValue value, boolean isPadding, DpAttribute paddingCondtion,
			int idx) {
		this.value = value;
		this.isPadding = isPadding;
		setPaddingCondition(paddingCondtion);
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

	public void setPaddingCondition(DpAttribute paddingController) {
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
	
}
