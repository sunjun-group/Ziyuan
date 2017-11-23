package learntest.core;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import cfgextractor.CFGBuilder;
import icsetlv.common.dto.BreakpointValue;
import learntest.core.LearntestParamsUtils.GenTestPackage;
import learntest.core.commons.data.LearnTestApproach;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.exception.LearnTestException;
import learntest.core.commons.test.TestTools;
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
import variable.Variable;

public class LearnTest extends AbstractLearntest {
	private static Logger log = LoggerFactory.getLogger(LearnTest.class);
	
	public LearnTest(AppJavaClassPath appClasspath) {
		super(appClasspath);
	}
	
	public final RunTimeInfo run(LearnTestParams params) throws Exception {
		init(params);
		log.info("Start learntest..({})", params.getApproach().getName());
		prepareInitTestcase(params);
		SAVTimer.startCount();
		/* collect testcases in project */
		if (CollectionUtils.isEmpty(params.getInitialTestcases())) {
			log.info("Empty testcase!");
			return null;
		}
		boolean learningStarted = false;
		CfgCoverage cfgCoverage = null;
		TargetMethod targetMethod = params.getTargetMethod();
		try {
			/* collect coverage and build cfg */
			cfgCoverage = runCfgCoverage(targetMethod, params.getInitialTests().getJunitClasses());

			if (CoverageUtils.notCoverAtAll(cfgCoverage)) {
				log.info("Start node is not covered!");
				return reconcileGeneratedTestsAndGetRuntimeInfo(cfgCoverage, targetMethod, params.isTestMode());
			}
			
			/* <offset, relevant variables> */
			Map<Integer, List<Variable>> relevantVarMap = new CFGBuilder().parsingCFG(getAppClasspath(),
					targetMethod.getClassName(), targetMethod.getMethodFullName(), targetMethod.getLineNum(), targetMethod.getMethodSignature())
					.getRelevantVarMap();
			
			double firstCoverage = CoverageUtils.calculateCoverageByBranch(cfgCoverage);
			TestTools.getInstance().logFirstCoverage(firstCoverage, cfgCoverage);
			log.info("First coverage: " + firstCoverage);
			BreakPoint methodEntryBkp = BreakpointCreator.createMethodEntryBkp(targetMethod, relevantVarMap);
			/**
			 * run testcases
			 */
			List<BreakpointValue> result = executeTestcaseAndGetTestInput(params.getInitialTestcases(), methodEntryBkp);
			if (CoverageUtils.noDecisionNodeIsCovered(cfgCoverage) || (firstCoverage == 1.0)) {
				if (firstCoverage != 1.0) {
					log.info("No decision node is covered!");
				}
				copyTestsToResultFolder(params);
				return reconcileGeneratedTestsAndGetRuntimeInfo(cfgCoverage, targetMethod, params.isTestMode());
			} else {
				/* learn */
				IInputLearner learner = mediator.initDecisionLearner(params);
				DecisionProbes initProbes = initProbes(targetMethod, cfgCoverage, result);
				learningStarted = true;
				
				DecisionProbes probes = learner.learn(initProbes, relevantVarMap);
				
				/** 
				 * In this way, all samples are recorded.
				 *  But record sample data after sample selecting is also important, 
				 *  because we may want to see which sample is selected after learning. 
				 * */
				learner.cleanup();
				learner.recordSample(probes, learner.getLogFile());
				
				RunTimeInfo info = reconcileGeneratedTestsAndGetRuntimeInfo(probes, targetMethod, params.isTestMode());
				if (learner instanceof PrecondDecisionLearner) { 
					setLearnState((PrecondDecisionLearner)learner, info);
					info.setSymbolicTimes(((PrecondDecisionLearner)learner).getSymoblicTimes());
				}
				info.setSample(learner);
				info.setLogFile(learner.getLogFile());
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
			return reconcileGeneratedTestsAndGetRuntimeInfo(cfgCoverage, targetMethod, params.isTestMode());
		} 
		return null;
	}


	private RunTimeInfo reconcileGeneratedTestsAndGetRuntimeInfo(CfgCoverage cfgCoverage, TargetMethod targetMethod, boolean testMode) {
		/* clean up testcases */
//		LineCoverageResult lineCoverageResult = mediator.commitFinalTests(cfgCoverage, targetMethod);
		RunTimeInfo runtimeInfo = getRuntimeInfo(cfgCoverage, testMode);
//		runtimeInfo.setLineCoverageResult(lineCoverageResult);
		return runtimeInfo;
	}

	private void setLearnState(PrecondDecisionLearner learner, RunTimeInfo runtimeInfo) {
		if (!(runtimeInfo instanceof TestRunTimeInfo)) {
			return;
		}
		TestRunTimeInfo info = (TestRunTimeInfo) runtimeInfo;
		boolean hasPoorFormula = false, hasValidFormula = false;;
		if (!((PrecondDecisionLearner)learner).learnedFormulas.isEmpty()) {
			for (Entry<CfgNode, FormulaInfo> entry : ((PrecondDecisionLearner)learner).learnedFormulas.entrySet()){
				info.getLearnedFormulas().add(entry.getValue());
				if (entry.getValue().getLearnedState() == FormulaInfo.INVALID) {
					hasPoorFormula = true;					
				}else if (entry.getValue().getLearnedState() == FormulaInfo.VALID) {
					hasValidFormula = true;
				}
			}
			if (hasValidFormula) {
				if (hasPoorFormula) {
					info.setLearnState(2);
				} else {
					info.setLearnState(1);
				}
			} else if (hasPoorFormula) {
				info.setLearnState(-1);
			} else {
				info.setLearnState(0);
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
		if (CollectionUtils.existIn(params.getApproach(), LearnTestApproach.JDART, LearnTestApproach.L2T)) {
			gentestParams.setGenerateMainClass(true);
		}
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



}
