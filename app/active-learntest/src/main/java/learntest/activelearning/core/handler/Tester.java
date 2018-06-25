package learntest.activelearning.core.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cfg.CFG;
import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import gentest.junit.JWriter;
import gentest.junit.TestsPrinter.PrintOption;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.variable.TestcasesExecutor;
import learntest.activelearning.core.model.UnitTestSuite;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.core.commons.data.classinfo.JunitTestsInfo;
import learntest.core.commons.data.classinfo.MethodInfo;
import learntest.core.gentest.GentestParams;
import learntest.core.gentest.GentestResult;
import learntest.core.gentest.LearntestJWriter;
import learntest.core.gentest.TestGenerator;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
import sav.common.core.utils.CollectionUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.vm.JavaCompiler;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 *
 */
public class Tester {
	private CoverageCounter coverageCounter = new CoverageCounter();

	/**
	 * create random testcases and count coverage,
	 * if generated test covers at least first branch, then execute to get input value.
	 */
	public UnitTestSuite createRandomTest(MethodInfo targetMethod, LearntestSettings settings, CFG cfg, AppJavaClassPath appClasspath) throws SavRtException {
		GentestParams params = initGentestParams(targetMethod, settings, appClasspath);
		TestGenerator testGenerator = new TestGenerator(appClasspath);
		try {
			GentestResult testCases = testGenerator.generateRandomTestcases(params);
			return executeTest(targetMethod, settings, cfg, appClasspath, testCases);
		} catch (Exception e) {
			throw new SavRtException(e);
		}
	}

	private UnitTestSuite executeTest(MethodInfo targetMethod, LearntestSettings settings, CFG cfg,
			AppJavaClassPath appClasspath, GentestResult testCases) throws SavException, SAVExecutionTimeOutException {
		JavaCompiler javaCompiler = new JavaCompiler(new VMConfiguration(appClasspath));
		javaCompiler.compile(appClasspath.getTestTarget(), testCases.getAllFiles());
		
		UnitTestSuite testSuite = new UnitTestSuite();
		testSuite.setJunitClassNames(testCases.getJunitClassNames(), appClasspath.getClassLoader());
		testSuite.setJunitfiles(testCases.getJunitfiles());
		testSuite.setTestcaseSequenceMap(testCases.getTestcaseSequenceMap());
		CfgCoverage coverage = coverageCounter.countCoverage(targetMethod, testCases.getJunitClassNames(), cfg, appClasspath);
		testSuite.setCoverage(coverage);
		if (coverage.getBranchCoverage() > 0) {
			JunitTestsInfo junitTestsInfo = new JunitTestsInfo(testCases, appClasspath.getClassLoader());
			TestcasesExecutor testcaseExecutor = new TestcasesExecutor(settings.inputValueExtractLevel);
			List<String> junitCases = junitTestsInfo.getJunitTestcases();
			testcaseExecutor.setup(appClasspath, junitCases);
			testcaseExecutor.run(CollectionUtils.listOf(cfg.getEntryPoint(), 1));
			List<BreakpointValue> inputData = new ArrayList<BreakpointValue>(junitCases.size());
			Map<Integer, List<BreakpointValue>> bkpValsMap = testcaseExecutor.getBkpValsByTestIdx();
			for (int i = 0; i < junitCases.size(); i++) {
				CollectionUtils.addIfNotNull(inputData, CollectionUtils.getFirstElement(bkpValsMap.get(i)));
			}
			testSuite.setInputData(inputData);
		}
		
		return testSuite;
	}
	
	public UnitTestSuite createTest(MethodInfo targetMethod, LearntestSettings settings, CFG cfg,
			AppJavaClassPath appClasspath, List<double[]> inputData, List<ExecVar> vars) throws SavRtException {
		GentestParams params = initGentestParams(targetMethod, settings, appClasspath);
		TestGenerator testGenerator = new TestGenerator(appClasspath);
		try {
			GentestResult testCases = testGenerator.genTestAccordingToSolutions(params, inputData, vars,
					new LearntestJWriter(params.extractTestcaseSequenceMap()));
			return executeTest(targetMethod, settings, cfg, appClasspath, testCases);
		} catch (Exception e) {
			throw new SavRtException(e);
		}
	}
	
	private GentestParams initGentestParams(MethodInfo targetMethod, LearntestSettings settings,
			AppJavaClassPath appClasspath) {
		GentestParams params = new GentestParams();
		params.setMethodExecTimeout(settings.methodExecTimeout);
		params.setMethodSignature(targetMethod.getMethodSignature());
		params.setTargetClassName(targetMethod.getClassName());
		params.setNumberOfTcs(settings.initRandomTestNumber);
		params.setTestPerQuery(1);
		params.setTestSrcFolder(appClasspath.getTestSrc());
		
		/* test pkg */
		String methodLc = targetMethod.getMethodName().toLowerCase();
		/* handle keyword cases */
		if (CollectionUtils.existIn(methodLc, "instanceof")) {
			methodLc = methodLc + "ziy";
		}
		String testPkg = String.format("testdata.learntest.%s.%s",
				targetMethod.getTargetClazz().getClassSimpleName().toLowerCase(), methodLc);
		
		params.setTestPkg(testPkg);
		params.setTestClassPrefix(targetMethod.getTargetClazz().getClassSimpleName());
		params.setTestMethodPrefix("test");
		params.setExtractTestcaseSequenceMap(true);
		params.setPrintOption(PrintOption.APPEND);
		return params;
	}
	
}
