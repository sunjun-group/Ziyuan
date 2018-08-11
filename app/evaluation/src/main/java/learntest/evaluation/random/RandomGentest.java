package learntest.evaluation.random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.activelearning.core.coverage.CoverageUtils;
import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.model.UnitTestSuite;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.core.commons.data.classinfo.MethodInfo;
import learntest.evaluation.core.CoverageProgressRecorder;
import microbat.instrumentation.cfgcoverage.CoverageAgentParams.CoverageCollectionType;
import microbat.instrumentation.cfgcoverage.InstrumentationUtils;
import microbat.instrumentation.cfgcoverage.graph.CFGInstance;
import microbat.instrumentation.cfgcoverage.graph.CFGUtility;
import microbat.instrumentation.cfgcoverage.graph.CoverageGraphConstructor;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFlowGraph;
import sav.common.core.utils.SingleTimer;
import sav.common.core.utils.TextFormatUtils;
import sav.strategies.dto.AppJavaClassPath;

public class RandomGentest {
	private Logger log = LoggerFactory.getLogger(RandomGentest.class);
	private String outputFolder;
	
	public RandomGentest(String randomOutputFolder) {
		this.outputFolder = randomOutputFolder;
	}

	public void generateTestcase(AppJavaClassPath appClasspath, MethodInfo targetMethod, LearntestSettings settings)
			throws Exception {
		log.info("Run method: " + targetMethod.toString());
		settings.setInitRandomTestNumber(1);
		settings.setCfgExtensionLayer(1);
		settings.setRunCoverageAsMethodInvoke(true);
		settings.setMethodExecTimeout(200l);
		settings.setCoverageRunSocket(true);
		CFGUtility cfgUtility = new CFGUtility();
		CFGInstance cfgInstance = cfgUtility.buildProgramFlowGraph(appClasspath,
				InstrumentationUtils.getClassLocation(targetMethod.getClassName(), targetMethod.getMethodSignature()),
				settings.getCfgExtensionLayer());
		cfgUtility.breakCircle(cfgInstance);
		CoverageGraphConstructor constructor = new CoverageGraphConstructor();
		CoverageSFlowGraph coverageSFlowGraph = constructor.buildCoverageGraph(cfgInstance);
		
		Tester tester = new Tester(settings, false, appClasspath);
		tester.setCvgType(CoverageCollectionType.BRANCH_COVERAGE);
		UnitTestSuite finalTestsuit = null;
		long startTime = 0;
		long endTime = 0;
		int interval = 2000;
		int numInterval = 9;
		CoverageProgressRecorder progressRecorder = new CoverageProgressRecorder(targetMethod, outputFolder + "/coverage_progress.xlsx");
		
		log.debug(TextFormatUtils.printCol(CoverageUtils.getBranchCoverageDisplayTexts(coverageSFlowGraph, cfgInstance), "\n"));
		progressRecorder.setCoverageGraph(coverageSFlowGraph);
		SingleTimer timer = SingleTimer.start("run Random Test");
		try {
			for (int i = 0; i < numInterval; i++) {
				startTime = System.currentTimeMillis();
				CoverageSFlowGraph newCoverageGraph;
				do {
					try {
						long startTest = System.currentTimeMillis();
						UnitTestSuite testsuite = tester.createRandomTest(targetMethod, settings, appClasspath);
						endTime = System.currentTimeMillis();
						newCoverageGraph = testsuite.getCoverageGraph();
						log.debug(TextFormatUtils.printCol(CoverageUtils.getBranchCoverageDisplayTexts(newCoverageGraph, cfgInstance), "\n"));
						progressRecorder.updateNewCoverage(newCoverageGraph, 1);
						log.debug(String.format("Execution Time-testcase %s: %s", testsuite.getJunitTestcases().toString(), TextFormatUtils.printTimeString(endTime - startTest)));
						if (finalTestsuit == null) {
							finalTestsuit = testsuite;
						} else {
							finalTestsuit.addTestCases(testsuite);
						}
					} catch(Exception e) {
						// ignore
						endTime = System.currentTimeMillis();
					}
				} while (endTime - startTime <= interval);
				progressRecorder.updateProgress();
			}
			System.out.println(timer.getResult());
			progressRecorder.store();
		} finally {
			tester.dispose();
		}
	}

}
