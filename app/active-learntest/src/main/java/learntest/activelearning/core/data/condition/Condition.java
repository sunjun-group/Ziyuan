package learntest.activelearning.core.data.condition;

import learntest.activelearning.core.data.DpAttribute;

public abstract class Condition {
	protected DpAttribute owner;
	
	protected Condition(DpAttribute owner) {
		this.owner = owner;
	}
	
	public void updatePadding() {
		boolean[] paddings = calculateDependenteePadding();
		System.currentTimeMillis();
		int i = 0;
		for (DpAttribute dependentee : owner.getPaddingDependentees()) {
			boolean isPadding = paddings[i++];
			dependentee.setPadding(isPadding);
			if (dependentee.getPaddingCondition() != null && !dependentee.getPaddingDependentees().isEmpty()) {
				dependentee.getPaddingCondition().updatePadding();
			}
		}
	}

	/**
	 * return the array correspondent to owner's dependentees which indicate whether it is a padding element or not
	 * */
	protected abstract boolean[] calculateDependenteePadding();
}
