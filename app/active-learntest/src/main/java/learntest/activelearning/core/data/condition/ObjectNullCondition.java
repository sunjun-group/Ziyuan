package learntest.activelearning.core.data.condition;

import learntest.activelearning.core.data.DpAttribute;

public class ObjectNullCondition extends Condition {

	public ObjectNullCondition(DpAttribute isNullAttribute) {
		super(isNullAttribute);
	}

	@Override
	protected boolean[] calculateDependenteePadding() {
		boolean[] paddings = new boolean[owner.getPaddingDependentees().size()];
		boolean padding = owner.isPadding();
		if (!padding) {
			padding = Boolean.FALSE.equals(owner.getBoolean()) ? false : true;
		}
		for (int i = 0; i < paddings.length; i++) {
			paddings[i] = padding;
		}
		return paddings;
	}

}
