package learntest.activelearning.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.activelearning.core.data.DpAttribute;
import learntest.activelearning.core.data.LearnTestContext;
import learntest.activelearning.core.data.LearningVarCollector;
import learntest.activelearning.core.data.MethodInfo;
import learntest.activelearning.core.data.TestInputData;
import learntest.activelearning.core.data.UnitTestSuite;
import learntest.activelearning.core.distribution.DistributionExcelWriter;
import learntest.activelearning.core.distribution.DistributionRow;
import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.progress.ProgressExcelWriter;
import learntest.activelearning.core.progress.ProgressRow;
import learntest.activelearning.core.report.CoverageTimer;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.activelearning.core.testgeneration.SearchBasedTestGenerator;
import microbat.instrumentation.cfgcoverage.InstrumentationUtils;
import microbat.instrumentation.cfgcoverage.graph.Branch;
import microbat.instrumentation.cfgcoverage.graph.CFGInstance;
import microbat.instrumentation.cfgcoverage.graph.CFGUtility;
import microbat.instrumentation.cfgcoverage.graph.cdg.CDG;
import microbat.instrumentation.cfgcoverage.graph.cdg.CDGConstructor;
import sav.common.core.SavRtException;
import sav.common.core.utils.TextFormatUtils;
import sav.settings.SAVTimer;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.execute.value.ExecVar;

public class SearchBasedLearnTest {
	private static Logger log = LoggerFactory.getLogger(NeuralActiveLearnTest.class);

	public void generateTestcase(AppJavaClassPath appClasspath, MethodInfo targetMethod, LearntestSettings settings)
			throws Exception {
		SAVTimer.startTime = System.currentTimeMillis();

		LearnTestContext.init();
		settings.setInitRandomTestNumber(10);
		// settings.setMethodExecTimeout(100);
		CFGUtility cfgUtility = new CFGUtility();
		Repository.clearCache();
		CFGInstance cfgInstance = cfgUtility.buildProgramFlowGraph(appClasspath,
				InstrumentationUtils.getClassLocation(targetMethod.getClassName(), targetMethod.getMethodSignature()),
				settings.getCfgExtensionLayer());
		cfgUtility.breakCircle(cfgInstance);
		/* generate random test */
		Tester tester = new Tester(settings, true, appClasspath);
		UnitTestSuite testsuite = tester.createInitRandomTest(targetMethod, settings, appClasspath, 3, cfgInstance);
		if (testsuite == null) {
			throw new SavRtException("Fail to generate random test!");
		}

		String exceptionMessage = "";
		
		CoverageTimer timer = new CoverageTimer(testsuite.getBranchInputMap(), settings.getTestTotalTimeout() + 10000, 10000);
		Thread t = new Thread(timer);
		
		List<Double> progressCoverages = new ArrayList<>();
		List<Integer> tcsNum = new ArrayList<>();
		
		try {
			List<ExecVar> learningVarsSet = new LearningVarCollector(settings.getInputValueExtractLevel(),
					settings.getLearnArraySizeThreshold(), settings.getReceiverFieldRetrieveLevel())
							.collectLearningVars(appClasspath, targetMethod, testsuite.getInputData().values());
			LearnTestContext.setDatasetMapper(learningVarsSet, settings.getLearnArraySizeThreshold());
			testsuite.setLearnDataMapper(LearnTestContext.getLearnDataSetMapper());
			for (TestInputData inputData : testsuite.getInputData().values()) {
				DpAttribute[] dataPoint = inputData.getDataPoint();
				log.debug(TextFormatUtils.printObj(dataPoint));
			}
			
			testsuite.getCoverageGraph().setCfg(cfgInstance);
			CDGConstructor cdgConstructor = new CDGConstructor();
			CDG cdg = cdgConstructor.construct(testsuite.getCoverageGraph());
			
			t.start();
			
			SearchBasedTestGenerator generator = new SearchBasedTestGenerator(tester, testsuite, appClasspath, targetMethod,
					settings, cdg);
			generator.cover(cdg);
			
			progressCoverages = timer.getProgressCoverages();
			tcsNum = timer.getTcsNum();
			
			double coverage = generator.computeTestCoverage();
			progressCoverages.add(coverage);
			tcsNum.add(generator.computeTestNumber());
			
			List<Branch> uncovered = generator.getUncoveredBranches();
			System.out.println(uncovered);
			System.out.println(coverage);
		} catch (Exception e) {
			e.printStackTrace();
			exceptionMessage = e.getMessage();
		}

		
		
		
		System.out.println("Total tcs: " + tcsNum);
		ProgressExcelWriter coverageWriter = new ProgressExcelWriter(new File("E:/linyun/coverage_report.xlsx"));
		ProgressRow trial = new ProgressRow();
		trial.setMethodName(targetMethod.getMethodFullName() + '.' + targetMethod.getLineNum());
		trial.setErrorMessage(exceptionMessage);
		double[] progress = new double[progressCoverages.size()];
		int i = 0;
		for (Double cvg : progressCoverages) {
			progress[i++] = cvg;
		}
		trial.setProgress(progress);
		coverageWriter.addRowData(trial);
		
		ProgressExcelWriter testNumWriter = new ProgressExcelWriter(new File("E:/linyun/test_number_report.xlsx"));
		trial.setMethodName(targetMethod.getMethodFullName() + '.' + targetMethod.getLineNum());
		double[] tcsnum = new double[tcsNum.size()];
		i = 0;
		for (Integer num : tcsNum) {
			tcsnum[i++] = (double)(num.intValue()); 
		}
		trial.setProgress(tcsnum);
		testNumWriter.addRowData(trial);

		LearnTestContext.dispose();

	}
}
