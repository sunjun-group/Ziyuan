package learntest.activelearning.core.data.condition;

import learntest.activelearning.core.data.DpAttribute;

public class LengthCondition extends Condition {

	public LengthCondition(DpAttribute lengthAttribute) {
		super(lengthAttribute);
	}

	@Override
	protected boolean[] calculateDependenteePadding() {
		boolean[] paddings = new boolean[owner.getPaddingDependentees().size()];
		Integer length = owner.getInt();
		if(owner.isPadding() || length == null || length < 0) { // padding
			for (int i = 0; i < paddings.length; i++) {
				paddings[i] = true;
			}
		}
		int realEles = Math.min(length, paddings.length);
		int i = 0;
		for (; i < realEles; i++) {
			paddings[i] = false;
		}
		for (; i < paddings.length; i++) {
			paddings[i] = true;
		}
		return paddings; 
	}

}
