package learntest.activelearning.core.data.condition;

public interface Condition {
	Range getDependentee(int index, double value);
}
