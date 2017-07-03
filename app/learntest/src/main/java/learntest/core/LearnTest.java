package learntest.core;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.BreakpointValue;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.utils.CoverageUtils;
import learntest.core.commons.utils.JavaFileCopier;
import learntest.core.gentest.GentestParams;
import learntest.core.machinelearning.IInputLearner;
import learntest.exception.LearnTestException;
import learntest.main.LearnTestParams;
import learntest.main.RunTimeInfo;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
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
		log.debug("Start learntest..({})", params.getApproach());
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
			cfgCoverage = runCfgCoverage(targetMethod, params.getInitialTests().getJunitClasses());

			if (CoverageUtils.notCoverAtAll(cfgCoverage)) {
				log.info("start node is not covered!");
				return getRuntimeInfo(cfgCoverage);
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
				IInputLearner learner = mediator.initDecisionLearner(params);
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

	protected void prepareInitTestcase(LearnTestParams params) throws SavException, ClassNotFoundException, IOException {
		if (!params.getInitialTestcases().isEmpty()) {
			logInitTests(params.getInitialTests().getJunitClasses());
			return;
		}
		/* init test */
		GentestParams gentestParams = params.initGentestParams(appClasspath);
		gentestParams.setTestPkg(params.getInitTestPkg());
		randomGenerateInitTestWithBestEffort(params, gentestParams);
		logInitTests(params.getInitialTests().getJunitClasses());
	}

	private void logInitTests(List<String> junitClasses) {
		log.info("Initial junit classes: {}", junitClasses);
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
