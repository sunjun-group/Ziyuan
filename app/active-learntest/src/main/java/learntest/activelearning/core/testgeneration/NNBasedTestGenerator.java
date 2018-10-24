package learntest.activelearning.core.testgeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import learntest.activelearning.core.data.MethodInfo;
import learntest.activelearning.core.data.TestInputData;
import learntest.activelearning.core.data.UnitTestSuite;
import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.activelearning.core.testgeneration.localsearch.GradientBasedSearch;
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
public class NNBasedTestGenerator extends TestGenerator{

	private PythonCommunicator communicator;
	
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
		this.branchInputMap = buildBranchTestInputMap(testsuite.getInputData(), testsuite.getCoverageGraph());
		for (CDGNode node : cdg.getStartNodes()) {
			traverseLearning(node);
		}
	}

	private void traverseLearning(CDGNode branchCDGNode) {
		GradientBasedSearch searchStategy = new GradientBasedSearch(this.branchInputMap, this.testsuite, this.tester,
			this.appClasspath, this.targetMethod, this.settings);
		
		List<CDGNode> decisionChildren = new ArrayList<>();
		for (CDGNode child : branchCDGNode.getChildren()) {
			if (isAllChildrenCovered(child)) {
				continue;
			}

			if (child.getCfgNode().isConditionalNode()) {
				decisionChildren.add(child);
			}

			Branch branch = new Branch(branchCDGNode.getCfgNode(), child.getCfgNode());
			List<TestInputData> inputs = this.branchInputMap.get(branch);
			if (inputs != null) {
				if (inputs.isEmpty()) {
					List<TestInputData> gradientInputs = searchStategy.generateInputByGradientSearch(branch, branchCDGNode);
					if (gradientInputs.isEmpty()) {
						generateInputByExplorationSearch(branch, branchCDGNode);
					}
				}

				inputs = this.branchInputMap.get(branch);
				if (!inputs.isEmpty()) {
					CoverageSFNode originalParentNode = testsuite.getCoverageGraph().getNodeList()
							.get(branchCDGNode.getCfgNode().getCvgIdx());
					learnClassificationModel(branch, originalParentNode);
				}
			}
		}

		for (CDGNode decisionChild : decisionChildren) {
//			Branch b = findParentBranch(parentNode, decisionChild);
			traverseLearning(decisionChild);
		}
	}

	private void learnClassificationModel(Branch branch, CoverageSFNode parent) {
		List<TestInputData> positiveInputs = this.branchInputMap.get(branch);
		List<TestInputData> negativeInputs = retrieveNegativeInputs(branch, parent);
		System.currentTimeMillis();
		if (positiveInputs.isEmpty() || negativeInputs.isEmpty()) {
			return;
		}

		Message response = communicator.requestTraining(branch, positiveInputs, negativeInputs);
		if (response == null) {
			System.err.println("the python server is closed!");
		}

		while (response != null && response.getRequestType() == RequestType.$REQUEST_LABEL) {
			DataPoints points = (DataPoints) response.getMessageBody();
			UnitTestSuite newSuite = this.tester.createTest(this.targetMethod, this.settings, this.appClasspath,
					points.values, points.varList);
			this.testsuite.addTestCases(newSuite);

			CoverageSFNode branchNode = testsuite.getCoverageGraph().getNodeList().get(branch.getToNodeIdx());

			for (String testcase : newSuite.getJunitTestcases()) {
				// TestInputData input = this.testsuite.getInputData().get(i);
				if (branchNode.getCoveredTestcases().contains(testcase)) {
					points.getLabels().add(true);
				} else {
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

		List<String> coveredTestcases = CollectionUtils
				.nullToEmpty(node.getCoveredTestcasesOnBranches().get(childNode));
		for (Entry<String, TestInputData> inputEntry : testsuite.getInputData().entrySet()) {
			if (!coveredTestcases.contains(inputEntry.getKey())) {
				negativeInputs.add(inputEntry.getValue());
			}
		}

		return negativeInputs;
	}

	private void generateInputByExplorationSearch(Branch branch, CDGNode parent) {
		
//		List<Branch> parentBranches = findParentsWithModel(branch);
//		
//		
//		
//		for(Branch parentBranch: parentBranches){
//			communicator.requestBoundaryExploration(parentBranch, testData);
//		}

	}

}
