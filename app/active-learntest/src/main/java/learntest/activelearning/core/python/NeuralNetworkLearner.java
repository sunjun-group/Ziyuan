package learntest.activelearning.core.python;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;

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
import sav.common.core.utils.CollectionUtils;
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
			
			Branch branch = new Branch(parent.getCfgNode(), child.getCfgNode());
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
			Branch b = new Branch(parent.getCfgNode(), node);
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
							b = new Branch(parent.getCfgNode(), p);
							if(this.branchInputMap.containsKey(b)){
								return b;
							}
							else{
								node = p;
								break;
							}
						}
					}
				} else {
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
		
		Message response = communicator.requestTraining(branch, positiveInputs, negativeInputs);
		if(response==null){
			System.err.println("the python server is closed!");
		}
		
		while(response!=null && response.getRequestType()==RequestType.$REQUEST_LABEL){
			DataPoints points = (DataPoints) response.getMessageBody();
			UnitTestSuite newSuite = this.tester.createTest(this.targetMethod, this.settings, this.appClasspath, 
					points.values, points.varList);
			
			this.testsuite.addTestCases(newSuite);
			
			CoverageSFNode branchNode = testsuite.getCoverageGraph().getNodeList().get(branch.getToNodeIdx());
			
			for (String testcase : newSuite.getJunitTestcases()) {
//				TestInputData input = this.testsuite.getInputData().get(i);
				if(branchNode.getCoveredTestcases().contains(testcase)){
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
		
		CoverageSFNode childNode = testsuite.getCoverageGraph().getNodeList().get(branch.getToNodeIdx());
		
		List<String> coveredTestcases = CollectionUtils.nullToEmpty(node.getCoveredTestcasesOnBranches().get(childNode));
		for (Entry<String, TestInputData> inputEntry : testsuite.getInputData().entrySet()) {
			if (!coveredTestcases.contains(inputEntry.getKey())) {
				negativeInputs.add(inputEntry.getValue());
			}
		}
		
		return negativeInputs;
	}

	private void generateInputByExplorationSearch(Branch parentBranch) {
		//TODO
		Boolean hasModel = false;
		Queue<Branch> queue = new LinkedList<>();
		queue.add(parentBranch);
//		while(!queue.isEmpty()){
//			Message response0 = communicator.confirmModel(parentBranch);
//			if(response0.getRequestType()!=RequestType.$CONFIRM_MODEL){
//				return;
//			}
//			
//			hasModel = (Boolean) response0.getMessageBody();
//			
//			if(hasModel){
////				Message response1 = communicator.gener	
//				break;
//			}
//			else{
//				CoverageSFlowGraph graph = this.testsuite.getCoverageGraph();
//				CoverageSFNode node = graph.getNodeList().get(parentBranch.getFromNodeIdx());
//				
//				List<Branch> newParentBranches = retrieveParents()
//			}
//		}
		
		
	}

	/**
	 * the branch <code>branch</code> is a branch of <code>decisionCDGNode</code> node  
	 * @param branch
	 * @param decisionCDGNode
	 * @return
	 */
	private List<TestInputData> generateInputByGradientSearch(Branch branch, CDGNode decisionCDGNode) {
		Branch siblingBranch = findSiblingBranch(branch);
		if(siblingBranch==null){
			return new ArrayList<>();			
		}
		
		List<TestInputData> otherInputs = this.branchInputMap.get(siblingBranch);
		if(otherInputs.isEmpty()){
			return new ArrayList<>();
		}
		else{
//			CoverageSFNode decisionNode = decisionCDGNode.getCfgNode();
			TestInputData closestInput = findClosestInput(otherInputs, decisionCDGNode);
			double bestFitness = closestInput.getFitness(decisionCDGNode);
			
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
					
					TestInputData newInput = newSuite.getInputData().values().iterator().next();
					list.add(newInput);
					if (branch.getFromNode().getCoveredBranches().contains(branch.getToNode())) {
						break;
					}
//					if(isCoverBranch(newSuite, newInput, branch)){
//						break;
//					}
					else{
						double newFitness = newInput.getFitness(decisionCDGNode);
						if(newFitness < bestFitness){
							bestFitness = newInput.getFitness(decisionCDGNode);
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

	private TestInputData findClosestInput(List<TestInputData> otherInputs, CDGNode decisionCDGNode) {
		TestInputData returnInput = null;
		double closestValue = -1;
		for(TestInputData input: otherInputs){
			if(returnInput==null){
				returnInput = input;
				closestValue = input.getFitness(decisionCDGNode);
			}
			else{
				Double value = input.getFitness(decisionCDGNode);;
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

	public Map<Branch, List<TestInputData>> buildBranchTestInputMap(Map<String, TestInputData> inputData,
			CoverageSFlowGraph coverageSFlowGraph) {
		Map<Branch, List<TestInputData>> map = new HashMap<>();
		for (CoverageSFNode node : coverageSFlowGraph.getDecisionNodes()) {
			for (CoverageSFNode branchNode : node.getBranches()) {
				List<TestInputData> list = new ArrayList<>();
				Branch branch = new Branch(node, branchNode);
				List<String> coveredTcs = node.getCoveredTestcasesOnBranches().get(branchNode);
				for (String testcase : CollectionUtils.nullToEmpty(coveredTcs)) {
					TestInputData testInput = inputData.get(testcase);
					if (testInput != null) {
						list.add(testInput);
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
