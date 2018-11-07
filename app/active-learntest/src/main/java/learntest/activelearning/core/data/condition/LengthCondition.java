package learntest.activelearning.core.data.condition;

public class LengthCondition implements Condition {

	@Override
	public Range getDependentee(int index, double value) {
		return new Range(index+1, (int)(index+value));
	}

}
