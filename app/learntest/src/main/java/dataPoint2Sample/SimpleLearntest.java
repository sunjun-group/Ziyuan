package dataPoint2Sample;

import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.BreakpointValue;
import learntest.core.AbstractLearntest;
import learntest.core.BreakpointCreator;
import learntest.core.LearnTestParams;
import learntest.core.LearningMediator;
import learntest.core.LearntestParamsUtils;
import learntest.core.RunTimeInfo;
import learntest.core.LearntestParamsUtils.GenTestPackage;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.exception.LearnTestException;
import learntest.core.commons.utils.CoverageUtils;
import learntest.core.gentest.GentestParams;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.settings.SAVTimer;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;

/**
 * @author ZhangHr
 */
public class SimpleLearntest extends AbstractLearntest {
	private static Logger log = LoggerFactory.getLogger(SimpleLearntest.class);
	LearningMediator mediator;
	public DecisionProbes initProbes;

	public SimpleLearntest(AppJavaClassPath appClasspath) {
		super(appClasspath);
	}

	@Override
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
		CfgCoverage cfgCoverage = null;
		TargetMethod targetMethod = params.getTargetMethod();
		/* collect coverage and build cfg */
		cfgCoverage = runCfgCoverage(targetMethod, params.getInitialTests().getJunitClasses());

		if (CoverageUtils.notCoverAtAll(cfgCoverage)) {
			log.info("start node is not covered!");
			return getRuntimeInfo(cfgCoverage, params.isTestMode());
		}
		log.info("first coverage: " + CoverageUtils.calculateCoverageByBranch(cfgCoverage));
		BreakPoint methodEntryBkp = BreakpointCreator.createMethodEntryBkp(targetMethod);
		/**
		 * run testcases
		 */
		BreakpointData result = executeTestcaseAndGetTestInput(params.getInitialTestcases(), methodEntryBkp);
		if (CoverageUtils.noDecisionNodeIsCovered(cfgCoverage)) {
			log.info("no decision node is covered!");
		} else {
			initProbes = initProbes(targetMethod, cfgCoverage, result);
		}
		return null;
	}

	protected void prepareInitTestcase(LearnTestParams params)
			throws SavException, ClassNotFoundException, IOException {
		if (!params.getInitialTestcases().isEmpty()) {
			return;
		}
		GentestParams gentestParams = LearntestParamsUtils.createGentestParams(appClasspath, params,
				GenTestPackage.INIT);
		randomGenerateInitTestWithBestEffort(params, gentestParams);
	}

	private DecisionProbes initProbes(TargetMethod targetMethod, CfgCoverage cfgcoverage, BreakpointData result)
			throws LearnTestException {
		DecisionProbes probes = new DecisionProbes(targetMethod, cfgcoverage);
		List<BreakpointValue> entryValues = result.getAllValues();
		if (CollectionUtils.isEmpty(entryValues)) {
			throw new LearnTestException("cannot get entry value when coverage is still not empty");
		}
		probes.setRunningResult(entryValues);
		return probes;
	}

	protected void init(LearnTestParams params) {
		mediator = new LearningMediator(appClasspath, params);
	}
}
