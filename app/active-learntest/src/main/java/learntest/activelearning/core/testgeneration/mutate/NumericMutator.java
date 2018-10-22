package learntest.activelearning.core.testgeneration.mutate;

public class NumericMutator implements Mutator {

	@Override
	public void mutateValue(double[] newValue, int d, boolean increaseValue, double amount) {
		if (increaseValue) {
			newValue[d] += amount;
		} else {
			newValue[d] -= amount;
		}
	}

}
