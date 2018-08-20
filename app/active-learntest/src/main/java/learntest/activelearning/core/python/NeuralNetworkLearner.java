package learntest.activelearning.core.python;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.utils.BreakpointDataUtils;
import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.model.TestInputData;
import learntest.activelearning.core.model.UnitTestSuite;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.core.commons.data.classinfo.MethodInfo;
import microbat.instrumentation.cfgcoverage.graph.Branch;
import microbat.instrumentation.cfgcoverage.graph.CoveragePath;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFNode;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFlowGraph;
import microbat.instrumentation.cfgcoverage.graph.cdg.CDG;
import microbat.instrumentation.cfgcoverage.graph.cdg.CDGNode;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class NeuralNetworkLearner {
	
	private Map<Branch, List<TestInputData>> branchInputMap = new HashMap<>();
	private UnitTestSuite testsuite;
	private PythonCommunicator communicator;
	
	public void learningToCover(CDG cdg) {
		this.branchInputMap = buildBranchTestInputMap(testsuite.getInputData(), testsuite.getCoverageGraph());
		for(CDGNode node: cdg.getStartNodes()){
			traverseLearning(node, null);			
		}
	}
	
	private void traverseLearning(CDGNode parent, Branch parentBranch) {
		List<CDGNode> decisionChildren = new ArrayList<>();
		for(CDGNode child: parent.getChildren()){
			if(isAllChildrenCovered(child)){
				continue;
			}
			
			if(child.getCfgNode().isConditionalNode()){
				decisionChildren.add(child);
			}
			
			Branch branch = new Branch(parent.getCfgNode().getId(), child.getCfgNode().getId());
			List<TestInputData> inputs = this.branchInputMap.get(branch);
			if(inputs!=null){
				if(inputs.isEmpty()){
					List<TestInputData> gradientInputs = generateInputByGradientSearch(branch, parent);
					if(gradientInputs.isEmpty()){
						generateInputByExplorationSearch(parentBranch);
					}
				}
				
				inputs = this.branchInputMap.get(branch);
				if(!inputs.isEmpty()){
					CoverageSFNode originalParentNode = testsuite.getCoverageGraph().getNodeList().get(parent.getCfgNode().getCvgIdx());
					learnClassificationModel(branch, originalParentNode);
				}
			}
		}
		
		for(CDGNode decisionChild: decisionChildren){
			Branch b = findParentBranch(parent, decisionChild);
			traverseLearning(decisionChild, b);
		}
	}
	
	private Branch findParentBranch(CDGNode parent, CDGNode decisionChild){
		CoverageSFNode node = decisionChild.getCfgNode();
		while(node!=null && node.getCvgIdx()!=parent.getCfgNode().getCvgIdx()){
			Branch b = new Branch(parent.getCfgNode().getCvgIdx(), node.getCvgIdx());
			if(this.branchInputMap.containsKey(b)){
				return b;
			}
			else{
				if(node.getParents().size()==1){
					node = node.getParents().get(0);
				}
				else if(node.getParents().size()>1){
					for(CoverageSFNode p: node.getParents()){
						if(isChildOf(p, parent)){
							b = new Branch(parent.getCfgNode().getCvgIdx(), p.getCvgIdx());
							if(this.branchInputMap.containsKey(b)){
								return b;
							}
							else{
								node = p;
								break;
							}
						}
					}
				}
				else{
					node = null;
				}
			}
		}
		
		return null;
	}

	private boolean isChildOf(CoverageSFNode node, CDGNode parent) {
		for(CDGNode c: parent.getChildren()){
			if(c.getCfgNode().getCvgIdx()==node.getCvgIdx()){
				return true;
			}
		}
		return false;
	}



	private void learnClassificationModel(Branch branch, CoverageSFNode parent) {
		List<TestInputData> positiveInputs = this.branchInputMap.get(branch);
		List<TestInputData> negativeInputs = retrieveNegativeInputs(branch, parent);
		System.currentTimeMillis();
		if(positiveInputs.isEmpty() || negativeInputs.isEmpty()){
			return;
		}
		
		//TODO
		Message response = communicator.requestTraining(branch, positiveInputs, negativeInputs);
		while(response.getRequestType()==RequestType.$REQUEST_LABEL){
			DataPoints points = (DataPoints) response.getMessageBody();
			UnitTestSuite newSuite = this.tester.createTest(this.targetMethod, this.settings, this.appClasspath, 
					points.values, points.varList);
			
			int start = this.testsuite.getInputData().size();
			int length = newSuite.getInputData().size();
			
			this.testsuite.addTestCases(newSuite);
			
			CoverageSFNode branchNode = testsuite.getCoverageGraph().getNodeList().get(branch.getToNodeIdx());
			
			for(int i=start; i<=start+length-1; i++){
//				TestInputData input = this.testsuite.getInputData().get(i);
				if(branchNode.getCoveredTestcases().contains(i)){
					points.getLabels().add(true);
				}
				else{
					points.getLabels().add(false);
				}
			}
			
			response = communicator.sendLabel(points);
		}
		
		
		System.currentTimeMillis();
	}

	private List<TestInputData> retrieveNegativeInputs(Branch branch, CoverageSFNode node) {
		List<TestInputData> negativeInputs = new ArrayList<>();
		
		CoverageSFNode childNode = this.testsuite.getCoverageGraph().getNodeList().get(branch.getToNodeIdx());
		
		List<Integer> inputIndexes = node.getCoveredTestcasesOnBranches().get(childNode);
		for(int i=0; i<this.testsuite.getInputData().size(); i++){
			if(inputIndexes!=null && !inputIndexes.contains(i)){
				negativeInputs.add(this.testsuite.getInputData().get(i));
			}
		}
		
		return negativeInputs;
	}

	private void generateInputByExplorationSearch(Branch parentBranch) {
//		communicator.requestBundaryExploration(parentBranch);
		
	}

	private List<TestInputData> generateInputByGradientSearch(Branch branch, CDGNode parent) {
		Branch siblingBranch = findSiblingBranch(branch);
		if(siblingBranch==null){
			return new ArrayList<>();			
		}
		
		List<TestInputData> otherInputs = this.branchInputMap.get(siblingBranch);
		if(otherInputs.isEmpty()){
			return new ArrayList<>();
		}
		else{
			CoverageSFNode decisionNode = parent.getCfgNode();
			TestInputData closestInput = findClosestInput(otherInputs, decisionNode);
			double bestFitness = closestInput.getConditionVariationMap().get(decisionNode.getCvgIdx());
			
			List<BreakpointValue> l = new ArrayList<>();
			l.add(closestInput.getInputValue());
			List<ExecVar> vars = BreakpointDataUtils.collectAllVars(l);
			
			List<TestInputData> list = new ArrayList<>();
			double[] value = closestInput.getInputValue().getAllValues();
			for(int i=0; i<vars.size(); i++){
				
				boolean currentDirection = true;
				boolean previousDirection = currentDirection;
				int amount = 1;
				
				while(true){
					double[] newValue = value.clone();
					adjustValue(newValue, i, currentDirection, amount);
					
					List<double[]> inputData = new ArrayList<>();
					inputData.add(newValue);
					UnitTestSuite newSuite = this.tester.createTest(this.targetMethod, this.settings, this.appClasspath, 
							inputData, vars);
					this.testsuite.addTestCases(newSuite);
					
					TestInputData newInput = newSuite.getInputData().get(0);
					list.add(newInput);
					
					if(isCoverBranch(newSuite, newInput, branch)){
						break;
					}
					else{
						double newFitness = newInput.getConditionVariationMap().get(decisionNode);
						if(newFitness < bestFitness){
							bestFitness = newInput.getConditionVariationMap().get(decisionNode);
							amount *= 2;
							continue;
						}
						else{
							if(previousDirection!=currentDirection){
								break;
							}
							
							previousDirection = currentDirection;
							currentDirection = !currentDirection;
							amount = 1;
						}
					}
				}
				
			}
			
			return list;
		}
	}

	private void adjustValue(double[] newValue, int d, boolean increaseValue, int amount) {
		if(increaseValue){
			newValue[d] += amount; 			
		}
		else{
			newValue[d] -= amount; 	
		}
	}

	private boolean isCoverBranch(UnitTestSuite newSuite, TestInputData newInput1, Branch branch) {
		CoveragePath path = newSuite.getCoverageGraph().getCoveragePaths().get(0);
		for(CoverageSFNode node: path.getPath()){
			if(node.getCvgIdx()==branch.getToNodeIdx()){
				return true;
			}
		}
		return false;
	}

	private TestInputData findClosestInput(List<TestInputData> otherInputs, CoverageSFNode decisionNode) {
		TestInputData returnInput = null;
		double closestValue = -1;
		for(TestInputData input: otherInputs){
			if(returnInput==null){
				returnInput = input;
				closestValue = input.getConditionVariationMap().get(decisionNode.getCvgIdx());
			}
			else{
				Double value = input.getConditionVariationMap().get(decisionNode.getCvgIdx());
				if(closestValue>value){
					closestValue = value;
					returnInput = input;
				}
			}
			
		}
		
		return returnInput;
	}

	private Branch findSiblingBranch(Branch branch) {
		for(Branch b: branchInputMap.keySet()){
			if(b.getFromNodeIdx()==branch.getFromNodeIdx() &&
					b.getToNodeIdx()!=branch.getToNodeIdx()){
				return b;
			}
		}
		return null;
	}

	private boolean isAllChildrenCovered(CDGNode node) {
		if(!node.getCfgNode().isCovered()){
			return false; 
		}
		
		for(CDGNode child: node.getChildren()){
			boolean covered = isAllChildrenCovered(child);
			if(!covered){
				return false;
			}
		}
		
		return true;
	}

	public Map<Branch, List<TestInputData>> buildBranchTestInputMap(List<TestInputData> testInputs,
			CoverageSFlowGraph coverageSFlowGraph){
		Map<Branch, List<TestInputData>> map = new HashMap<>();
		for (CoverageSFNode node : coverageSFlowGraph.getDecisionNodes()) {
			for (CoverageSFNode branchNode : node.getBranches()) {
				List<TestInputData> list = new ArrayList<>();
				Branch branch = new Branch(node.getCvgIdx(), branchNode.getCvgIdx());
				
				List<Integer> coveredTcs = node.getCoveredTestcasesOnBranches().get(branchNode);
				if (coveredTcs != null) {
					for(Integer inputID: coveredTcs){
						list.add(testInputs.get(inputID));
					}
				}
				map.put(branch, list);
				
			}
		}
		
		return map;
		
	}
	
	private Tester tester;
	private AppJavaClassPath appClasspath;
	private MethodInfo targetMethod;
	private LearntestSettings settings;
	
	public NeuralNetworkLearner(Tester tester, UnitTestSuite testsuite, PythonCommunicator communicator, 
			AppJavaClassPath appClasspath, MethodInfo targetMethod, LearntestSettings settings) {
		this.testsuite = testsuite;
		this.communicator = communicator;
		this.tester = tester;
		this.appClasspath = appClasspath;
		this.targetMethod = targetMethod;
		this.settings = settings;
	}
	
}
