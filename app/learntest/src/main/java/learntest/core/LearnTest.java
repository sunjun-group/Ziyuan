package learntest.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.BreakpointValue;
import learntest.core.LearntestParamsUtils.GenTestPackage;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.exception.LearnTestException;
import learntest.core.commons.utils.CoverageUtils;
import learntest.core.commons.utils.JavaFileCopier;
import learntest.core.gentest.GentestParams;
import learntest.core.machinelearning.FormulaInfo;
import learntest.core.machinelearning.IInputLearner;
import learntest.core.machinelearning.PrecondDecisionLearner;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.settings.SAVTimer;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;

public class LearnTest extends AbstractLearntest {
	private static Logger log = LoggerFactory.getLogger(LearnTest.class);
	protected LearningMediator mediator;
	
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
			log.info("first coverage: " + CoverageUtils.calculateCoverageByBranch(cfgCoverage));
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
				DecisionProbes probes = learner.learn(initProbes, result);
				RunTimeInfo info = getRuntimeInfo(probes);
				if (learner instanceof PrecondDecisionLearner) { 
					setLearnState((PrecondDecisionLearner)learner, info);
				}
				info.setSample(learner);
				return info;
			}
		} catch (SAVExecutionTimeOutException e) {
			if (learningStarted) {
				// LLT: still trying to figure out what to do with new approach.
			}
		} catch (LearnTestException e) {
			log.warn("still cannot get entry value when coverage is not empty!");
		}
		if (cfgCoverage != null) {
			return getRuntimeInfo(cfgCoverage);
		} 
		return null;
	}

	private void setLearnState(PrecondDecisionLearner learner, RunTimeInfo info) {
		boolean hasPoorFormula = false, hasValidFormula = false;;
		if (!((PrecondDecisionLearner)learner).learnedFormulas.isEmpty()) {
			for (Entry<CfgNode, FormulaInfo> entry : ((PrecondDecisionLearner)learner).learnedFormulas.entrySet()){
				info.learnedFormulas.add(entry.getValue());
				if (entry.getValue().getLearnedState() == FormulaInfo.INVALID) {
					hasPoorFormula = true;					
				}else if (entry.getValue().getLearnedState() == FormulaInfo.VALID) {
					hasValidFormula = true;
				}
			}
			if (hasValidFormula) {
				if (hasPoorFormula) {
					info.learnState = 2;
				}else {
					info.learnState = 1;
				}
			}else if (hasPoorFormula) {
				info.learnState = -1;
			}else {
				info.learnState = 0;
			}
		}
		info.setDomainMap(learner.getDominationMap());
		
	}

	private void copyTestsToResultFolder(LearnTestParams params) {
		JavaFileCopier.copy(params.getInitialTests().getJunitFiles(), params.getTestPackage(GenTestPackage.INIT),
				params.getTestPackage(GenTestPackage.RESULT), appClasspath.getTestSrc());
	}

	protected void prepareInitTestcase(LearnTestParams params) throws SavException, ClassNotFoundException, IOException {
		if (!params.getInitialTestcases().isEmpty()) {
			logInitTests(params.getInitialTests().getJunitClasses());
			return;
		}
		/* init test */
		GentestParams gentestParams = LearntestParamsUtils.createGentestParams(appClasspath, params, GenTestPackage.INIT);
		randomGenerateInitTestWithBestEffort(params, gentestParams);
		logInitTests(params.getInitialTests().getJunitClasses());
	}

	private void logInitTests(List<String> junitClasses) {
		log.info("Initial junit classes: {}", junitClasses);
	}

	/**
	 * init fields.
	 */
	protected void init(LearnTestParams params) {
		mediator = new LearningMediator(appClasspath, params);
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
