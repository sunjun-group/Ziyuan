package learntest.evaluation.jdart;

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
import microbat.instrumentation.cfgcoverage.InstrumentationUtils;
import microbat.instrumentation.cfgcoverage.graph.CFGInstance;
import microbat.instrumentation.cfgcoverage.graph.CFGUtility;
import microbat.instrumentation.cfgcoverage.graph.CoverageGraphConstructor;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFlowGraph;
import microbat.instrumentation.cfgcoverage.graph.cdg.CDG;
import microbat.instrumentation.cfgcoverage.graph.cdg.CDGConstructor;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;
import sav.common.core.utils.TextFormatUtils;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.execute.value.ExecVar;

public class JDartGentest {
	private static Logger log = LoggerFactory.getLogger(JDartGentest.class);
	private String appPropertiesFile;
	private String sitePropertiesFile;
	
	public JDartGentest(String appPropertiesFile, String sitePropertiesFile) {
		this.appPropertiesFile = appPropertiesFile;
		this.sitePropertiesFile = sitePropertiesFile;
	}

	public void generateTestcase(AppJavaClassPath appClasspath, MethodInfo targetMethod, LearntestSettings settings)
			throws SavException {
		settings.setInitRandomTestNumber(10);
		// settings.setMethodExecTimeout(100);
		CFGUtility cfgUtility = new CFGUtility();
		CFGInstance cfgInstance = cfgUtility.buildProgramFlowGraph(appClasspath,
				InstrumentationUtils.getClassLocation(targetMethod.getClassName(), targetMethod.getMethodSignature()),
				settings.getCfgExtensionLayer());
		cfgUtility.breakCircle(cfgInstance);
		CoverageGraphConstructor constructor = new CoverageGraphConstructor();
		CoverageSFlowGraph coverageSFlowGraph = constructor.buildCoverageGraph(cfgInstance);
		CDGConstructor cdgConstructor = new CDGConstructor();
		CDG cdg = cdgConstructor.construct(coverageSFlowGraph);

		/* generate random test */
		Tester tester = new Tester(settings, true);
		UnitTestSuite initTestsuite = tester.createInitRandomTest(targetMethod, settings, appClasspath, 3, cfgInstance);
		JDartRunner jdartRunner = new JDartRunner(appClasspath);
		Pair<List<TestInput>, Integer> result = jdartRunner.runJDart(initJDartParams(appClasspath, targetMethod),
				initTestsuite.getMainClass());
		List<TestInput> inputs = result.a;
		if (CollectionUtils.isEmpty(inputs)) {
			log.info("jdart result: {}", TextFormatUtils.printListSeparateWithNewLine(inputs));
			return;
		} else{
			log.info("jdart result (print 10 result at most):");
			for (int i = 0; i < inputs.size() && i < 10; i++) {
				log.info("input: {}", inputs.get(i).toString());
			}
		}
		List<BreakpointValue> bkpVals = JdartTestInputUtils.toBreakpointValue(inputs,
				targetMethod.getMethodFullName());
		List<ExecVar> vars = BreakpointDataUtils.collectAllVarsInturn(bkpVals);
		List<double[]> solutions = VarSolutionUtils.buildSolutions(bkpVals, vars);
		UnitTestSuite testsuite = tester.createTest(targetMethod, settings, appClasspath, solutions, vars);
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
			sb.append(targetMethod.getParams().get(i))
				.append(":")
				.append(targetMethod.getParamTypes().get(i));
			if (i < lastIdx) {
				sb.append(",");
			}
		}
		sb.append(")");
		return sb.toString();
	}
}
