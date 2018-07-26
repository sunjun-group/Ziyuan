package learntest.activelearning.core.distribution;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.model.UnitTestSuite;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.core.commons.data.classinfo.MethodInfo;
import microbat.instrumentation.cfgcoverage.graph.CoveragePath;
import sav.common.core.SavRtException;
import sav.strategies.dto.AppJavaClassPath;


public class RandomTestDistributionRunner {
	private Logger log = LoggerFactory.getLogger(RandomTestDistributionRunner.class);
	
	public void run(AppJavaClassPath appClasspath, MethodInfo targetMethod, LearntestSettings settings) throws Exception {
		log.info("Run method: " + targetMethod.toString());
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
