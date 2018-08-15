package learntest.evaluation.jdart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.utils.BreakpointDataUtils;
import jdart.core.JDartParams;
import jdart.model.TestInput;
import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.model.UnitTestSuite;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.core.commons.data.classinfo.MethodInfo;
import learntest.core.commons.utils.VarSolutionUtils;
import learntest.core.jdart.JdartTestInputUtils;
import learntest.evaluation.core.CoverageProgressRecorder;
import microbat.instrumentation.cfgcoverage.InstrumentationUtils;
import microbat.instrumentation.cfgcoverage.graph.CFGInstance;
import microbat.instrumentation.cfgcoverage.graph.CFGUtility;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.FileUtils;
import sav.common.core.utils.SingleTimer;
import sav.common.core.utils.StringUtils;
import sav.common.core.utils.TextFormatUtils;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.execute.value.ExecVar;

public class JDartGentest {
	private static Logger log = LoggerFactory.getLogger(JDartGentest.class);
	private String appPropertiesFile;
	private String sitePropertiesFile;
	private String jdartOutputFolder;
	
	public JDartGentest(String appPropertiesFile, String sitePropertiesFile, String jdartFolder) {
		this.appPropertiesFile = appPropertiesFile;
		this.sitePropertiesFile = sitePropertiesFile;
		this.jdartOutputFolder = jdartFolder;
	}

	public void generateTestcase(AppJavaClassPath appClasspath, MethodInfo targetMethod, LearntestSettings settings)
			throws SavException {
		CFGUtility cfgUtility = new CFGUtility();
		CFGInstance cfgInstance = cfgUtility.buildProgramFlowGraph(appClasspath,
				InstrumentationUtils.getClassLocation(targetMethod.getClassName(), targetMethod.getMethodSignature()),
				settings.getCfgExtensionLayer());
		cfgUtility.breakCircle(cfgInstance);

		SingleTimer timer = SingleTimer.start("jdart-learntest");
		timer.setTimeLimit(settings.getRuntimeForEachRound());
		/* generate random test */
		Tester tester = new Tester(settings, true, appClasspath);
		UnitTestSuite initTestsuite = null;
		
		while (initTestsuite == null && timer.getExecutionTime() < settings.getRuntimeForEachRound()) {
			initTestsuite = tester.createInitRandomTest(targetMethod, settings, appClasspath, 3, cfgInstance);
		}
		if (initTestsuite == null) {
			throw new SavException("Fail to generate init tests!!");
		}
		List<String> junitMethods = new ArrayList<>();
		junitMethods.addAll(initTestsuite.getJunitTestcases());
		int round = 0;
		JDartRunner jdartRunner = new JDartRunner(appClasspath);
		CoverageProgressRecorder recorder = new CoverageProgressRecorder(targetMethod, jdartOutputFolder + "/coverage_progress.xlsx", jdartOutputFolder + "/coverage_casenumber.xlsx");
		recorder.setCoverageGraph(initTestsuite.getCoverageGraph());
		recorder.updateNewCoverage(initTestsuite.getCoverageGraph(), initTestsuite.getJunitTestcases().size());
		MainClassGenerator mainClassGenerator = new MainClassGenerator(appClasspath);
		String mainSrcFolder = jdartOutputFolder + "/generate_classes";
		FileUtils.mkDirs(mainSrcFolder);
		while (round < settings.getTestingIteration()) {
			round ++;
			while (true) {
				if (timer.isTimeout()) {
					break;
				}
				String mainClassName = mainClassGenerator.generate(junitMethods, "learntest.jdart", "JunitMethodInvoker", mainSrcFolder);
				Pair<List<TestInput>, Integer> result = jdartRunner.runJDart(initJDartParams(appClasspath, targetMethod),
						mainClassName);
				List<TestInput> inputs = result.a;
				if (CollectionUtils.isEmpty(inputs)) {
					initTestsuite = tester.createInitRandomTest(targetMethod, settings, appClasspath, 3, cfgInstance);
					junitMethods.addAll(initTestsuite.getJunitTestcases());
					continue;
				}
//				logInputSamples(inputs);
				for (TestInput input : inputs) {
					if (timer.isTimeout()) {
						break;
					}
					try {
						List<TestInput> testInput = Arrays.asList(input);
						List<BreakpointValue> bkpVals = JdartTestInputUtils.toBreakpointValue(testInput,
								targetMethod.getMethodFullName());
						List<ExecVar> vars = BreakpointDataUtils.collectAllVarsInturn(bkpVals);
						List<double[]> solutions = VarSolutionUtils.buildSolutions(bkpVals, vars);
						UnitTestSuite testsuite = tester.createTest(targetMethod, settings, appClasspath, solutions,
								vars);
						recorder.updateNewCoverage(testsuite.getCoverageGraph(), testsuite.getJunitTestcases().size());
						initTestsuite = testsuite;
						junitMethods.addAll(testsuite.getJunitTestcases());
					} catch (Exception e) {
						log.debug("Fail to generate testcases according to solutions!");
						// ignore
					}
				}
			}
			recorder.updateProgress();
			timer.restart();
		}
		recorder.store();
	}

	private void logInputSamples(List<TestInput> inputs) {
		if (CollectionUtils.isEmpty(inputs)) {
			log.info("jdart result: {}", TextFormatUtils.printListSeparateWithNewLine(inputs));
			return;
		} else{
			log.info("jdart result (print 10 result at most):");
			for (int i = 0; i < inputs.size() && i < 10; i++) {
				log.info("input: {}", inputs.get(i).toString());
			}
		}
	}
	
	private JDartParams initJDartParams(AppJavaClassPath appClasspath, MethodInfo targetMethod) {
		JDartParams params = new JDartParams();
		params.setAppProperties(appPropertiesFile);
		params.setSiteProperties(sitePropertiesFile);
		params.setClassName(targetMethod.getClassName());
		params.setMethodName(targetMethod.getMethodName());
		params.setParamString(buildJDartParamStr(targetMethod));
		params.setClasspathStr(StringUtils.join(appClasspath.getClasspaths(), ";"));
		params.setMinFree(20 * (1024 << 10));
		params.setTimeLimit(3 * 1000);
		return params;
	}
	
	private String buildJDartParamStr(MethodInfo targetMethod) {
		int lastIdx = targetMethod.getParams().size() - 1;
		StringBuilder sb = new StringBuilder("(");
		for (int i = 0; i <= lastIdx; i++) {
			sb.append(targetMethod.getParams().get(i)).append(":").append(targetMethod.getParamTypes().get(i));
			if (i < lastIdx) {
				sb.append(",");
			}
		}
		sb.append(")");
		return sb.toString();
	}
}
