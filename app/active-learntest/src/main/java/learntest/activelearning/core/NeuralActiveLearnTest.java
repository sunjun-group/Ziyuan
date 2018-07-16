package learntest.activelearning.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import cfg.CFG;
import cfg.CfgNode;
import cfg.utils.CfgConstructor;
import cfgextractor.CFGBuilder;
import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.model.UnitTestSuite;
import learntest.activelearning.core.python.NeuralNetworkLearner;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.core.BreakpointCreator;
import learntest.core.commons.data.classinfo.MethodInfo;
import sav.common.core.SavRtException;
import sav.common.core.utils.Randomness;
import sav.strategies.dto.AppJavaClassPath;
import variable.Variable;

/**
 * @author LLT
 *
 */
public class NeuralActiveLearnTest {
	
	public void generateTestcase(AppJavaClassPath appClasspath, MethodInfo targetMethod, LearntestSettings settings) throws Exception {
		CfgConstructor cfgConstructor = new CfgConstructor();
		CFG cfg = cfgConstructor.constructCFG(appClasspath, targetMethod.getClassName(),
				targetMethod.getMethodSignature(), settings.getCdgLayer());
		/* <offset, relevant variables> */
		Map<Integer, List<Variable>> relevantVarMap = new CFGBuilder().parsingCFG(appClasspath,
				targetMethod.getClassName(), targetMethod.getMethodFullName(), targetMethod.getLineNum(), targetMethod.getMethodSignature())
				.getRelevantVarMap();
		cfg.getEntryPoint().setVars(BreakpointCreator.toBkpVariables(relevantVarMap));
		
		/* generate random test */
		Tester tester = new Tester(settings);
		int mx = 3;
		UnitTestSuite testsuite = null;
		for (int i = 0; i < mx; i++) {
			UnitTestSuite initTest = tester.createRandomTest(targetMethod, settings, cfg, appClasspath);
			if (initTest.getCoverage().getBranchCoverage() > 0) {
				testsuite = initTest;
				break;
			}
		}
		
		if (testsuite == null) {
			throw new SavRtException("Fail to generate random test!");
		}
		
		NeuralNetworkLearner nnLearner = new NeuralNetworkLearner();
		/* learn */
		Queue<CfgNode> queue = new LinkedList<>();
		queue.add(cfg.getFirstDecisionNode());
		Set<Integer> visited = new HashSet<>();
		
		while (!queue.isEmpty()) {
			CfgNode node = queue.poll();
			if (visited.contains(node.getIdx())) {
				continue;
			}
			for (CfgNode branch : node.getBranches()) {
				while (branch != null && !branch.isDecisionNode()) {
					branch = branch.getNext();
				}
				if (branch != null && branch.isDecisionNode()) {
					queue.add(branch);
				}
			}
			/* */
			for (CfgNode branch : node.getBranches()) {
				List<double[]> coveredInput = testsuite.getCoveredInputData(branch);
				List<double[]> uncoveredInput = testsuite.getUnCoveredInputData(branch);
				int i = 0;
				while ((coveredInput.isEmpty() || uncoveredInput.isEmpty()) && i++ < settings.getNnLearningThreshold()) {
//					List<double[]> generatedInput = nnLearner.boundaryRemaining(coveredInput, uncoveredInput, branch);
					List<double[]> generatedInput = fake_boundary_remaining(coveredInput, uncoveredInput, branch);
					UnitTestSuite newTestCases = tester.createTest(targetMethod, settings, cfg, appClasspath,
							generatedInput, testsuite.getInputVars());
					testsuite.addTestCases(newTestCases);
					coveredInput = testsuite.getCoveredInputData(branch);
					uncoveredInput = testsuite.getUnCoveredInputData(branch);
				}
				if (!coveredInput.isEmpty() && !uncoveredInput.isEmpty()) {
					// active_learning.
				}
			}
			visited.add(node.getIdx());
		}
	}

	private List<double[]> fake_boundary_remaining(List<double[]> coveredInput, List<double[]> uncoveredInput, CfgNode branch) {
		List<double[]> newInputs = new ArrayList<double[]>();
		int max = 2;
		for (double[] input : coveredInput) {
			double[] newInput = new double[input.length];
			for (int i = 0; i < input.length; i++) {
				newInput[i] = input[i] + Randomness.nextInt();
			}
			newInputs.add(newInput);
			if (newInputs.size() == max) {
				break;
			}
		}
		for (double[] input : uncoveredInput) {
			double[] newInput = new double[input.length];
			for (int i = 0; i < input.length; i++) {
				newInput[i] = input[i] + Randomness.nextInt(-100, 100);
			}
			newInputs.add(newInput);
			if (newInputs.size() == max) {
				break;
			}
		}
		return newInputs;
	}
}
