package learntest.activelearning.core.testgeneration.localsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.utils.BreakpointDataUtils;
import learntest.activelearning.core.data.DpAttribute;
import learntest.activelearning.core.data.MethodInfo;
import learntest.activelearning.core.data.TestInputData;
import learntest.activelearning.core.data.UnitTestSuite;
import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.activelearning.core.testgeneration.mutate.Mutator;
import learntest.activelearning.core.testgeneration.mutate.NumericMutator;
import microbat.instrumentation.cfgcoverage.graph.Branch;
import microbat.instrumentation.cfgcoverage.graph.cdg.CDGNode;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.execute.value.ExecVar;

public class GradientBasedSearch {
	
	private Map<Branch, List<TestInputData>> branchInputMap = new HashMap<>();
	private UnitTestSuite testsuite;
	private Tester tester;
	private AppJavaClassPath appClasspath;
	private MethodInfo targetMethod;
	private LearntestSettings settings;
	
	public GradientBasedSearch(Map<Branch, List<TestInputData>> branchInputMap, UnitTestSuite testsuite, Tester tester,
			AppJavaClassPath appClasspath, MethodInfo targetMethod, LearntestSettings settings) {
		super();
		this.branchInputMap = branchInputMap;
		this.testsuite = testsuite;
		this.tester = tester;
		this.appClasspath = appClasspath;
		this.targetMethod = targetMethod;
		this.settings = settings;
	}

	/**
	 * the branch <code>branch</code> is a branch of
	 * <code>decisionCDGNode</code> node
	 * 
	 * @param branch
	 * @param branchCDGNode
	 * @return
	 */
	public List<TestInputData> generateInputByGradientSearch(Branch branch, CDGNode branchCDGNode) {
		
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
		newSuite.setLearnDataMapper(testsuite.getLearnDataMapper());
		
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

	private IntermediateSearchResult doSearch(Mutator mutator, double[] bestValue, double bestFitness, int index, List<ExecVar> vars,
			List<TestInputData> list, Branch branch, CDGNode decisionCDGNode, double minimumUnit, double factor) {
		double amount = minimumUnit;

		boolean currentDirection = true;
		double[] value = bestValue.clone();

		double[] localBestValue = value;
		double localBestFitness = bestFitness;

		while (true) {
			double[] newValue = value.clone();
			mutator.mutateValue(newValue, index, currentDirection, amount);

			List<double[]> inputData = new ArrayList<>();
			inputData.add(newValue);
			UnitTestSuite newSuite = this.tester.createTest(this.targetMethod, this.settings, this.appClasspath,
					inputData, vars);
			newSuite.setLearnDataMapper(testsuite.getLearnDataMapper());
			for (TestInputData id : newSuite.getInputData().values()) {
				DpAttribute[] dataPoint = id.getDataPoint();
				System.out.println(dataPoint);
			}
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

		return doSearch(new NumericMutator(), bestValue, bestFitness, index, vars, list, branch, decisionCDGNode, minimumUnit, factor);
	}

	private IntermediateSearchResult doIntegerSearch(double[] bestValue, double bestFitness, int index,
			List<ExecVar> vars, List<TestInputData> list, Branch branch, CDGNode decisionCDGNode) {
		double minimumUnit = 1;
		double factor = 2;

		return doSearch(new NumericMutator(), bestValue, bestFitness, index, vars, list, branch, decisionCDGNode, minimumUnit, factor);
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
}
