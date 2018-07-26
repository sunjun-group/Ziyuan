package learntest.activelearning.core.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gentest.junit.TestsPrinter.PrintOption;
import icsetlv.common.dto.BreakpointValue;
import learntest.activelearning.core.model.UnitTestSuite;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.core.commons.data.classinfo.MethodInfo;
import learntest.core.gentest.GentestParams;
import learntest.core.gentest.GentestResult;
import learntest.core.gentest.LearntestJWriter;
import learntest.core.gentest.TestGenerator;
import microbat.instrumentation.cfgcoverage.CoverageOutput;
import microbat.model.BreakPointValue;
import microbat.model.value.ArrayValue;
import microbat.model.value.PrimitiveValue;
import microbat.model.value.ReferenceValue;
import microbat.model.value.StringValue;
import microbat.model.value.VarValue;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
import sav.common.core.utils.CollectionUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.vm.JavaCompiler;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 *
 */
public class Tester {
	private CoverageCounter coverageCounter;

	public Tester(LearntestSettings settings) {
		coverageCounter = new CoverageCounter(settings);
	}

	/**
	 * create random testcases and count coverage,
	 * if generated test covers at least first branch, then execute to get input value.
	 */
	public UnitTestSuite createRandomTest(MethodInfo targetMethod, LearntestSettings settings,
			AppJavaClassPath appClasspath) throws SavRtException {
		GentestParams params = initGentestParams(targetMethod, settings, appClasspath);
		TestGenerator testGenerator = new TestGenerator(appClasspath);
		try {
			GentestResult testCases = testGenerator.generateRandomTestcases(params);
			return executeTest(targetMethod, settings, appClasspath, testCases);
		} catch (Exception e) {
			throw new SavRtException(e);
		}
	}

	private UnitTestSuite executeTest(MethodInfo targetMethod, LearntestSettings settings,
			AppJavaClassPath appClasspath, GentestResult testCases) throws SavException, SAVExecutionTimeOutException {
		JavaCompiler javaCompiler = new JavaCompiler(new VMConfiguration(appClasspath));
		javaCompiler.compile(appClasspath.getTestTarget(), testCases.getAllFiles());
		
		UnitTestSuite testSuite = new UnitTestSuite();
		testSuite.setJunitClassNames(testCases.getJunitClassNames(), appClasspath.getClassLoader());
		testSuite.setJunitfiles(testCases.getJunitfiles());
		testSuite.setTestcaseSequenceMap(testCases.getTestcaseSequenceMap());
		
		CoverageOutput coverageOutput = coverageCounter.runCoverage(targetMethod, testSuite.getJunitTestcases(),
				appClasspath, settings.getInputValueExtractLevel());
		testSuite.setCoverageGraph(coverageOutput.getCoverageGraph());
		
		/* transfer input data */
		Map<Integer, BreakpointValue> inputDataMap = transferInputData(coverageOutput.getInputData());
		List<BreakpointValue> inputData = new ArrayList<>(testSuite.getJunitTestcases().size());
		for (int i = 0; i < testSuite.getJunitTestcases().size(); i++) {
			inputData.add(inputDataMap.get(i));
		}
		testSuite.setInputData(inputData);
		return testSuite;
	}
	
	private Map<Integer, BreakpointValue> transferInputData(Map<Integer, BreakPointValue> inputData) {
		Map<Integer, BreakpointValue> resultMap = new HashMap<>();
		for (Integer testIdx : inputData.keySet()) {
			BreakPointValue bkpValue = inputData.get(testIdx);
			BreakpointValue inputValue = new BreakpointValue(bkpValue.getName());
			for (VarValue value : bkpValue.getChildren()) {
				inputValue.add(transfer(value));
			}
			resultMap.put(testIdx, inputValue);
		}
		return resultMap;
	}
	
	private ExecValue transfer(VarValue value) {
		ExecValue execValue = null;
		if (value instanceof StringValue) {
			execValue = new sav.strategies.dto.execute.value.StringValue(value.getVarID(), value.getStringValue());
		} else if (value instanceof PrimitiveValue) {
			execValue = new sav.strategies.dto.execute.value.PrimitiveValue(value.getVarID(), value.getStringValue());
		} else if (value instanceof ArrayValue) {
			execValue = new sav.strategies.dto.execute.value.ArrayValue(value.getVarID());
		} else if (value instanceof ReferenceValue) {
			ReferenceValue refVal = (ReferenceValue) value;
			execValue = new sav.strategies.dto.execute.value.ReferenceValue(value.getVarID(), refVal.isNull());
		}
		if (execValue != null) {
			for (VarValue child : value.getChildren()) {
				ExecValue childExecVal = transfer(child);
				if (childExecVal != null) {
					execValue.add(childExecVal);
				}
			}
		}
		return execValue;
	}

	public UnitTestSuite createTest(MethodInfo targetMethod, LearntestSettings settings,
			AppJavaClassPath appClasspath, List<double[]> inputData, List<ExecVar> vars) throws SavRtException {
		GentestParams params = initGentestParams(targetMethod, settings, appClasspath);
		TestGenerator testGenerator = new TestGenerator(appClasspath);
		try {
			GentestResult testCases = testGenerator.genTestAccordingToSolutions(params, inputData, vars,
					new LearntestJWriter(params.extractTestcaseSequenceMap()));
			return executeTest(targetMethod, settings, appClasspath, testCases);
		} catch (Exception e) {
			throw new SavRtException(e);
		}
	}
	
	private GentestParams initGentestParams(MethodInfo targetMethod, LearntestSettings settings,
			AppJavaClassPath appClasspath) {
		GentestParams params = new GentestParams();
		params.setMethodExecTimeout(settings.getMethodExecTimeout());
		params.setMethodSignature(targetMethod.getMethodSignature());
		params.setTargetClassName(targetMethod.getClassName());
		params.setNumberOfTcs(settings.getInitRandomTestNumber());
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
