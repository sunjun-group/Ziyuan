package learntest.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cfgcoverage.jacoco.CfgJaCoCo;
import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.utils.CfgJaCoCoUtils;
import icsetlv.DefaultValues;
import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.variable.TestcasesExecutor;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.data.testtarget.TargetMethod;
import learntest.core.commons.utils.CoverageUtils;
import learntest.core.commons.utils.DomainUtils;
import learntest.core.machinelearning.PrecondDecisionLearner;
import learntest.main.LearnTestParams;
import learntest.main.RunTimeInfo;
import learntest.main.TestGenerator;
import learntest.main.TestGenerator.GentestResult;
import learntest.util.LearnTestUtil;
import sav.common.core.SavException;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StopTimer;
import sav.settings.SAVExecutionTimeOutException;
import sav.settings.SAVTimer;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;

public class LearnTest {
	private AppJavaClassPath appClassPath;
	private TestcasesExecutor testcaseExecutor;
	private StopTimer timer;
	private LearningMediator mediator;
	
	public LearnTest(AppJavaClassPath appClassPath){
		this.appClassPath = appClassPath;
	}

	public RunTimeInfo run(LearnTestParams params) throws ClassNotFoundException, SavException, IOException {
		timer = new StopTimer("Learntest");
		SAVTimer.startCount();
		/* collect testcases in project */
		List<String> testcases = collectExistingTestcases(params.getTestClass());
		if (CollectionUtils.isEmpty(testcases)) {
			return null;
		}
		init(params);
		boolean learningStarted = false;
		CfgCoverage cfgCoverage = null;
		try {
			/* collect coverage and build cfg */
			TargetMethod targetMethod = params.getTargetMethod();
			cfgCoverage = runCfgCoverage(targetMethod, params.getTestClass());
			targetMethod.updateCfgIfNotExist(cfgCoverage.getCfg());

			if (CoverageUtils.notCoverAtAll(cfgCoverage)) {
				return null;
			}

			BreakPoint methodEntryBkp = BreakpointCreator.createMethodEntryBkp(targetMethod);
			/**
			 * run testcases
			 */
			ensureTestcaseExecutor();
			testcaseExecutor.setup(appClassPath, testcases);
			testcaseExecutor.run(CollectionUtils.listOf(methodEntryBkp, 1));
			BreakpointData result = CollectionUtils.getFirstElement(testcaseExecutor.getResult());

			if (CoverageUtils.noDecisionNodeIsCovered(cfgCoverage)) {
				return regenerateTestNotLearning(cfgCoverage, result);
			} else {
				/* learn */
				PrecondDecisionLearner learner = mediator.initDecisionLearner(params.isLearnByPrecond());
				learningStarted = true;
				DecisionProbes probes = learner.learn(initProbes(cfgCoverage, result));
				return getRuntimeInfo(probes);
			}
		} catch (SAVExecutionTimeOutException e) {
			if (learningStarted) {
				// LLT: still trying to figure out what to do with new approach.
				if (cfgCoverage != null) {
					return getRuntimeInfo(cfgCoverage);
				} 
			}
		}
		return null;
	}

	private RunTimeInfo regenerateTestNotLearning(CfgCoverage cfgCoverage, BreakpointData result)
			throws ClassNotFoundException, SavException, IOException {
		CfgCoverage newCoverage = new CfgCoverage(cfgCoverage.getCfg());
		/* generate new testcases */
		GentestResult gentestResult = createSolutionAndGentest(result);
		/* update coverage */
		mediator.compile(gentestResult.getJunitfiles());
		mediator.runCoverageForGeneratedTests(CoverageUtils.getCfgCoverageMap(newCoverage), 
				gentestResult.getJunitClassNames());
		return getRuntimeInfo(newCoverage);
	}
	
	private RunTimeInfo getRuntimeInfo(CfgCoverage cfgCoverage) {
		return new RunTimeInfo(SAVTimer.getExecutionTime(), CoverageUtils.calculateCoverage(cfgCoverage),
				cfgCoverage.getTestcases().size());
	}

	/**
	 * init fields.
	 */
	private void init(LearnTestParams params) {
		mediator = new LearningMediator(appClassPath, params.getTargetMethod());
	}

	private DecisionProbes initProbes(CfgCoverage cfgcoverage, BreakpointData result) {
		DecisionProbes probes = new DecisionProbes(cfgcoverage);
		probes.setRunningResult(result.getAllValues());
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
			GentestResult gentestResult = new TestGenerator(appClassPath)
					.genTestAccordingToSolutions(DomainUtils.buildSolutions(list, vars), vars);
			return gentestResult;
		}
		return GentestResult.getEmptyResult();
	}
	
	public TestcasesExecutor ensureTestcaseExecutor() {
		if (testcaseExecutor == null) {
			testcaseExecutor = new TestcasesExecutor(DefaultValues.DEBUG_VALUE_RETRIEVE_LEVEL);
		}
		return testcaseExecutor;
	}
	
	private CfgCoverage runCfgCoverage(TargetMethod targetMethod, String testClasses)
			throws SavException, IOException, ClassNotFoundException {
		timer.newPoint("start cfgCoverage");
		CfgJaCoCo cfgCoverage = new CfgJaCoCo(appClassPath);
		List<String> targetMethods = CollectionUtils.listOf(ClassUtils.toClassMethodStr(targetMethod.getClassName(),
				targetMethod.getMethodName()));
		Map<String, CfgCoverage> coverage = cfgCoverage.runJunit(targetMethods, Arrays.asList(targetMethod.getClassName()),
				Arrays.asList(testClasses));
		timer.newPoint("end cfgCoverage");
		return coverage.get(CfgJaCoCoUtils.createMethodId(targetMethod.getClassName(), targetMethod.getMethodName(),
				targetMethod.getMethodSignature()));
	}
	
	private List<String> collectExistingTestcases(String testClass) {
		org.eclipse.jdt.core.dom.CompilationUnit cu = LearnTestUtil.findCompilationUnitInProject(testClass);
		List<org.eclipse.jdt.core.dom.MethodDeclaration> mList = LearnTestUtil.findTestingMethod(cu);
		
		List<String> result = new ArrayList<String>();
		for(org.eclipse.jdt.core.dom.MethodDeclaration m: mList){
			String testcaseName = testClass + "." + m.getName();
			result.add(testcaseName);
		}
		
		return result;
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
