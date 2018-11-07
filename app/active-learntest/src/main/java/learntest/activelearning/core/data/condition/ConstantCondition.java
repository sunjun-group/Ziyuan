package learntest.activelearning.core.data.condition;

public class ConstantCondition implements Condition {

	public int constant;
	
	public ConstantCondition(int constant) {
		this.constant = constant;
	}
	
	@Override
	public Range getDependentee(int index, double value) {
		return new Range(index+1, index+constant);
	}

}
