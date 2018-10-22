package learntest.activelearning.core.testgeneration;

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
import sav.strategies.dto.execute.value.BooleanValue;
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
		for (CDGNode node : cdg.getStartNodes()) {
			traverseLearning(node);
		}
	}

	private void traverseLearning(CDGNode branchCDGNode) {
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
					List<TestInputData> gradientInputs = generateInputByGradientSearch(branch, branchCDGNode);
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

	private Branch findParentBranch(CDGNode parent, CDGNode decisionChild) {
		CoverageSFNode node = decisionChild.getCfgNode();
		while (node != null && node.getCvgIdx() != parent.getCfgNode().getCvgIdx()) {
			Branch b = new Branch(parent.getCfgNode(), node);
			if (this.branchInputMap.containsKey(b)) {
				return b;
			} else {
				if (node.getParents().size() == 1) {
					node = node.getParents().get(0);
				} else if (node.getParents().size() > 1) {
					for (CoverageSFNode p : node.getParents()) {
						if (isChildOf(p, parent)) {
							b = new Branch(parent.getCfgNode(), p);
							if (this.branchInputMap.containsKey(b)) {
								return b;
							} else {
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
		for (CDGNode c : parent.getChildren()) {
			if (c.getCfgNode().getCvgIdx() == node.getCvgIdx()) {
				return true;
			}
		}
		return false;
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

	/**
	 * the branch <code>branch</code> is a branch of
	 * <code>decisionCDGNode</code> node
	 * 
	 * @param branch
	 * @param branchCDGNode
	 * @return
	 */
	private List<TestInputData> generateInputByGradientSearch(Branch branch, CDGNode branchCDGNode) {
		Branch siblingBranch = findSiblingBranch(branch);
		if (siblingBranch == null) {
			return new ArrayList<>();
		}

		List<TestInputData> otherInputs = this.branchInputMap.get(siblingBranch);
		if (otherInputs.isEmpty()) {
			return new ArrayList<>();
		} else {
			TestInputData closestInput = findClosestInput(otherInputs, branchCDGNode, branch);
			List<BreakpointValue> l = new ArrayList<>();
			l.add(closestInput.getInputValue());
			List<ExecVar> vars = BreakpointDataUtils.collectAllVars(l);

			List<TestInputData> list = new ArrayList<>();
			double[] value = closestInput.getInputValue().getAllValues();

			double[] bestValue = value;
			double bestFitness = closestInput.getFitness(branchCDGNode, branch);

			for (int index = 0; index < vars.size(); index++) {

				IntermediateSearchResult iResult = null;

				ExecVar var = vars.get(index);
				switch (var.getType()) {
				case INTEGER:
				case BYTE:
				case CHAR:
				case LONG:
				case SHORT:
					iResult = doIntegerSearch(bestValue, bestFitness, index, vars, list, branch, branchCDGNode);
					break;
				case DOUBLE:
				case FLOAT:
					iResult = doDoubleSearch(bestValue, bestFitness, index, vars, list, branch, branchCDGNode);
					break;
				case BOOLEAN:
					iResult = doBooleanSearch(bestValue, bestFitness, index, vars, list, branch, branchCDGNode);
					break;
				case STRING:
					iResult = doStringSearch(bestValue, bestFitness, index, vars, list, branch, branchCDGNode);
					break;
				default:
					break;
				}

				if (iResult != null) {
					bestFitness = iResult.bestFitness;
					bestValue = iResult.bestValue;
				}

			}

			return list;
		}
	}

	private IntermediateSearchResult doStringSearch(double[] bestValue, double bestFitness, int index,
			List<ExecVar> vars, List<TestInputData> list, Branch branch, CDGNode decisionCDGNode) {
		// TODO Auto-generated method stub
		return null;
	}

	private IntermediateSearchResult doBooleanSearch(double[] bestValue, double bestFitness, int index,
			List<ExecVar> vars, List<TestInputData> list, Branch branch, CDGNode decisionCDGNode) {
		double[] newValue = bestValue.clone();
		if (newValue[index] == 1.0) {
			newValue[index] = 0.0;
		} else {
			newValue[index] = 1.0;
		}

		List<double[]> inputData = new ArrayList<>();
		inputData.add(newValue);
		UnitTestSuite newSuite = this.tester.createTest(this.targetMethod, this.settings, this.appClasspath, inputData,
				vars);
		this.testsuite.addTestCases(newSuite);

		TestInputData newInput = null;
		while (newInput == null) {
			try {
				newInput = newSuite.getInputData().values().iterator().next();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (!branch.isCovered()) {
			double newFitness = newInput.getFitness(decisionCDGNode, branch);
			if (newFitness < bestFitness) {
				bestValue = newValue.clone();
				bestFitness = newFitness;
			}
		}

		IntermediateSearchResult iResult = new IntermediateSearchResult(bestValue, bestFitness, list);
		return iResult;
	}

	private IntermediateSearchResult doSearch(double[] bestValue, double bestFitness, int index, List<ExecVar> vars,
			List<TestInputData> list, Branch branch, CDGNode decisionCDGNode, double minimumUnit, double factor) {
		double amount = minimumUnit;

		boolean currentDirection = true;
		double[] value = bestValue.clone();

		double[] localBestValue = value;
		double localBestFitness = bestFitness;

		while (true) {
			double[] newValue = value.clone();
			adjustValue(newValue, index, currentDirection, amount);

			List<double[]> inputData = new ArrayList<>();
			inputData.add(newValue);
			UnitTestSuite newSuite = this.tester.createTest(this.targetMethod, this.settings, this.appClasspath,
					inputData, vars);
			this.testsuite.addTestCases(newSuite);

			TestInputData newInput = null;
			while (newInput == null) {
				try {
					newInput = newSuite.getInputData().values().iterator().next();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			boolean isVisit = false;
			if (!list.contains(newInput)) {
				list.add(newInput);
			} else {
				isVisit = true;
			}

			if (branch.isCovered()) {
				break;
			} else {
				double newFitness = newInput.getFitness(decisionCDGNode, branch);
				if (newFitness < localBestFitness) {
					localBestFitness = newFitness;
					localBestValue = newInput.getInputValue().getAllValues();
					amount *= factor;

					if (localBestFitness < bestFitness) {
						bestValue = localBestValue.clone();
						bestFitness = localBestFitness;
					}

					value = newValue;
					continue;
				} else {
					if (isVisit) {
						break;
					}

					if (amount != minimumUnit) {
						value = newValue;
						localBestFitness = newFitness;
						localBestValue = value.clone();
					}

					currentDirection = !currentDirection;
					amount = minimumUnit;
				}
			}
		}

		IntermediateSearchResult iResult = new IntermediateSearchResult(bestValue, bestFitness, list);
		return iResult;
	}

	private IntermediateSearchResult doDoubleSearch(double[] bestValue, double bestFitness, int index,
			List<ExecVar> vars, List<TestInputData> list, Branch branch, CDGNode decisionCDGNode) {
		double minimumUnit = 0.001;
		double factor = 2;

		return doSearch(bestValue, bestFitness, index, vars, list, branch, decisionCDGNode, minimumUnit, factor);
	}

	private IntermediateSearchResult doIntegerSearch(double[] bestValue, double bestFitness, int index,
			List<ExecVar> vars, List<TestInputData> list, Branch branch, CDGNode decisionCDGNode) {
		double minimumUnit = 1;
		double factor = 2;

		return doSearch(bestValue, bestFitness, index, vars, list, branch, decisionCDGNode, minimumUnit, factor);
	}

	private void adjustValue(double[] newValue, int d, boolean increaseValue, double amount) {
		if (increaseValue) {
			newValue[d] += amount;
		} else {
			newValue[d] -= amount;
		}
	}

	private boolean isCoverBranch(UnitTestSuite newSuite, TestInputData newInput1, Branch branch) {
		CoveragePath path = newSuite.getCoverageGraph().getCoveragePaths().get(0);
		for (CoverageSFNode node : path.getPath()) {
			if (node.getCvgIdx() == branch.getToNodeIdx()) {
				return true;
			}
		}
		return false;
	}

	private TestInputData findClosestInput(List<TestInputData> otherInputs, CDGNode decisionCDGNode, Branch branch) {
		TestInputData returnInput = null;
		double closestValue = -1;
		for (TestInputData input : otherInputs) {
			if (returnInput == null) {
				returnInput = input;
				closestValue = input.getFitness(decisionCDGNode, branch);
			} else {
				Double value = input.getFitness(decisionCDGNode, branch);
				;
				if (closestValue > value) {
					closestValue = value;
					returnInput = input;
				}
			}

		}

		return returnInput;
	}

	private Branch findSiblingBranch(Branch branch) {
		for (Branch b : branchInputMap.keySet()) {
			if (b.getFromNodeIdx() == branch.getFromNodeIdx() && b.getToNodeIdx() != branch.getToNodeIdx()) {
				return b;
			}
		}
		return null;
	}

	private boolean isAllChildrenCovered(CDGNode node) {
		if (!node.getCfgNode().isCovered()) {
			return false;
		}

		for (CDGNode child : node.getChildren()) {
			boolean covered = isAllChildrenCovered(child);
			if (!covered) {
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