package learntest.evaluation.random;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.activelearning.core.coverage.CoverageUtils;
import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.model.UnitTestSuite;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.core.commons.data.classinfo.MethodInfo;
import learntest.evaluation.core.CoverageProgressRecorder;
import microbat.instrumentation.cfgcoverage.InstrumentationUtils;
import microbat.instrumentation.cfgcoverage.graph.Branch;
import microbat.instrumentation.cfgcoverage.graph.CFGInstance;
import microbat.instrumentation.cfgcoverage.graph.CFGUtility;
import microbat.instrumentation.cfgcoverage.graph.CoverageGraphConstructor;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFlowGraph;
import sav.common.core.utils.TextFormatUtils;
import sav.strategies.dto.AppJavaClassPath;

public class RandomGentest {
	private Logger log = LoggerFactory.getLogger(RandomGentest.class);

	public void generateTestcase(AppJavaClassPath appClasspath, MethodInfo targetMethod, LearntestSettings settings)
			throws Exception {
		log.info("Run method: " + targetMethod.toString());
		settings.setInitRandomTestNumber(1);
		
		CFGUtility cfgUtility = new CFGUtility();
		CFGInstance cfgInstance = cfgUtility.buildProgramFlowGraph(appClasspath,
				InstrumentationUtils.getClassLocation(targetMethod.getClassName(), targetMethod.getMethodSignature()),
				settings.getCfgExtensionLayer());
		cfgUtility.breakCircle(cfgInstance);
		CoverageGraphConstructor constructor = new CoverageGraphConstructor();
		CoverageSFlowGraph coverageSFlowGraph = constructor.buildCoverageGraph(cfgInstance);
		
		Tester tester = new Tester(settings, false);
		UnitTestSuite testsuite = null;
		long startTime = 0;
		long endTime = 0;
		int interval = 10000;
		int numInterval = 9;
		double[] progress = new double[numInterval];
		log.debug(TextFormatUtils.printCol(CoverageUtils.getBranchCoverageDisplayTexts(coverageSFlowGraph, cfgInstance), "\n"));
		
		Set<Branch> allBranches = CoverageUtils.getAllBranches(coverageSFlowGraph);
		Set<Branch> coveredBranches = CoverageUtils.getCoveredBranches(coverageSFlowGraph, targetMethod.getMethodId());
		for (int i = 0; i < numInterval; i++) {
			startTime = System.currentTimeMillis();
			CoverageSFlowGraph newCoverageGraph;
			do {
				testsuite = tester.createRandomTest(targetMethod, settings, appClasspath);
				endTime = System.currentTimeMillis();
				newCoverageGraph = testsuite.getCoverageGraph();
				coveredBranches.addAll(CoverageUtils.getCoveredBranches(newCoverageGraph, targetMethod.getMethodId()));
			} while (endTime - startTime >= interval);
			progress[i] = coveredBranches.size() / (double) allBranches.size();
			System.out.println(progress[i]);
		}
		CoverageProgressRecorder recoreder = new CoverageProgressRecorder();
		recoreder.record(targetMethod, progress);
	}

}
