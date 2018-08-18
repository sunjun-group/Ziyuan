package learntest.activelearning.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfg.CfgNode;
import learntest.activelearning.core.coverage.CoverageUtils;
import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.model.UnitTestSuite;
import learntest.activelearning.core.python.NeuralNetworkLearner;
import learntest.activelearning.core.python.PythonCommunicator;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.core.commons.data.classinfo.MethodInfo;
import microbat.instrumentation.cfgcoverage.InstrumentationUtils;
import microbat.instrumentation.cfgcoverage.graph.CFGInstance;
import microbat.instrumentation.cfgcoverage.graph.CFGUtility;
import microbat.instrumentation.cfgcoverage.graph.CoverageGraphConstructor;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFlowGraph;
import microbat.instrumentation.cfgcoverage.graph.cdg.CDG;
import microbat.instrumentation.cfgcoverage.graph.cdg.CDGConstructor;
import sav.common.core.SavRtException;
import sav.common.core.utils.Randomness;
import sav.common.core.utils.TextFormatUtils;
import sav.strategies.dto.AppJavaClassPath;

public class NeuralActiveLearnTest {
	private static Logger log = LoggerFactory.getLogger(NeuralActiveLearnTest.class);

	public void generateTestcase(AppJavaClassPath appClasspath, MethodInfo targetMethod, LearntestSettings settings) throws Exception {
		settings.setInitRandomTestNumber(10);
		//settings.setMethodExecTimeout(100);
		CFGUtility cfgUtility = new CFGUtility();
		
		Repository.clearCache();
		CFGInstance cfgInstance = cfgUtility.buildProgramFlowGraph(appClasspath,
				InstrumentationUtils.getClassLocation(targetMethod.getClassName(), targetMethod.getMethodSignature()),
				settings.getCfgExtensionLayer());
		cfgUtility.breakCircle(cfgInstance);
		CoverageGraphConstructor constructor = new CoverageGraphConstructor();
		CoverageSFlowGraph coverageSFlowGraph = constructor.buildCoverageGraph(cfgInstance);
		CDGConstructor cdgConstructor = new CDGConstructor();
		CDG cdg = cdgConstructor.construct(coverageSFlowGraph);
		
		/* generate random test */
		Tester tester = new Tester(settings, true, appClasspath);
		UnitTestSuite testsuite = tester.createInitRandomTest(targetMethod, settings, appClasspath, 3, cfgInstance);
		if (testsuite == null) {
			throw new SavRtException("Fail to generate random test!");
		}
		
		/* learn */
		PythonCommunicator communicator = new PythonCommunicator();
		communicator.start();
		
		NeuralNetworkLearner nnLearner = new NeuralNetworkLearner(tester, testsuite, communicator, appClasspath, targetMethod, settings);
		nnLearner.learningToCover(cdg);
		communicator.stop();
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
