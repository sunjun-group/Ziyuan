package learntest.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jacop.core.Domain;

import cfgcoverage.jacoco.CfgJaCoCo;
import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import icsetlv.DefaultValues;
import icsetlv.common.dto.BreakpointValue;
import japa.parser.ParseException;
import learntest.breakpoint.data.DecisionBkpsData;
import learntest.breakpoint.data.DecisionLocation;
import learntest.cfg.CfgHandlerAdapter;
import learntest.cfg.CfgHandlerAdapter.CfgAproach;
import learntest.cfg.ICfgHandler;
import learntest.core.commons.data.LearnTestApproach;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.commons.utils.DomainUtils;
import learntest.core.gentest.GentestResult;
import learntest.exception.LearnTestException;
import learntest.sampling.JavailpSelectiveSampling;
import learntest.testcase.TestcasesExecutorwithLoopTimes;
import learntest.testcase.data.BreakpointData;
import learntest.testcase.data.BreakpointDataBuilder;
import learntest.util.LearnTestUtil;
import sav.common.core.SavException;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StopTimer;
import sav.settings.SAVExecutionTimeOutException;
import sav.settings.SAVTimer;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.vm.JavaCompiler;
import sav.strategies.vm.VMConfiguration;

public class LearnTest {

	private AppJavaClassPath appClassPath;
	private TestcasesExecutorwithLoopTimes tcExecutor;

	private BreakpointDataBuilder dtBuilder;
	private JavaCompiler jcompiler;

	public LearnTest(AppJavaClassPath appClassPath) {
		this.appClassPath = appClassPath;
		jcompiler = new JavaCompiler(new VMConfiguration(appClassPath));
	}

	public RunTimeInfo run(boolean random) throws LearnTestException {
		try {
			LearnTestParams params = LearnTestParams.initFromLearnTestConfig();
			params.setApproach(random ? LearnTestApproach.RANDOOP : LearnTestApproach.L2T);
			return run(params);
		} catch (ParseException e) {
			throw new LearnTestException(e);
		} catch (IOException e) {
			throw new LearnTestException(e);
		} catch (ClassNotFoundException e) {
			throw new LearnTestException(e);
		} catch (SavException e) {
			throw new LearnTestException(e);
		}
	}

	public RunTimeInfo run(LearnTestParams params)
			throws LearnTestException, ParseException, IOException, SavException, ClassNotFoundException {
		SAVTimer.startCount();
		/* collect testcases in project */
		List<String> testcases = collectExistingTestcases(params.getTestClass());
		if (CollectionUtils.isEmpty(testcases)) {
			return null;
		}

		/* collect coverage and build cfg */
		StopTimer timer = new StopTimer("jacoco");
		timer.start();
		timer.newPoint("start");
		TargetMethod targetMethod = params.getTargetMethod();
		Collection<CfgCoverage> cfgcoverage = runCfgCoverage(params, targetMethod);
		// System.out.println(cfgcoverage);
		timer.stop();
		System.out.println(timer.getResults());

		ICfgHandler cfgHandler = new CfgHandlerAdapter(appClassPath, params, CfgAproach.SOURCE_CODE_LEVEL);
		dtBuilder = new BreakpointDataBuilder(cfgHandler.getDecisionBkpsData());

		System.currentTimeMillis();

		long time = -1;
		double coverage = 0;
		int testCnt = 1;

		JavailpSelectiveSampling selectiveSampling = null;
		DecisionLearner learner = null;

		try {
			/**
			 * run testcases
			 */
			initTcExecutor(cfgHandler.getDecisionBkpsData(), targetMethod);
			tcExecutor.setup(appClassPath, testcases);
			tcExecutor.run();
			Map<DecisionLocation, BreakpointData> result = tcExecutor.getResult();

			if (CollectionUtils.isEmpty(tcExecutor.getCurrentTestInputValues())) {
				return null;
			}

			if (result.isEmpty()) {
				List<BreakpointValue> tests = tcExecutor.getCurrentTestInputValues();
				if (tests != null && !tests.isEmpty()) {
					BreakpointValue test = tests.get(0);
					Set<ExecVar> allVars = new HashSet<ExecVar>();
					collectExecVar(test.getChildren(), allVars);
					List<ExecVar> vars = new ArrayList<ExecVar>(allVars);
					List<BreakpointValue> list = new ArrayList<BreakpointValue>();
					list.add(test);

					/* GENERATE NEW TESTCASES */
					gentestAccordingToSolutions(DomainUtils.buildSolutions(list, vars), vars);
					System.out.println("Total test cases number: " + testCnt);
					coverage = 1;
				}
			} else {
				tcExecutor.setjResultFileDeleteOnExit(true);
				tcExecutor.setInstrMode(true);
				// selectiveSampling = new JacopSelectiveSampling(tcExecutor);
				selectiveSampling = new JavailpSelectiveSampling(tcExecutor);
				selectiveSampling.addPrevValues(tcExecutor.getCurrentTestInputValues());
				learner = new DecisionLearner(selectiveSampling, cfgHandler, params.isLearnByPrecond());
				learner.learn(result);
				coverage = learner.getCoverage();

				try {
					List<Domain[]> domainList = DomainUtils.buildSolutions(learner.getRecords(),
							learner.getOriginVars());
					gentestAccordingToSolutions(domainList, learner.getOriginVars());
				} catch (Exception e) {
					System.out.println(e);
				}

				testCnt = selectiveSampling.getTotalNum();
				System.out.println("Total test cases number: " + testCnt);
			}

			time = SAVTimer.getExecutionTime();
		} catch (SAVExecutionTimeOutException e) {
			if (learner != null) {
				coverage = learner.getCoverage();
				List<Domain[]> domainList = DomainUtils.buildSolutions(learner.getRecords(), learner.getOriginVars());
				gentestAccordingToSolutions(domainList, learner.getOriginVars());
				testCnt = selectiveSampling.getTotalNum();
				System.out.println("Total test cases number: " + testCnt);
			}
			e.printStackTrace();
		}

		RunTimeInfo info = new RunTimeInfo(time, coverage, testCnt);
		return info;
	}
	
	private void gentestAccordingToSolutions(List<Domain[]> domainList, List<ExecVar> vars)
			throws ClassNotFoundException, SavException {
//		GentestResult gentestResult = new TestGenerator(appClassPath).genTestAccordingToSolutions(domainList, vars);
//		jcompiler.compile(appClassPath.getTestTarget(), gentestResult.getJunitfiles());
	}

	private Collection<CfgCoverage> runCfgCoverage(LearnTestParams params, TargetMethod targetMethod)
			throws SavException, IOException, ClassNotFoundException {
		CfgJaCoCo cfgCoverage = new CfgJaCoCo(appClassPath);
		List<String> targetMethods = CollectionUtils
				.listOf(ClassUtils.toClassMethodStr(targetMethod.getClassName(), targetMethod.getMethodName()));
		Map<String, CfgCoverage> coverage = cfgCoverage.runJunit(targetMethods,
				Arrays.asList(params.getTargetMethod().getClassName()), Arrays.asList(params.getTestClass()));
		return coverage.values();
	}

//	private List<Domain[]> getFullSolutions(List<BreakpointValue> records, List<ExecVar> originVars) {
//		List<Domain[]> res = new ArrayList<Domain[]>();
//		int size = originVars.size();
//		for (BreakpointValue record : records) {
//			Domain[] solution = new Domain[size + (size + 1) * size / 2];
//			int i = 0;
//			for (; i < size; i++) {
//				double value = record.getValue(originVars.get(i).getLabel(), 0.0).doubleValue();
//				solution[i] = new FloatIntervalDomain(value, value);
//			}
//			for (int j = 0; j < size; j++) {
//				double value = record.getValue(originVars.get(j).getLabel(), 0.0).doubleValue();
//				for (int k = j; k < size; k++) {
//					double tmp = value * record.getValue(originVars.get(k).getLabel(), 0.0).doubleValue();
//					solution[i++] = new FloatIntervalDomain(tmp, tmp);
//				}
//			}
//			res.add(solution);
//		}
//		return res;
//	}

	private List<String> collectExistingTestcases(String testClass) {
		org.eclipse.jdt.core.dom.CompilationUnit cu = LearnTestUtil.findCompilationUnitInProject(testClass);
		List<org.eclipse.jdt.core.dom.MethodDeclaration> mList = LearnTestUtil.findTestingMethod(cu);

		List<String> result = new ArrayList<String>();
		for (org.eclipse.jdt.core.dom.MethodDeclaration m : mList) {
			String testcaseName = testClass + "." + m.getName();
			result.add(testcaseName);
		}

		return result;
	}

	private void initTcExecutor(DecisionBkpsData decisionBkpsData, TargetMethod targetMethod) {
		if (tcExecutor == null) {
			tcExecutor = new TestcasesExecutorwithLoopTimes(DefaultValues.DEBUG_VALUE_RETRIEVE_LEVEL);
		}
//		ModifierAgentVmRunner vmRunner = ModifierAgentVmRunner.createNew()
//						.modifiedClass(targetMethod.getClassName()).modifiedMethod(targetMethod.getMethodName());
//		tcExecutor.setDebugger(new SimpleDebugger(vmRunner));
		tcExecutor.setBuilder(dtBuilder);
		tcExecutor.setDecisionBkpsData(decisionBkpsData);
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
