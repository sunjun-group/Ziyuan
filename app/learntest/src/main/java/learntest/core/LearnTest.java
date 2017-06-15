package learntest.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jacop.core.Domain;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.BreakpointValue;
import learntest.core.commons.data.classinfo.JunitTestsInfo;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.utils.CoverageUtils;
import learntest.core.commons.utils.DomainUtils;
import learntest.core.gentest.GentestParams;
import learntest.core.gentest.TestGenerator.GentestResult;
import learntest.core.machinelearning.PrecondDecisionLearner;
import learntest.exception.LearnTestException;
import learntest.main.LearnTestParams;
import learntest.main.RunTimeInfo;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.settings.SAVTimer;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;

public class LearnTest extends AbstractLearntest {
	private LearningMediator mediator;
	
	public LearnTest(AppJavaClassPath appClasspath) {
		super(appClasspath);
	}
	
	public RunTimeInfo run(LearnTestParams params) throws Exception {
		prepareInitTestcase(params);
		SAVTimer.startCount();
		/* collect testcases in project */
		if (CollectionUtils.isEmpty(params.getInitialTestcases())) {
			System.out.println("empty testcase!");
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
				System.out.println("start node is not covered!");
				return null;
			}
			System.out.println("first coverage: " + CoverageUtils.calculateCoverage(cfgCoverage));
			BreakPoint methodEntryBkp = BreakpointCreator.createMethodEntryBkp(targetMethod);
			/**
			 * run testcases
			 */
			BreakpointData result = executeTestcaseAndGetTestInput(params.getInitialTestcases(), methodEntryBkp);
			System.out.println();
			if (CoverageUtils.noDecisionNodeIsCovered(cfgCoverage)) {
				System.out.println("no decision node is covered!");
				return regenerateTestNotLearning(cfgCoverage, result);
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
			System.out.println("Warning: cannot get entry value when coverage is still not empty!");
		}
		return null;
	}

	protected void prepareInitTestcase(LearnTestParams params) throws SavException {
		/* init test */
		GentestParams gentestParams = params.initGentestParams(appClasspath);
		GentestResult initTests = generateTestcases(gentestParams);
		params.setInitialTests(new JunitTestsInfo(initTests.getJunitClassNames(), appClasspath.getClassLoader()));
	}

	/**
	 * run coverage, and in case the coverage is too bad (means no branch is covered)
	 * try to generate another testcase.
	 */
	private CfgCoverage tryBestForInitialCoverage(LearnTestParams params, TargetMethod targetMethod)
			throws SavException, IOException, ClassNotFoundException {
		CfgCoverage cfgCoverage = null;
		int i;
		for (i = 0; i < 3; i++) {
			cfgCoverage = runCfgCoverage(targetMethod, params.getInitialTests().getJunitClasses());
			if (CoverageUtils.notCoverAtAll(cfgCoverage) || CoverageUtils.noDecisionNodeIsCovered(cfgCoverage)) {
				String newTestClass = randomGentest();
				params.getInitialTests().addJunitClass(newTestClass, appClasspath.getClassLoader());
			} else {
				break;
			}
		}
		if (i > 0) {
			System.out.println(String.format("Get best coverage after regenerate test %d times", i));
		}
		return cfgCoverage;
	}

	private RunTimeInfo regenerateTestNotLearning(CfgCoverage cfgCoverage, BreakpointData result)
			throws ClassNotFoundException, SavException, IOException {
		CfgCoverage newCoverage = new CfgCoverage(cfgCoverage.getCfg());
		/* generate new testcases */
		GentestResult gentestResult = createSolutionAndGentest(result);
		/* update coverage */
		mediator.runCoverageForGeneratedTests(CoverageUtils.getCfgCoverageMap(newCoverage), 
				gentestResult.getJunitClassNames());
		return getRuntimeInfo(newCoverage);
	}
	
	private String randomGentest()
			throws ClassNotFoundException, SavException, IOException {
		GentestResult result = mediator.getTestGenerator().genTest();
		/* update coverage */
		mediator.compile(result.getJunitfiles());
		return result.getJunitClassNames().get(0);
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

	private GentestResult createSolutionAndGentest(BreakpointData result)
			throws ClassNotFoundException, SavException {
		List<BreakpointValue> tests = result.getAllValues();
		if (tests != null && !tests.isEmpty()) {
			BreakpointValue test = tests.get(0);
			Set<ExecVar> allVars = new HashSet<ExecVar>();
			collectExecVar(test.getChildren(), allVars);
			List<ExecVar> vars = new ArrayList<ExecVar>(allVars);
			List<BreakpointValue> list = new ArrayList<BreakpointValue>();
			list.add(test);

			/* GENERATE NEW TESTCASES */
			List<Domain[]> solutions = DomainUtils.buildSolutions(list, vars);
			return genterateTestFromSolutions(vars, solutions);
		}
		return GentestResult.getEmptyResult();
	}

	private void collectExecVar(List<ExecValue> vals, Set<ExecVar> vars) {
		if (CollectionUtils.isEmpty(vals)) {
			return;
		}
		for (ExecValue val : vals) {
			if (val == null || CollectionUtils.isEmpty(val.getChildren())) {
				String varId = val.getVarId();
				vars.add(new ExecVar(varId, val.getType()));
			}
			collectExecVar(val.getChildren(), vars);
		}
	}

}
