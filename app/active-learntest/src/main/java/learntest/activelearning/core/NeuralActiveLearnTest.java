package learntest.activelearning.core;

import java.util.ArrayList;
import java.util.List;

import cfg.CfgNode;
import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.model.UnitTestSuite;
import learntest.activelearning.core.python.NeuralNetworkLearner;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.core.commons.data.classinfo.MethodInfo;
import microbat.instrumentation.cfgcoverage.InstrumentationUtils;
import microbat.instrumentation.cfgcoverage.graph.CFGInstance;
import microbat.instrumentation.cfgcoverage.graph.CFGUtility;
import microbat.instrumentation.cfgcoverage.graph.CoverageGraphConstructor;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFlowGraph;
import sav.common.core.SavRtException;
import sav.common.core.utils.Randomness;
import sav.strategies.dto.AppJavaClassPath;

public class NeuralActiveLearnTest {

	public void generateTestcase(AppJavaClassPath appClasspath, MethodInfo targetMethod, LearntestSettings settings) throws Exception {
		settings.setInitRandomTestNumber(10);
		//settings.setMethodExecTimeout(100);
		CFGUtility cfgUtility = new CFGUtility();
		CFGInstance cfgInstance = cfgUtility.buildProgramFlowGraph(appClasspath,
				InstrumentationUtils.getClassLocation(targetMethod.getClassName(), targetMethod.getMethodSignature()),
				settings.getCfgExtensionLayer());
		CoverageGraphConstructor constructor = new CoverageGraphConstructor();
		CoverageSFlowGraph coverageSFlowGraph = constructor.buildCoverageGraph(cfgInstance);
		
		/* generate random test */
		Tester tester = new Tester(settings);
		int mx = 1;
		UnitTestSuite testsuite = null;
		for (int i = 0; i < mx; i++) {
			try {
				UnitTestSuite initTest = tester.createRandomTest(targetMethod, settings, appClasspath);
//			if (initTest.getCoverage().getBranchCoverage() > 0) {
//				testsuite = initTest;
//				break;
//			}
				testsuite = initTest;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (testsuite == null) {
			throw new SavRtException("Fail to generate random test!");
		}
		
		NeuralNetworkLearner nnLearner = new NeuralNetworkLearner();
		/* learn */
		
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
