package learntest.activelearning.core.testgeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import learntest.activelearning.core.data.DpAttribute;
import learntest.activelearning.core.data.MethodInfo;
import learntest.activelearning.core.data.TestInputData;
import learntest.activelearning.core.data.UnitTestSuite;
import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.activelearning.core.testgeneration.communication.DataPoints;
import learntest.activelearning.core.testgeneration.communication.Message;
import learntest.activelearning.core.testgeneration.communication.ProcessDeadException;
import learntest.activelearning.core.testgeneration.communication.PythonCommunicator;
import learntest.activelearning.core.testgeneration.communication.RequestType;
import learntest.activelearning.core.testgeneration.localsearch.GradientBasedSearch;
import learntest.activelearning.core.utils.DomainUtils;
import microbat.instrumentation.cfgcoverage.graph.Branch;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFNode;
import microbat.instrumentation.cfgcoverage.graph.cdg.CDG;
import microbat.instrumentation.cfgcoverage.graph.cdg.CDGNode;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public class NNBasedTestGenerator extends TestGenerator {

	private PythonCommunicator communicator;
	private CDG cdg;

	public NNBasedTestGenerator(Tester tester, UnitTestSuite testsuite, PythonCommunicator communicator,
			AppJavaClassPath appClasspath, MethodInfo targetMethod, LearntestSettings settings) {
		this.testsuite = testsuite;
		this.communicator = communicator;
		this.tester = tester;
		this.appClasspath = appClasspath;
		this.targetMethod = targetMethod;
		this.settings = settings;
	}

	public void cover(CDG cdg) {
		this.branchInputMap = testsuite.getBranchInputMap();
		this.cdg = cdg;
		for (CDGNode node : cdg.getStartNodes()) {
			try{
				traverseLearning(node);				
			}
			catch(ProcessDeadException e){
				e.printStackTrace();
				break;
			}
		}
	}

	private void traverseLearning(CDGNode branchCDGNode) throws ProcessDeadException {
		GradientBasedSearch searchStategy = new GradientBasedSearch(this.branchInputMap, this.testsuite, this.tester,
				this.appClasspath, this.targetMethod, this.settings);

		List<CDGNode> decisionChildren = new ArrayList<>();
		for (CDGNode child : branchCDGNode.getChildren()) {
			if (child.getCfgNode().isConditionalNode()) {
				if (!isAllChildrenCovered(child)) {
					decisionChildren.add(child);
				}
			}

			Branch branch = new Branch(branchCDGNode.getCfgNode(), child.getCfgNode());
			List<TestInputData> inputs = this.branchInputMap.get(branch);
			if (inputs != null) {
				if (inputs.isEmpty()) {
					List<TestInputData> gradientInputs = searchStategy.generateInputByGradientSearch(branch,
							branchCDGNode);
					if (gradientInputs.isEmpty()) {
						generateInputByExplorationSearch(branch, branchCDGNode);
					}
				}

				inputs = this.branchInputMap.get(branch);
				CoverageSFNode toNode = branch.getToNode();
				CoverageSFNode stopNode = findStopNode(toNode);
				CDGNode stopCDGNode = findCorrespondingCDG(cdg, stopNode);
				if (!inputs.isEmpty() && !isAllChildrenCovered(stopCDGNode)) {
					CoverageSFNode originalParentNode = testsuite.getCoverageGraph().getNodeList()
							.get(branchCDGNode.getCfgNode().getCvgIdx());
					learnClassificationModel(branch, originalParentNode);
				}
			}
		}

		for (CDGNode decisionChild : decisionChildren) {
			traverseLearning(decisionChild);
		}
	}

	private CoverageSFNode findStopNode(CoverageSFNode toNode) {
		CoverageSFNode node = toNode;
		List<CoverageSFNode> list = new ArrayList<>();
		while(node.getBranches().size()==1 && !list.contains(node)){
			list.add(node);
			node = node.getBranches().get(0);
		}
		
		if(node.getBranches().size()==0){
			return toNode;
		}
		else if(node.getBranches().size()==2){
			return node;
		}
		else{
			return toNode;
		}
	}

	private List<Branch> findDirectParentBranches(CDGNode branchCDGNode) {
		CoverageSFNode branchCFGNode = branchCDGNode.getCfgNode();
		List<Branch> list = new ArrayList<>();
		for (CDGNode parent : branchCDGNode.getParent()) {
			CoverageSFNode parentCFGNode = parent.getCfgNode();
			for (CoverageSFNode childCFGNode : parentCFGNode.getBranches()) {
				if (canReach(childCFGNode, branchCFGNode)) {
					Branch branch = new Branch(parentCFGNode, childCFGNode);
					list.add(branch);
				}
			}
		}

		return list;
	}

	private boolean canReach(CoverageSFNode node1, CoverageSFNode node2) {
		if (node1.getCvgIdx() == node2.getCvgIdx()) {
			return true;
		}

		for (CoverageSFNode child : node1.getBranches()) {
			boolean canReach = canReach(child, node2);
			if (canReach) {
				return true;
			}
		}

		return false;
	}

	private void learnClassificationModel(Branch branch, CoverageSFNode parent) throws ProcessDeadException {
		List<TestInputData> positiveInputs = this.branchInputMap.get(branch);
		List<TestInputData> negativeInputs = retrieveNegativeInputs(branch, parent);
		System.currentTimeMillis();
		if (positiveInputs.isEmpty() || negativeInputs.isEmpty()) {
			return;
		}

		int pointNumberLimit = 50;
		Message response = communicator.requestTraining(branch, positiveInputs, negativeInputs, pointNumberLimit);
		if (response == null) {
			System.err.println("the python server is closed!");
		}

		while (response != null) {
			if(response.getRequestType() == RequestType.$REQUEST_LABEL) {
				DataPoints points = (DataPoints) response.getMessageBody();
				response = generateAndSendLabels(branch, points);					
			}
			else if(response.getRequestType() == RequestType.$REQUEST_MASK_RESULT) {
				DataPoints points = (DataPoints) response.getMessageBody();
				response = generateAndSendMaskResult(branch, points);	
			}
			else {
				break;				
			}
		}

		System.currentTimeMillis();
	}

	private Message generateAndSendMaskResult(Branch branch, DataPoints points) throws ProcessDeadException {
		List<DpAttribute[]> attributesList = points.convertToDpAttributeList();
		points.attributes = attributesList;
		
		Message response = communicator.sendMaskResult(points);
		return response;
	}

	private List<TestInputData> retrieveNegativeInputs(Branch branch, CoverageSFNode node) {
		List<TestInputData> negativeInputs = new ArrayList<>();

		CoverageSFNode childNode = testsuite.getCoverageGraph().getNodeList().get(branch.getToNodeIdx());

		List<String> coveredTestcases = CollectionUtils
				.nullToEmpty(node.getCoveredTestcasesOnBranches().get(childNode));
		for (Entry<String, TestInputData> inputEntry : testsuite.getInputData().entrySet()) {
			if (!coveredTestcases.contains(inputEntry.getKey())) {
				negativeInputs.add(inputEntry.getValue());
			}
		}

		return negativeInputs;
	}

	private void generateInputByExplorationSearch(Branch branch, CDGNode branchCDGNode) throws ProcessDeadException {
		List<Branch> trainedParentBranches = new ArrayList<>();
		findTrainedParentBranches(branchCDGNode, trainedParentBranches);

		for (Branch parentBranch : trainedParentBranches) {
			List<TestInputData> relativeData = this.branchInputMap.get(parentBranch);
			requestBoundaryExploration(this.targetMethod.getMethodId(), branch, parentBranch, relativeData);
		}

		List<TestInputData> testData = retrieveNegativeInputs(branch, branchCDGNode.getCfgNode());
		if (trainedParentBranches.isEmpty()) {
			requestBoundaryExploration(this.targetMethod.getMethodId(), branch, null, testData);
		}
	}
	
	private Message generateAndSendLabels(Branch branch, DataPoints points) throws ProcessDeadException{
		UnitTestSuite newSuite = this.tester.createTest(this.targetMethod, this.settings, this.appClasspath,
				DomainUtils.toHierachyBreakpointValue(points.values, points.varList));
		newSuite.setLearnDataMapper(testsuite.getLearnDataMapper());

		this.testsuite.addTestCases(newSuite);

		CoverageSFNode branchNode = testsuite.getCoverageGraph().getNodeList().get(branch.getToNodeIdx());

		for (String testcase : newSuite.getJunitTestcases()) {
			TestInputData input = this.testsuite.getInputData().get(testcase);
			DpAttribute[] vector = input.getDataPoint();
			DpAttribute.updatePaddingInfo(vector);
			points.attributes.add(vector);
			
			if (branchNode.getCoveredTestcases().contains(testcase)) {
				points.getLabels().add(true);
			} else {
				points.getLabels().add(false);
			}
		}

		Message response = communicator.sendLabel(points);
		return response;
	}
	

	private void requestBoundaryExploration(String methodId, Branch branch, Branch parentBranch, List<TestInputData> testData) throws ProcessDeadException {
		Message response = communicator.requestBoundaryExploration(this.targetMethod.getMethodId(), null, testData);
		while (response != null && response.getRequestType() == RequestType.$REQUEST_LABEL) {
			if(response.getRequestType() == RequestType.$REQUEST_LABEL) {
				DataPoints points = (DataPoints) response.getMessageBody();
				response = generateAndSendLabels(branch, points);					
			}
			else if(response.getRequestType() == RequestType.$REQUEST_MASK_RESULT) {
				DataPoints points = (DataPoints) response.getMessageBody();
				response = generateAndSendMaskResult(branch, points);	
			}
			else {
				break;				
			}
		}
	}

	private void findTrainedParentBranches(CDGNode branchCDGNode, List<Branch> trainedParentBranches) throws ProcessDeadException {

		List<Branch> parentBranches = findDirectParentBranches(branchCDGNode);
		for (Branch parentBranch : parentBranches) {
			Message existenceResponse = communicator.requestModelCheck(parentBranch, this.targetMethod.getMethodId());
//			System.currentTimeMillis();
			String existenceMessage = existenceResponse.getMessageBody().toString();
			if (existenceMessage.equals("TRUE")) {
				trainedParentBranches.add(parentBranch);
			} else {
				CoverageSFNode node = testsuite.getCoverageGraph().getNodeList().get(parentBranch.getFromNodeIdx());
				CDGNode CDGNode = findCorrespondingCDG(this.cdg, node);
				if (CDGNode != null) {
					findTrainedParentBranches(CDGNode, trainedParentBranches);
				}
			}
		}
	}

	private microbat.instrumentation.cfgcoverage.graph.cdg.CDGNode findCorrespondingCDG(CDG cdg2, CoverageSFNode node) {
		for (CDGNode CDGNode : cdg2.getNodeList()) {
			if (CDGNode.getCfgNode().getCvgIdx() == node.getCvgIdx()) {
				return CDGNode;
			}
		}
		return null;
	}

}
