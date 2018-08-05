package learntest.activelearning.core.progress;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.model.UnitTestSuite;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.core.commons.data.classinfo.MethodInfo;
import microbat.instrumentation.cfgcoverage.graph.CoveragePath;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFNode;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFlowGraph;
import sav.strategies.dto.AppJavaClassPath;

public class RandomTestProgressRunner {

	private Logger log = LoggerFactory.getLogger(RandomTestProgressRunner.class);

	public void run(AppJavaClassPath appClasspath, MethodInfo targetMethod, LearntestSettings settings) throws Exception {
		log.info("Run method: " + targetMethod.toString());
		settings.setInitRandomTestNumber(1);
		Tester tester = new Tester(settings, false);
		UnitTestSuite testsuite = null;
		long startTime = 0;
		long endTime = 0; 
		int interval = 10000;
		int numInterval = 9;
		double allBranches = 0.00001;
		double coveredBranches = 0;
		double[] progress = new double[numInterval];
		CoverageSFlowGraph graph = new CoverageSFlowGraph(0);
		testsuite = tester.createRandomTest(targetMethod, settings, appClasspath);
		graph = testsuite.getCoverageGraph();
		for(CoverageSFNode node0 : graph.getNodeList()) {
			if(node0.getBranches().size() > 1)allBranches += node0.getBranches().size();
		}

		int[][] branchTable = new int[testsuite.getCoverageGraph().getNodeList().size()][testsuite.getCoverageGraph().getNodeList().size()];
		int s = testsuite.getCoverageGraph().getNodeList().size();
		for(int a=0; a< s; a++) {
			for(int b=0; b < s; b ++) {
				branchTable[a][b] = 0;
			}
		}
		
		
		startTime = System.currentTimeMillis();
		do {
			testsuite = tester.createRandomTest(targetMethod, settings, appClasspath);
			endTime = System.currentTimeMillis();
            for(CoveragePath path : testsuite.getCoverageGraph().getCoveragePaths()) {
				for(int c=0; c < path.getPath().size() - 1; c++) {
					CoverageSFNode node = path.getPath().get(c);
            		if(node.getBranches().size() > 1)
            		branchTable[node.getCvgIdx()][path.getPath().get(c+1).getCvgIdx()] = 1;
            	}
            }

			if(endTime - startTime>=interval)break;
		}while(true);	
		
		coveredBranches = 0;
		for (int a = 0; a < s; a++) {
			for(int b = 0; b < s; b++) {
				if(branchTable[a][b]==1)
					coveredBranches = coveredBranches + 1;
			}
		}
		System.out.println(coveredBranches);
		progress[0] = coveredBranches / allBranches;
		System.out.println(progress[0]);
		
		for(int i = 1; i < numInterval; i++) {
			startTime = System.currentTimeMillis();
			do {
				testsuite = tester.createRandomTest(targetMethod, settings, appClasspath);
				endTime = System.currentTimeMillis();
				for(CoveragePath path : testsuite.getCoverageGraph().getCoveragePaths()) {
	            	for(int c=0; c < path.getPath().size() - 1; c++) {
	            		CoverageSFNode node = path.getPath().get(c);
	            		if(node.getBranches().size() > 1)
	            		branchTable[node.getCvgIdx()][path.getPath().get(c+1).getCvgIdx()] = 1;
	            	}
	            }

				if(endTime - startTime>=interval)break;
			}while(true);
			coveredBranches = 0;
			for (int a = 0; a < s; a ++) {
				for(int b = 0; b < s; b ++) {
					if(branchTable[a][b] == 1)
						coveredBranches = coveredBranches + 1;
				}
			}
			progress[i] = coveredBranches / allBranches;
			System.out.println(progress[i]);
			
		}

			
		ProgressExcelWriter writer = new ProgressExcelWriter(new File("D:/progress.xlsx"));
		ProgressRow trial = new ProgressRow();
		trial.setMethodName(targetMethod.getMethodFullName());
		trial.setProgress(progress);
		writer.addRowData(trial);

	}


}
