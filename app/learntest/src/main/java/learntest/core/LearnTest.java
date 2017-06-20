package learntest.core;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import gentest.junit.TestsPrinter.PrintOption;
import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.BreakpointValue;
import learntest.core.commons.data.classinfo.JunitTestsInfo;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.utils.CoverageUtils;
import learntest.core.commons.utils.JavaFileCopier;
import learntest.core.gentest.GentestParams;
import learntest.core.gentest.GentestResult;
import learntest.core.machinelearning.PrecondDecisionLearner;
import learntest.exception.LearnTestException;
import learntest.main.LearnTestParams;
import learntest.main.RunTimeInfo;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.FileUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.settings.SAVTimer;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;

public class LearnTest extends AbstractLearntest {
	private static Logger log = LoggerFactory.getLogger(LearnTest.class);
	private LearningMediator mediator;
	
	public LearnTest(AppJavaClassPath appClasspath) {
		super(appClasspath);
	}
	
	public RunTimeInfo run(LearnTestParams params) throws Exception {
		prepareInitTestcase(params);
		SAVTimer.startCount();
		/* collect testcases in project */
		if (CollectionUtils.isEmpty(params.getInitialTestcases())) {
			log.info("empty testcase!");
			return null;
		}
		init(params);
		boolean learningStarted = false;
		CfgCoverage cfgCoverage = null;
		TargetMethod targetMethod = params.getTargetMethod();
		try {
			/* collect coverage and build cfg */
			cfgCoverage = tryBestForInitialCoverage(params, targetMethod);

			if (CoverageUtils.notCoverAtAll(cfgCoverage)) {
				log.info("start node is not covered!");
				return null;
			}
			log.info("first coverage: " + CoverageUtils.calculateCoverage(cfgCoverage));
			BreakPoint methodEntryBkp = BreakpointCreator.createMethodEntryBkp(targetMethod);
			/**
			 * run testcases
			 */
			BreakpointData result = executeTestcaseAndGetTestInput(params.getInitialTestcases(), methodEntryBkp);
			if (CoverageUtils.noDecisionNodeIsCovered(cfgCoverage)) {
				log.info("no decision node is covered!");
				copyTestsToResultFolder(params);
				return getRuntimeInfo(cfgCoverage);
			} else {
				/* learn */
				PrecondDecisionLearner learner = mediator.initDecisionLearner(params.isLearnByPrecond());
				DecisionProbes initProbes = initProbes(targetMethod, cfgCoverage, result);
				learningStarted = true;
				DecisionProbes probes = learner.learn(initProbes);
				return getRuntimeInfo(probes);
			}
		} catch (SAVExecutionTimeOutException e) {
			if (learningStarted) {
				// LLT: still trying to figure out what to do with new approach.
				if (cfgCoverage != null) {
					return getRuntimeInfo(cfgCoverage);
				} 
			}
		} catch (LearnTestException e) {
			log.warn("still cannot get entry value when coverage is not empty!");
		}
		return null;
	}

	private void copyTestsToResultFolder(LearnTestParams params) {
		JavaFileCopier.copy(params.getInitialTests().getJunitFiles(), params.getInitTestPkg(),
				params.getResultTestPkg(), appClasspath.getTestSrc());
	}

	protected void prepareInitTestcase(LearnTestParams params) throws SavException {
		if (!params.getInitialTestcases().isEmpty()) {
			return;
		}
		/* init test */
		GentestParams gentestParams = params.initGentestParams(appClasspath);
		gentestParams.setTestPkg(params.getInitTestPkg());
		GentestResult initTests = generateTestcases(gentestParams);
		params.setInitialTests(new JunitTestsInfo(initTests, appClasspath.getClassLoader()));
	}

	/**
	 * run coverage, and in case the coverage is too bad (means no branch is covered)
	 * try to generate another testcase.
	 */
	private CfgCoverage tryBestForInitialCoverage(LearnTestParams params, TargetMethod targetMethod)
			throws SavException, IOException, ClassNotFoundException {
		CfgCoverage cfgCoverage = null;
		int i;
		GentestParams gentestParams = params.initGentestParams(appClasspath);
		gentestParams.setPrintOption(PrintOption.APPEND);
		List<String> junitClasses = params.getInitialTests().getJunitClasses();

		double bestCvg = 0.0;
		GentestResult gentestResult = null;
		for (i = 0; ; i++) {
			cfgCoverage = runCfgCoverage(targetMethod, junitClasses);
			double cvg = CoverageUtils.calculateCoverage(cfgCoverage);
			/* replace current init test with new generated test */
			if (cvg > bestCvg) {
				bestCvg = cvg;
				if(gentestResult != null) {
					params.setInitialTests(new JunitTestsInfo(gentestResult, appClasspath.getClassLoader()));
				}
			} else if (gentestResult != null) {
				// remove files
				FileUtils.deleteFiles(gentestResult.getJunitfiles());
			}
			if (i >= 3 || !CoverageUtils.noDecisionNodeIsCovered(cfgCoverage)) {
				break;
			}
			gentestResult = randomGentest(gentestParams);
		}
		if (i > 0) {
			log.debug(String.format("Get best initial coverage after trying to regenerate test %d times", i));
		}
		return cfgCoverage;
	}

	private GentestResult randomGentest(GentestParams gentestParams)
			throws ClassNotFoundException, SavException, IOException {
		GentestResult result = mediator.getTestGenerator().genTest(gentestParams);
		/* update coverage */
		mediator.compile(result.getJunitfiles());
		return result;
	}
	
	/**
	 * init fields.
	 */
	private void init(LearnTestParams params) {
		mediator = new LearningMediator(appClasspath, params.getTargetMethod());
	}

	private DecisionProbes initProbes(TargetMethod targetMethod, CfgCoverage cfgcoverage, BreakpointData result)
			throws LearnTestException {
		DecisionProbes probes = new DecisionProbes(targetMethod, cfgcoverage);
		List<BreakpointValue> entryValues = result.getAllValues();
		if (CollectionUtils.isEmpty(entryValues)) {
			throw new LearnTestException("cannot get entry value when coverage is still not empty");
		}
		probes.setRunningResult(entryValues);
//		XmlCoverageWriter writer = new XmlCoverageWriter();
//		ProbesXmlConverter converter = new ProbesXmlConverter();
//		writer.writeXml(converter.toMethodsElement(probes), LearntestConstant.XML_FILE_PATH);
		return probes;
	}

}
