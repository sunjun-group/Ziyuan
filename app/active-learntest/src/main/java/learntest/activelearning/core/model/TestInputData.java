package learntest.activelearning.core.model;

import java.util.Map;

import icsetlv.common.dto.BreakpointValue;

public class TestInputData {
	private String testcase;
	private BreakpointValue inputValue;
	private Map<Integer, Double> conditionVariationMap;

	public TestInputData(String testcase, BreakpointValue inputValue, Map<Integer, Double> conditionVariationMap) {
		this.testcase = testcase;
		this.inputValue = inputValue;
		this.conditionVariationMap = conditionVariationMap;
	}

	public BreakpointValue getInputValue() {
		return inputValue;
	}

	public Map<Integer, Double> getConditionVariationMap() {
		return conditionVariationMap;
	}

	public String toString(){
		return inputValue.toString();
	}
	
	public String getTestcase() {
		return testcase;
	}
}
