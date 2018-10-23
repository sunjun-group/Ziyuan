package learntest.activelearning.core;

import org.apache.bcel.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.activelearning.core.data.MethodInfo;
import learntest.activelearning.core.data.UnitTestSuite;
import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.activelearning.core.testgeneration.SearchBasedTestGenerator;
import microbat.instrumentation.cfgcoverage.InstrumentationUtils;
import microbat.instrumentation.cfgcoverage.graph.CFGInstance;
import microbat.instrumentation.cfgcoverage.graph.CFGUtility;
import microbat.instrumentation.cfgcoverage.graph.cdg.CDG;
import microbat.instrumentation.cfgcoverage.graph.cdg.CDGConstructor;
import sav.common.core.SavRtException;
import sav.strategies.dto.AppJavaClassPath;

public class SearchBasedLearnTest {
	private static Logger log = LoggerFactory.getLogger(NeuralActiveLearnTest.class);

	public void generateTestcase(AppJavaClassPath appClasspath, MethodInfo targetMethod, LearntestSettings settings) throws Exception {
		settings.setInitRandomTestNumber(10);
		//settings.setMethodExecTimeout(100);
		CFGUtility cfgUtility = new CFGUtility();
		Repository.clearCache();
		CFGInstance cfgInstance = cfgUtility.buildProgramFlowGraph(appClasspath,
				InstrumentationUtils.getClassLocation(targetMethod.getClassName(), targetMethod.getMethodSignature()),
				settings.getCfgExtensionLayer());
		cfgUtility.breakCircle(cfgInstance);
		/* generate random test */
		Tester tester = new Tester(settings, true, appClasspath);
		UnitTestSuite testsuite = tester.createInitRandomTest(targetMethod, settings, appClasspath, 3, cfgInstance);
		if (testsuite == null) {
			throw new SavRtException("Fail to generate random test!");
		}
		testsuite.getCoverageGraph().setCfg(cfgInstance);
		CDGConstructor cdgConstructor = new CDGConstructor();
		CDG cdg = cdgConstructor.construct(testsuite.getCoverageGraph());
		
		SearchBasedTestGenerator generator = new SearchBasedTestGenerator(tester, testsuite, appClasspath, targetMethod, settings);
		generator.cover(cdg);
	}
}
