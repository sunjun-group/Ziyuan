package learntest.activelearning.core.testgeneration.mutate;

public class NumericMutator implements Mutator {

	@Override
	public void mutateValue(double[] newValue, int d, boolean increaseValue, double amount){
		if (increaseValue) {
			newValue[d] += amount;
		} else {
			newValue[d] -= amount;
		}
		
		if(amount == 0.001){
			double normalizedValue = Double.parseDouble(String.format("%.3f", newValue[d]));
			newValue[d] = normalizedValue;
		}
	}

}
