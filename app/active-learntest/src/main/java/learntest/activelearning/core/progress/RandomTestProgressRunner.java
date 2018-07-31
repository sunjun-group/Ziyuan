package learntest.activelearning.core.progress;

import java.io.File;
import java.util.Queue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.model.UnitTestSuite;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.activelearning.core.coverage.CoverageUtils;
import learntest.activelearning.core.distribution.DistributionExcelWriter;
import learntest.activelearning.core.distribution.DistributionRow;
import learntest.activelearning.core.distribution.RandomTestDistributionRunner;

import learntest.activelearning.core.progress.ProgressHeader;
import learntest.activelearning.core.progress.ProgressRow;
import learntest.activelearning.core.progress.ProgressExcelWriter;



import learntest.core.commons.data.classinfo.MethodInfo;
import microbat.instrumentation.cfgcoverage.InstrumentationUtils;
import microbat.instrumentation.cfgcoverage.graph.CFGInstance;
import microbat.instrumentation.cfgcoverage.graph.CFGUtility;
import microbat.instrumentation.cfgcoverage.graph.CoverageGraphConstructor;
import microbat.instrumentation.cfgcoverage.graph.CoveragePath;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFNode;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFlowGraph;
import sav.common.core.SavRtException;
import sav.strategies.dto.AppJavaClassPath;

public class RandomTestProgressRunner {

	private Logger log = LoggerFactory.getLogger(RandomTestDistributionRunner.class);

	public void run(AppJavaClassPath appClasspath, MethodInfo targetMethod, LearntestSettings settings) throws Exception {
		log.info("Run method: " + targetMethod.toString());
		Tester tester = new Tester(settings);
		settings.setInitRandomTestNumber(1);
		
		UnitTestSuite testsuite = null;
		long startTime = 0;
		long endTime = 0; 
		int interval = 15000;
		int numInterval = 12;
		double[] progress = new double[numInterval];
		for(int i = 0; i < 12; i++) {
			startTime = System.currentTimeMillis();
			do {
				testsuite = tester.createRandomTest(targetMethod, settings, appClasspath);
				endTime = System.currentTimeMillis();
				if(endTime - startTime>=interval)break;
			}while(true);
			
		}

			
		if (testsuite == null) {
			throw new SavRtException("Fail to generate random test!");
		}
					
       
		ProgressExcelWriter writer = new ProgressExcelWriter(new File("D:/progress.xlsx"));
		ProgressRow trial = new ProgressRow();
		trial.setMethodName(targetMethod.getMethodFullName());
		trial.setProgress(progress);
		writer.addRowData(trial);

	}
	

}
