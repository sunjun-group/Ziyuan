package learntest.activelearning.core.testgeneration.mutate;

public interface Mutator {
	public void mutateValue(double[] newValue, int d, boolean increaseValue, double amount);
}
