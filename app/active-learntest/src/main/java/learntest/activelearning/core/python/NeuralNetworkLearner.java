package learntest.activelearning.core.python;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cfg.CfgNode;
import learntest.activelearning.core.model.TestInputData;
import learntest.activelearning.core.model.UnitTestSuite;
import microbat.instrumentation.cfgcoverage.graph.Branch;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFNode;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFlowGraph;
import microbat.instrumentation.cfgcoverage.graph.cdg.CDG;
import microbat.instrumentation.cfgcoverage.graph.cdg.CDGNode;
import sav.common.core.SavException;
import sav.strategies.vm.interprocess.InputDataWriter;
import sav.strategies.vm.interprocess.python.PythonVmConfiguration;
import sav.strategies.vm.interprocess.python.PythonVmRunner;

/**
 * @author LLT
 *
 */
public class NeuralNetworkLearner {
	
	private Map<Branch, List<TestInputData>> branchInputMap = new HashMap<>();
	private UnitTestSuite testsuite;
	
	private InputDataWriter inputWriter;
	private OutputDataReader outputReader;
	private PythonVmRunner vmRunner;
	private long timeout = -1;
	
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
					List<TestInputData> gradientInputs = generateInputByGradientSearch();
					if(gradientInputs.isEmpty()){
						generateInputByParentBranch(parentBranch);
						generateInputByExplorationSearch();
					}
				}
				
				inputs = this.branchInputMap.get(branch);
				if(!inputs.isEmpty()){
					learnClassificationModel(branch, child.getCfgNode());
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



	private void learnClassificationModel(Branch branch, CoverageSFNode node) {
		List<TestInputData> positiveInputs = this.branchInputMap.get(branch);
		List<TestInputData> negativeInputs = retrieveNegativeInputs(branch, node);
		
		if(positiveInputs.isEmpty() || negativeInputs.isEmpty()){
			return;
		}
		
		
	}

	private List<TestInputData> retrieveNegativeInputs(Branch branch, CoverageSFNode node) {
		List<TestInputData> negativeInputs = new ArrayList<>();
		
		List<Integer> inputIndexes = node.getCoveredTestcasesOnBranches().get(branch);
		for(int i=0; i<this.testsuite.getInputData().size(); i++){
			if(!inputIndexes.contains(i)){
				negativeInputs.add(this.testsuite.getInputData().get(i));
			}
		}
		
		return negativeInputs;
	}

	private void generateInputByExplorationSearch() {
		// TODO Auto-generated method stub
		
	}

	private void generateInputByParentBranch(Branch parentBranch) {
		// TODO Auto-generated method stub
		
	}

	private List<TestInputData> generateInputByGradientSearch() {
		// TODO Auto-generated method stub
		return new ArrayList<>();
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
	
	public NeuralNetworkLearner() {
		// init vm configuration
		inputWriter = new InputDataWriter();
		outputReader = new OutputDataReader();
	}
	
	public NeuralNetworkLearner(UnitTestSuite testsuite) {
		this.testsuite = testsuite;
//		this.branchInputMap = CoverageUtils.buildBranchTestInputMap(testsuite.getInputData(), testsuite.getCoverageGraph());
	}

	public void start() throws SavException {
		inputWriter.open();
		outputReader.open();
		vmRunner = new PythonVmRunner(inputWriter, outputReader, true);
		vmRunner.setTimeout(timeout);
		PythonVmConfiguration vmConfig = new PythonVmConfiguration();
		vmConfig.setPythonHome("C:\\Program Files\\Python36");
		vmConfig.setLaunchClass("E:\\linyun\\git_space\\nn_active_learning\\nn_learntest.py");
		vmRunner.start(vmConfig);
	}
	
	public void startTrainingMethod(String methodName) {
		inputWriter.request(InputData.createStartMethodRequest(methodName));
	}
	
	public void stop() {
		vmRunner.stop();
	}
	
	public void setVmTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	public List<double[]> boundaryRemaining(Dataset pathCoverage) {
		inputWriter.request(InputData.forBoundaryRemaining(pathCoverage));
		OutputData output = outputReader.readOutput();
		return output.getDataSet().getCoveredData();
	}

}
