package learntest.activelearning.core.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gentest.core.data.Sequence;
import gentest.core.data.statement.RConstructor;
import gentest.core.data.statement.Rmethod;
import gentest.core.data.statement.Statement;
import gentest.junit.TestsPrinter.PrintOption;
import icsetlv.common.dto.BreakpointValue;
import learntest.activelearning.core.coverage.CoverageUtils;
import learntest.activelearning.core.data.MethodInfo;
import learntest.activelearning.core.data.TestInputData;
import learntest.activelearning.core.data.UnitTestSuite;
import learntest.activelearning.core.gentest.GentestParams;
import learntest.activelearning.core.gentest.GentestResult;
import learntest.activelearning.core.gentest.TestGenerator;
import learntest.activelearning.core.settings.LearntestSettings;
import microbat.instrumentation.cfgcoverage.CoverageAgentParams.CoverageCollectionType;
import microbat.instrumentation.cfgcoverage.CoverageOutput;
import microbat.instrumentation.cfgcoverage.graph.CFGInstance;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFlowGraph;
import microbat.instrumentation.cfgcoverage.runtime.MethodExecutionData;
import microbat.model.BreakPointValue;
import microbat.model.value.ArrayValue;
import microbat.model.value.PrimitiveValue;
import microbat.model.value.ReferenceValue;
import microbat.model.value.StringValue;
import microbat.model.value.VarValue;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
import sav.common.core.utils.AlphanumComparator;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StopTimer;
import sav.common.core.utils.TextFormatUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.vm.JavaCompiler;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 *
 */
public class Tester {
	private Logger log = LoggerFactory.getLogger(Tester.class);
	private CoverageCounter coverageCounter;
	private CoverageCollectionType cvgType = CoverageCollectionType.UNCIRCLE_CFG_COVERAGE;

	public Tester(LearntestSettings settings, boolean collectConditionVariation, AppJavaClassPath appClasspath) {
		coverageCounter = new CoverageCounter(settings, collectConditionVariation, appClasspath);
	}
	
	public UnitTestSuite createInitRandomTest(MethodInfo targetMethod, LearntestSettings settings,
			AppJavaClassPath appClasspath, int maxTry, CFGInstance cfg) {
		UnitTestSuite testsuite = null;
		for (int i = 0; i < maxTry; i++) {
			try {
				UnitTestSuite initTest = createRandomTest(targetMethod, settings, appClasspath);
				CoverageSFlowGraph coverageGraph = initTest.getCoverageGraph();
				if (CoverageUtils.getBranchCoverage(coverageGraph, targetMethod.getMethodId()) > 0) {
					log.debug(TextFormatUtils
							.printCol(CoverageUtils.getBranchCoverageDisplayTexts(coverageGraph, cfg), "\n"));
					testsuite = initTest;
					break;
				}
				testsuite = initTest;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return testsuite;
	}

	/**
	 * create random testcases and count coverage,
	 * if generated test covers at least first branch, then execute to get input value.
	 */
	StopTimer timer;
	public UnitTestSuite createRandomTest(MethodInfo targetMethod, LearntestSettings settings,
			AppJavaClassPath appClasspath) throws SavRtException {
		timer = new StopTimer("createRandomTest");
		timer.newPoint("geneate test");
		GentestParams params = initGentestParams(targetMethod, settings, appClasspath);
		TestGenerator testGenerator = new TestGenerator(appClasspath);
		try {
			GentestResult testCases = testGenerator.generateRandomTestcases(params);
			UnitTestSuite testsuite = executeTest(targetMethod, settings, appClasspath, testCases);
			log.debug(timer.getResultString());
			return testsuite;
		} catch (Exception e) {
			throw new SavRtException(e);
		}
	}

	private UnitTestSuite executeTest(MethodInfo targetMethod, LearntestSettings settings,
			AppJavaClassPath appClasspath, GentestResult testCases) throws SavException, SAVExecutionTimeOutException {
		UnitTestSuite testSuite = new UnitTestSuite();
		ArrayList<String> junitTest = new ArrayList<>(testCases.getTestcaseSequenceMap().keySet());
		Collections.sort(junitTest, new AlphanumComparator());
		testSuite.setJunitClassNames(testCases.getJunitClassNames());
		testSuite.setJunitTestcases(junitTest);
		testSuite.setJunitfiles(testCases.getJunitfiles());
		testSuite.setMainClass(testCases.getMainClassName());
		testSuite.setTestcaseSequenceMap(testCases.getTestcaseSequenceMap());
		List<Sequence> sequences = null;
		if (!settings.isCoverageRunSocket()) {
			timer.newPoint("compile");
			JavaCompiler javaCompiler = new JavaCompiler(new VMConfiguration(appClasspath));
			javaCompiler.setGenerateDebugInfo(false);
			javaCompiler.compile(appClasspath.getTestTarget(), testCases.getAllFiles());
		} else {
			sequences = new ArrayList<>(testSuite.getJunitTestcases().size());
			for (String tc : testSuite.getJunitTestcases()) {
				sequences.add(testSuite.getTestcaseSequenceMap().get(tc));
			}
			for (Sequence seq : sequences) {
				for (Statement stmt : seq.getStmts()) {
					if (stmt instanceof Rmethod) {
						((Rmethod) stmt).fillMissingMethodInfo();
					}
					if (stmt instanceof RConstructor) {
						((RConstructor) stmt).fillMissingInfo();
					}
				}
			}
		}
		
		timer.newPoint("coverage");
		
		CoverageOutput coverageOutput = coverageCounter.runCoverage(targetMethod, testSuite.getJunitTestcases(),
				appClasspath, settings.getInputValueExtractLevel(), cvgType, sequences);
		testSuite.setCoverageGraph(coverageOutput.getCoverageGraph());
		
		/* transfer input data */
		Map<Integer, TestInputData> inputDataMap = transferInputData(coverageOutput);
		List<TestInputData> inputData = new ArrayList<>(testSuite.getJunitTestcases().size());
		for (int i = 0; i < testSuite.getJunitTestcases().size(); i++) {
			TestInputData input = inputDataMap.get(i);
			inputData.add(input);
		}
		testSuite.setInputData(inputData);
		return testSuite;
	}
	
	private Map<Integer, TestInputData> transferInputData(CoverageOutput coverageOutput) {
		Map<Integer, List<MethodExecutionData>> inputData = coverageOutput.getInputData();
		List<String> testcases = coverageOutput.getCoverageGraph().getCoveredTestcases();
		Map<Integer, TestInputData> resultMap = new HashMap<>();
		if (inputData == null) {
			return resultMap;
		}
		for (Integer testIdx : inputData.keySet()) {
			MethodExecutionData testInputs = CollectionUtils.getLast(inputData.get(testIdx));
			BreakPointValue methodInputValue = testInputs.getMethodInputValue();
			BreakpointValue inputValue = new BreakpointValue(methodInputValue.getName());
			for (VarValue value : methodInputValue.getChildren()) {
				inputValue.add(convert(value));
			}
			resultMap.put(testIdx,
					new TestInputData(testcases.get(testIdx), inputValue, testInputs.getBranchFitnessMap(coverageOutput.getCoverageGraph())));
		}
		return resultMap;
	}
	
	private ExecValue convert(VarValue value) {
		ExecValue execValue = null;
		if (value instanceof StringValue) {
			execValue = new sav.strategies.dto.execute.value.StringValue(value.getVarID(), value.getStringValue());
		} else if (value instanceof PrimitiveValue) {
			execValue = sav.strategies.dto.execute.value.PrimitiveValue.valueOf(value.getVarID(), value.getType(), value.getStringValue());
		} else if (value instanceof ArrayValue) {
			boolean isNull = ((ArrayValue) value).isNull();
			sav.strategies.dto.execute.value.ArrayValue arrValue = new sav.strategies.dto.execute.value.ArrayValue(value.getVarID(), 
					isNull);
			execValue = arrValue;
			if (!isNull) {
				int maxIdx = -1;
				for (VarValue child : value.getChildren()) {
					ExecValue childExecVal = convert(child);
					if (childExecVal != null) {
						arrValue.add(childExecVal);
						maxIdx = Math.max(maxIdx, arrValue.getElements().get(arrValue.getElements().size() - 1).getIdx());
					}
				}
				arrValue.setLength(maxIdx + 1);
			}
		} else if (value instanceof ReferenceValue) {
			ReferenceValue refVal = (ReferenceValue) value;
			execValue = new sav.strategies.dto.execute.value.ReferenceValue(value.getVarID(), refVal.isNull());
		}
		if (execValue != null) {
			execValue.setValueType(value.getRuntimeType() == null ? value.getType() : value.getRuntimeType());
		}
		return execValue;
	}

	public UnitTestSuite createTest(MethodInfo targetMethod, LearntestSettings settings,
			AppJavaClassPath appClasspath, List<BreakpointValue> inputData) throws SavRtException {
//		for(BreakpointValue v: inputData){
//			if(v.getChildren().get(0).getChildren().size() > 10){
//				System.currentTimeMillis();
//			}
//		}
		
		GentestParams params = initGentestParams(targetMethod, settings, appClasspath);
		TestGenerator testGenerator = new TestGenerator(appClasspath);
		try {
			GentestResult testCases = testGenerator.genTestAccordingToSolutions(params, inputData);
			return executeTest(targetMethod, settings, appClasspath, testCases);
		} catch (Exception e) {
			throw new SavRtException(e);
		}
	}
	
	public GentestParams initGentestParams(MethodInfo targetMethod, LearntestSettings settings,
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
		params.setGenerateMainClass(settings.isAddMainClassWhenGeneratingTest());
		return params;
	}
	
	public void setCvgType(CoverageCollectionType cvgType) {
		this.cvgType = cvgType;
	}
	
	public void reset() {
		coverageCounter.stop();
	}
}
