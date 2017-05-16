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
import learntest.core.machinelearning.DecisionLearner;
import learntest.core.machinelearning.PrecondDecisionLearner;
import learntest.main.LearnTestParams;
import learntest.main.RunTimeInfo;
import learntest.main.TestGenerator;
import learntest.util.LearnTestUtil;
import sav.common.core.SavException;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StopTimer;
import sav.settings.SAVTimer;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;

public class LearnTest {
	private AppJavaClassPath appClassPath;
	private TestcasesExecutor testcaseExecutor;
	private StopTimer timer;
	
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

		/* collect coverage and build cfg */
		TargetMethod targetMethod = params.getTargetMethod();
		CfgCoverage cfgcoverage = runCfgCoverage(targetMethod, params.getTestClass());
		targetMethod.updateCfgIfNotExist(cfgcoverage.getCfg());

		if (CoverageUtils.notCoverAtAll(cfgcoverage)) {
			return null;
		}

		BreakPoint methodEntryBkp = BreakpointCreator.createMethodEntryBkp(targetMethod);
		/**
		 * run testcases
		 */
		ensureTestcaseExecutor();
		testcaseExecutor.setup(appClassPath, testcases);
		testcaseExecutor.run(CollectionUtils.listOf(methodEntryBkp, 1));
		icsetlv.common.dto.BreakpointData result = CollectionUtils.getFirstElement(testcaseExecutor.getResult());

		if (CoverageUtils.noDecisionNodeIsCovered(cfgcoverage)) {
			/* generate new testcases */
			createSolutionAndGentest(result);
			return null;
		} else {
			/* learn */
			PrecondDecisionLearner learner = initDecisionLearner(targetMethod, params.isLearnByPrecond());
			learner.learn(initProbes(cfgcoverage, result));
		}
		RunTimeInfo info = null;
		return info;
	}

	private DecisionProbes initProbes(CfgCoverage cfgcoverage, BreakpointData result) {
		DecisionProbes probes = new DecisionProbes(cfgcoverage);
		probes.setRunningResult(result.getAllValues());
		return probes;
	}

	private PrecondDecisionLearner initDecisionLearner(TargetMethod targetMethod, boolean precondApproach) {
		LearningMediator mediator = new LearningMediator(appClassPath, targetMethod, timer);
		if (precondApproach) {
			return new PrecondDecisionLearner(mediator);
		} else {
			return new DecisionLearner(mediator);
		}
	}

	private void createSolutionAndGentest(icsetlv.common.dto.BreakpointData result)
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
			new TestGenerator().genTestAccordingToSolutions(DomainUtils.buildSolutions(list, vars), vars);
		}
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
