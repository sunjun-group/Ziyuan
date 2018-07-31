package learntest.activelearning.core.distribution;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.model.UnitTestSuite;
import learntest.activelearning.core.settings.LearntestSettings;
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


public class RandomTestDistributionRunner {
	private Logger log = LoggerFactory.getLogger(RandomTestDistributionRunner.class);
	
	public void run(AppJavaClassPath appClasspath, MethodInfo targetMethod, LearntestSettings settings) throws Exception {
		log.info("Run method: " + targetMethod.toString());
		Tester tester = new Tester(settings);
		settings.setInitRandomTestNumber(100);
		
		/*filter out method without branches*/
		CFGUtility cfgUtility = new CFGUtility();
		CFGInstance cfgInstance = cfgUtility.buildProgramFlowGraph(appClasspath,
				InstrumentationUtils.getClassLocation(targetMethod.getClassName(), targetMethod.getMethodSignature()),
				settings.getCfgExtensionLayer());
		CoverageGraphConstructor constructor = new CoverageGraphConstructor();
		CoverageSFlowGraph coverageSFlowGraph = constructor.buildCoverageGraph(cfgInstance);
		if(coverageSFlowGraph.getNodeList().size() == 1) return;
		/*----------------------------------*/
			
		UnitTestSuite testsuite = tester.createRandomTest(targetMethod, settings, appClasspath);
			
		if (testsuite == null) {
			throw new SavRtException("Fail to generate random test!");
		}
		
		/* extract and order the distribution of cases*/
		List<CoveragePath> distributionPath = testsuite.getCoverageGraph().getCoveragePaths();
		List<CoveragePath> allPaths = new ArrayList<CoveragePath>();
		allPaths = findAllPathInSFlowGraph(testsuite.getCoverageGraph());
		
		/*merge two list of paths*/
		for(int l = 0; l < distributionPath.size(); l ++){
			for(int m = 0; m < allPaths.size(); m ++){
				if(pathEqual(allPaths.get(m).getPath(), distributionPath.get(l).getPath())) {
					allPaths.remove(m);
					m --;
				}
			}
		}
		distributionPath.addAll(allPaths);
		/*----------------------*/
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

	private boolean pathEqual(List<Integer> path, List<Integer> path2) {
		if(path.size()!=path2.size()) {
			return false;
		}
		Integer[] pathint =  path.toArray(new Integer[path.size()]);
		Integer[] pathint2 = path2.toArray(new Integer[path2.size()]);
		int len = pathint.length;
		for(int i = 0;i <= len; i++){
			if(pathint[i] != pathint2[i]) {
				return false;
			}
		}
		return true;
	}

	private List<CoveragePath> findAllPathInSFlowGraph(CoverageSFlowGraph coverageGraph) {
		List<CoveragePath> allpath = new ArrayList<CoveragePath>();
		Stack<CoverageSFNode> stack = new Stack<CoverageSFNode>();
		stack.push(coverageGraph.getStartNode());
		List<Integer> path = new ArrayList<Integer>();
		List<Integer> Testcases = new ArrayList<Integer>();
		CoveragePath newPath = new CoveragePath();
		//List<Boolean> visited = new ArrayList<Boolean>(coverageGraph.getNodeList().size());
		boolean[] visited = new boolean[coverageGraph.getNodeList().size()];
		for(int j = 0; j < visited.length; j++) {
			visited[j] = false;
		}
		visited[0] = true;
		do {
			if(coverageGraph.getExitList().contains(stack.peek())) {
				path.clear();
				for(int i = 0; i < stack.size(); i++) {
					path.add(stack.elementAt(i).getCvgIdx());
				}
                newPath.setPath(path);
                newPath.setCoveredTcs(Testcases);
				allpath.add(newPath);
				stack.pop();
			}
			
			for(CoverageSFNode node1 : stack.peek().getBranches()) {
				if (!visited[node1.getCvgIdx()]) {
					visited[node1.getCvgIdx()] =  true;
					stack.push(node1);
					break;
				}
			}
			stack.pop();
			
			
		}while(!stack.empty());
		return allpath;
	}

	
}
