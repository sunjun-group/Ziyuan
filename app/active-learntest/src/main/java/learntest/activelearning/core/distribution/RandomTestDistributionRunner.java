package learntest.activelearning.core.distribution;

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
	private List<CoveragePath> allPath = new ArrayList<CoveragePath>();
	private CoveragePath newPath = new CoveragePath();
	private Stack<CoverageSFNode> stack = new Stack<CoverageSFNode>();
	public void run(AppJavaClassPath appClasspath, MethodInfo targetMethod, LearntestSettings settings) throws Exception {
		log.info("Run method: " + targetMethod.toString());
		Tester tester = new Tester(settings, false);
		//settings.setInitRandomTestNumber(1);
		
		/*filter out method without branches*/
		/*CFGUtility cfgUtility = new CFGUtility();
		CFGInstance cfgInstance = cfgUtility.buildProgramFlowGraph(appClasspath,
				InstrumentationUtils.getClassLocation(targetMethod.getClassName(), targetMethod.getMethodSignature()),
				settings.getCfgExtensionLayer());
		cfgUtility.breakCircle(cfgInstance);
		CoverageGraphConstructor constructor = new CoverageGraphConstructor();
		CoverageSFlowGraph coverageSFlowGraph = constructor.buildCoverageGraph(cfgInstance);
		
		if(coverageSFlowGraph.getNodeList().size() == 1) return;*/
		/*----------------------------------*/
			
		UnitTestSuite testsuite = tester.createRandomTest(targetMethod, settings, appClasspath);
			
		if (testsuite == null) {
			throw new SavRtException("Fail to generate random test!");
		}
		
		/* extract and order the distribution of cases*/
		List<CoveragePath> distributionPath = testsuite.getCoverageGraph().getCoveragePaths();
		//findAllPathInSFlowGraphDfs(testsuite.getCoverageGraph(),testsuite.getCoverageGraph().getStartNode());
		allPath = findAllPathInSFlowGraphBfs(testsuite.getCoverageGraph(),testsuite.getCoverageGraph().getStartNode());
		System.out.println(allPath.size());
		/*merge two lists of paths*/
		for(int l = 0; l < distributionPath.size(); l ++){
			for(int m = 0; m < allPath.size(); m ++){
				if(pathEqual(allPath.get(m).getPath(), distributionPath.get(l).getPath())) {
					allPath.remove(m);
					m --;
				}
			}
		}
		//System.out.println(allPaths.size());
		distributionPath.addAll(allPath);
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

	private List<CoveragePath> findAllPathInSFlowGraphBfs(CoverageSFlowGraph coverageGraph, CoverageSFNode startNode) {
		List<Integer> Testcases = new ArrayList<Integer>();
		List<CoveragePath> allpath = new ArrayList<CoveragePath>();
		class BfsNode {
			public BfsNode(CoverageSFNode node) {
				this.bfsNode = node;
				this.prePath = new ArrayList<Integer>();
			}
			public BfsNode(CoverageSFNode node,List<Integer> path) {
				this.bfsNode = node;
				this.prePath = path;
			}
			 CoverageSFNode bfsNode;
			 List<Integer> prePath;
		}
		Queue<BfsNode> queue = new LinkedList<BfsNode>();
		BfsNode startnode = new BfsNode(startNode);
		queue.offer(startnode);
		BfsNode nodeProcessing;
		do {
            nodeProcessing = queue.poll();
			if(nodeProcessing.bfsNode.getBranches().size() == 0) {
				nodeProcessing.prePath.add(nodeProcessing.bfsNode.getCvgIdx());
				newPath.setPath(nodeProcessing.prePath);
				newPath.setCoveredTcs(Testcases);
				allpath.add(newPath);
			}
			else {
				for(CoverageSFNode node3 : nodeProcessing.bfsNode.getBranches()) {
					List<Integer> newPrepath = nodeProcessing.prePath;
					newPrepath.add(node3.getCvgIdx());
					BfsNode newnode = new BfsNode(node3,newPrepath);
					queue.offer(newnode);
				}
			}
		}while(!queue.isEmpty());
		return allpath;
		
	}

	private boolean pathEqual(List<Integer> path, List<Integer> path2) {
		if(path.size()!=path2.size()) {
			return false;
		}
		Integer[] pathint =  path.toArray(new Integer[path.size()]);
		Integer[] pathint2 = path2.toArray(new Integer[path2.size()]);
		int len = pathint.length;
		for(int i = 0;i < len; i++){
			if(pathint[i] != pathint2[i]) {
				return false;
			}
		}
		return true;
	}

	private void findAllPathInSFlowGraphDfs(CoverageSFlowGraph coverageGraph,CoverageSFNode nextnode ) {

		List<Integer> path = new ArrayList<Integer>();
		List<Integer> Testcases = new ArrayList<Integer>();
		CoveragePath newPath = new CoveragePath();
		stack.push(nextnode);
		if(nextnode.getBranches().size() == 0) {
			path.clear();
			for(int i = 0; i < stack.size(); i++) {
				path.add(stack.elementAt(i).getCvgIdx());
            
			}
            newPath.setPath(path);
            newPath.setCoveredTcs(Testcases);
			allPath.add(newPath);
			stack.pop();
			return;
		}
		else {
			for(CoverageSFNode node2 : nextnode.getBranches()) {
				findAllPathInSFlowGraphDfs(coverageGraph, node2);
			}
			stack.pop();
		}

	}
	
}
