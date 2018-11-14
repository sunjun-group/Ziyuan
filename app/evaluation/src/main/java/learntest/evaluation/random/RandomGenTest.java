package learntest.evaluation.random;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gentest.main.GentestService;
import learntest.activelearning.core.IProgressMonitor;
import learntest.activelearning.core.coverage.CoverageUtils;
import learntest.activelearning.core.data.DpAttribute;
import learntest.activelearning.core.data.LearnDataSetMapper;
import learntest.activelearning.core.data.LearnTestContext;
import learntest.activelearning.core.data.LearningVarCollector;
import learntest.activelearning.core.data.MethodInfo;
import learntest.activelearning.core.data.TestInputData;
import learntest.activelearning.core.data.UnitTestSuite;
import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.settings.LearntestSettings;
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
import sav.strategies.dto.execute.value.ExecVar;

public class RandomGenTest {
	private Logger log = LoggerFactory.getLogger(RandomGenTest.class);
	private String outputFolder;
	
	public RandomGenTest(String randomOutputFolder) {
		this.outputFolder = randomOutputFolder;
	}

	public void generateTestcase(AppJavaClassPath appClasspath, MethodInfo targetMethod, LearntestSettings settings,
			IProgressMonitor progressMonitor) throws Exception {
		LearnTestContext.init();
		settings.setInitRandomTestNumber(1);
		settings.setCfgExtensionLayer(1);
		settings.setRunCoverageAsMethodInvoke(true);
		settings.setMethodExecTimeout(500l);
		settings.setCoverageRunSocket(true);
		CFGUtility cfgUtility = new CFGUtility();
		CFGInstance cfgInstance = cfgUtility.buildProgramFlowGraph(appClasspath,
				InstrumentationUtils.getClassLocation(targetMethod.getClassName(), targetMethod.getMethodSignature()),
				settings.getCfgExtensionLayer());
		CoverageGraphConstructor constructor = new CoverageGraphConstructor();
		CoverageSFlowGraph coverageSFlowGraph = constructor.buildCoverageGraph(cfgInstance);
		
		Tester tester = new Tester(settings, false, appClasspath);
//		tester.setCvgType(CoverageCollectionType.BRANCH_COVERAGE);
		UnitTestSuite finalTestsuit = null;
		long startTime = 0;
		long endTime = 0;
		int interval = 5000;
		int numInterval = 3;
		CoverageProgressRecorder progressRecorder = new CoverageProgressRecorder(targetMethod, outputFolder + "/coverage_progress.xlsx", outputFolder + "/coverage_casenumber.xlsx");
		
		log.debug(TextFormatUtils.printCol(CoverageUtils.getBranchCoverageDisplayTexts(coverageSFlowGraph, cfgInstance), "\n"));
		progressRecorder.setCoverageGraph(coverageSFlowGraph);
		SingleTimer gentestTimer = SingleTimer.start("Gentest-Cleanup Thread");
		SingleTimer coverageTimer = SingleTimer.start("Coverage-timer");
		try {
			for (int i = 0; i < numInterval; i++) {
				startTime = System.currentTimeMillis();
				CoverageSFlowGraph newCoverageGraph;
				if (progressMonitor.isCanceled()) {
					break;
				}
				do {
					try {
						log.info(String.format("Run method: %s, round %s", targetMethod.toString(), i));
						long startTest = System.currentTimeMillis();
						UnitTestSuite testsuite = tester.createRandomTest(targetMethod, settings, appClasspath);
						List<ExecVar> learningVarsSet = new LearningVarCollector(settings.getInputValueExtractLevel(), settings.getLearnArraySizeThreshold(),
																	settings.getReceiverFieldRetrieveLevel())
											.collectLearningVars(appClasspath, targetMethod, testsuite.getInputData().values());
						LearnTestContext.setDatasetMapper(learningVarsSet, settings.getLearnArraySizeThreshold());
						testsuite.setLearnDataMapper(LearnTestContext.getLearnDataSetMapper());
						for (TestInputData inputData : testsuite.getInputData().values()) {
							DpAttribute[] dataPoint = inputData.getDataPoint();
							log.debug(TextFormatUtils.printObj(dataPoint));
						}
						
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
						e.printStackTrace();
						log.debug(e.getMessage());
						// ignore
						endTime = System.currentTimeMillis();
						tester.reset();
					}
					if (gentestTimer.getExecutionTime() > 2000) {
						GentestService.cleanupThread();
						gentestTimer.restart();
					}
					if (coverageTimer.getExecutionTime() > 60000) {
						tester.reset();
						coverageTimer.restart();
					}
					if (progressMonitor.isCanceled()) {
						break;
					}
				} while (endTime - startTime <= interval);
				progressRecorder.updateProgress();
			}
			log.debug(gentestTimer.getResult());
			progressRecorder.store();
		} finally {
			tester.reset();
			GentestService.reset();
		}
		log.debug("Finish RandomGenTest");
		LearnTestContext.dispose();
	}

}
