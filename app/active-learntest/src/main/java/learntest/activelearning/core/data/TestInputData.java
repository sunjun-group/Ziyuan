package learntest.activelearning.core.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import icsetlv.common.dto.BreakpointValue;
import microbat.instrumentation.cfgcoverage.graph.Branch;
import microbat.instrumentation.cfgcoverage.graph.CoveragePath;
import microbat.instrumentation.cfgcoverage.graph.cdg.CDGNode;
import sav.strategies.dto.execute.value.ExecVar;

public class TestInputData {
	private String testcase;
	private BreakpointValue inputValue;
	private Map<Branch, Double> conditionVariationMap;
	private LearnDataSetMapper dataMapper;
	private DpAttribute[] datapoint;
	
	public TestInputData(String testcase, BreakpointValue inputValue, Map<Branch, Double> conditionVariationMap) {
		this.testcase = testcase;
		this.inputValue = inputValue;
		this.conditionVariationMap = conditionVariationMap;
	}

	public BreakpointValue getInputValue() {
		return inputValue;
	}

	class ApproachLevel {
		int level = 0;

		public ApproachLevel(int level) {
			this.level = level;
		}
	}

	public Object getCoveredPath(UnitTestSuite newSuite){
		for (CoveragePath path : newSuite.getCoverageGraph().getCoveragePaths()) {
			int testIdx = newSuite.getCoverageGraph().getCoveredTestcases().indexOf(testcase);
			if (path.getCoveredTcs().contains(testIdx)) {
				return path.getPath();
			}
		}
		
		return null;
	}

	public double getFitness(CDGNode decisionCDGNode, Branch branch) {
		Double fitness = this.conditionVariationMap.get(branch.getBranchID());

		if (fitness != null) {
			if(!branch.isCovered() && fitness==0){
				fitness = 1.0;
			}
			
			return 1 - Math.pow(1.001, -Math.abs(fitness));
		} else {
			ApproachLevel approachLevel = new ApproachLevel(1);
			Branch exercisedParent = findTheExercisedParent(decisionCDGNode, approachLevel);
			fitness = this.conditionVariationMap.get(exercisedParent);
			return approachLevel.level + (1 - Math.pow(1.001, -Math.abs(fitness)));
		}
	}

	private Branch findTheExercisedParent(CDGNode node, ApproachLevel approachLevel) {
		approachLevel.level++;
		for (CDGNode parent : node.getParent()) {
			for (Branch branchToParent : parent.getCfgNode().getBranches()) {
				if (this.conditionVariationMap.containsKey(branchToParent)) {
					return branchToParent;
				}
			}
			
			Branch p = findTheExercisedParent(parent, approachLevel);
			if (p != null) {
				return p;
			}
		}

		return null;
	}

	public boolean equals(Object obj){
		if(obj instanceof TestInputData){
			TestInputData thatInput = (TestInputData)obj;
			return thatInput.toString().equals(this.toString());
		}
		
		return false;
	}
	
	public String toString() {
		return inputValue.toString();
	}

	public String getTestcase() {
		return testcase;
	}
	
	public void setDataMapper(LearnDataSetMapper dataMapper) {
		this.dataMapper = dataMapper;
	}
	
	public DpAttribute[] getDataPoint() {
		if (datapoint == null) {
			datapoint = dataMapper.getDatapoint(inputValue);
		}
		return datapoint;
	}

	public List<ExecVar> getLearningVars() {
		List<ExecVar> vars = new ArrayList<>(getDataPoint().length);
		for (DpAttribute att : getDataPoint()) {
			vars.add(new ExecVar(att.getValue().getVarId(), att.getValue().getType()));
		}
		return vars;
	}

	public double[] getDoubleVector() {
		double[] doubleValue = new double[getDataPoint().length];
		int i = 0;
		for (DpAttribute att : getDataPoint()) {
			doubleValue[i++] = att.getValue().getDoubleVal();
		}
		return doubleValue;
	}
}
