package learntest.activelearning.core.model;

import java.util.Map;

import icsetlv.common.dto.BreakpointValue;

public class TestInputData {
	private BreakpointValue inputValue;
	private Map<Integer, Double> conditionVariationMap;

	public TestInputData(BreakpointValue inputValue, Map<Integer, Double> conditionVariationMap) {
		super();
		this.inputValue = inputValue;
		this.conditionVariationMap = conditionVariationMap;
	}

	public BreakpointValue getInputValue() {
		return inputValue;
	}

	public Map<Integer, Double> getConditionVariationMap() {
		return conditionVariationMap;
	}

}
