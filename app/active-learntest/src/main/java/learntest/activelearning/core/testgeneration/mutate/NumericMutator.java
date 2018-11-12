package learntest.activelearning.core.testgeneration.mutate;

public class NumericMutator implements Mutator {

	@Override
	public void mutateValue(double[] newValue, int d, boolean increaseValue, double amount, Double boundary,
			double minimumUnit){
		if (increaseValue) {
			newValue[d] += amount;
			if(boundary!=null && newValue[d] > boundary){
				newValue[d] = boundary - minimumUnit;
			}
			
		} else {
			newValue[d] -= amount;
			if(boundary!=null && newValue[d] < boundary){
				newValue[d] = boundary + minimumUnit;
			}
		}
	}

}
