package learntest.activelearning.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import icsetlv.common.dto.BreakpointValue;
import microbat.instrumentation.cfgcoverage.graph.cdg.CDGNode;

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

	private Map<Integer, Double> getConditionVariationMap() {
		return conditionVariationMap;
	}
	
	class ApproachLevel{
		int level = 0;
		public ApproachLevel(int level) {
			this.level = level;
		}
	}
	
	public double getFitness(CDGNode decisionCDGNode) {
		int decisionNodeID = decisionCDGNode.getCfgNode().getCvgIdx();
		Double fitness = this.conditionVariationMap.get(decisionNodeID);
		if(fitness != null) {
			return 1 - Math.pow(1.001, -Math.abs(fitness));
		}
		else {
			ApproachLevel approachLevel = new ApproachLevel(1);
			CDGNode exercisedParent = findTheExercisedParent(decisionCDGNode, approachLevel);
			fitness = this.conditionVariationMap.get(exercisedParent.getCfgNode().getCvgIdx());
			return approachLevel.level + (1 - Math.pow(1.001, -Math.abs(fitness)));
		}
	}

	private CDGNode findTheExercisedParent(CDGNode node, ApproachLevel approachLevel) {
		approachLevel.level++;
		for(CDGNode parent: node.getParent()) {
			if(this.conditionVariationMap.containsKey(parent.getCfgNode().getCvgIdx())) {
				return parent;
			}
			else {
				CDGNode p = findTheExercisedParent(parent, approachLevel);
				if(p!=null) {
					return p;
				}
			}
		}
		
		return null;
	}

	public String toString(){
		return inputValue.toString();
	}
	
	public String getTestcase() {
		return testcase;
	}
}
