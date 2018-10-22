package learntest.activelearning.core.testgeneration.localsearch;

import java.util.List;

import learntest.activelearning.core.model.TestInputData;

class IntermediateSearchResult {
	double[] bestValue;
	double bestFitness;

	List<TestInputData> inputList;

	public IntermediateSearchResult(double[] bestValue, double bestFitness, List<TestInputData> inputList) {
		super();
		this.bestValue = bestValue;
		this.bestFitness = bestFitness;
		this.inputList = inputList;
	}
}
