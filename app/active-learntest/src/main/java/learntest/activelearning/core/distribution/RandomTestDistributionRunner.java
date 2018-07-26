package learntest.activelearning.core.distribution;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cfg.CFG;
import cfg.utils.CfgConstructor;
import cfgextractor.CFGBuilder;
import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.model.UnitTestSuite;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.core.BreakpointCreator;
import learntest.core.commons.data.classinfo.MethodInfo;
import microbat.instrumentation.cfgcoverage.graph.CoveragePath;
import sav.common.core.SavRtException;
import sav.strategies.dto.AppJavaClassPath;
import variable.Variable;


public class RandomTestDistributionRunner {

	public void run(AppJavaClassPath appClasspath, MethodInfo targetMethod, LearntestSettings settings) throws Exception {

		CfgConstructor cfgConstructor = new CfgConstructor();
		CFG cfg = cfgConstructor.constructCFG(appClasspath, targetMethod.getClassName(),
				targetMethod.getMethodSignature(), settings.getCfgExtensionLayer());
		/* <offset, relevant variables> */
		Map<Integer, List<Variable>> relevantVarMap = new CFGBuilder().parsingCFG(appClasspath,
				targetMethod.getClassName(), targetMethod.getMethodFullName(), targetMethod.getLineNum(), targetMethod.getMethodSignature())
				.getRelevantVarMap();
		cfg.getEntryPoint().setVars(BreakpointCreator.toBkpVariables(relevantVarMap));

		/* generate random test */
		Tester tester = new Tester(settings);
		settings.setInitRandomTestNumber(100);
		UnitTestSuite testsuite = tester.createRandomTest(targetMethod, settings, appClasspath);
			
		if (testsuite == null) {
			throw new SavRtException("Fail to generate random test!");
		}
		/* extract and order the distribution of cases*/

		List<CoveragePath> distributionPath = testsuite.getCoverageGraph().getCoveragePaths();
		Integer[] distribution = new Integer[distributionPath.size()];
		for(int j = 0; j < distributionPath.size(); j++)    {   
		       distribution[j] = distributionPath.get(j).getCoveredTcs().size();  
		   }   
		Arrays.sort(distribution);
		int t;
		for(int k = 0; k < (distribution.length / 2); k++) {
			t = distribution[k];
			distribution[k] = distribution[distribution.length - 1 - k];
			distribution[distribution.length - 1 - k]  = t;
		}				
       
		DistributionExcelWriter writer = new DistributionExcelWriter(new File("D:/distribution.xlsx"));
		DistributionRow trial = new DistributionRow();
		trial.setMethodName(targetMethod.getMethodFullName());
		trial.setDistribution(distribution);
		writer.addRowData(trial);

	}

	
}
