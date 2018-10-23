package learntest.activelearning.core.distribution;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.activelearning.core.data.MethodInfo;
import learntest.activelearning.core.data.UnitTestSuite;
import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.settings.LearntestSettings;
import microbat.instrumentation.cfgcoverage.graph.CoveragePath;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFNode;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFlowGraph;
import sav.common.core.SavRtException;
import sav.strategies.dto.AppJavaClassPath;


public class RandomTestDistributionRunner {
	private Logger log = LoggerFactory.getLogger(RandomTestDistributionRunner.class);
	private List<CoveragePath> allPath = new ArrayList<CoveragePath>();
	public void run(AppJavaClassPath appClasspath, MethodInfo targetMethod, LearntestSettings settings) throws Exception {
		log.info("Run method: " + targetMethod.toString());
		Tester tester = new Tester(settings, false, appClasspath);
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

		allPath = findAllPathInSFlowGraphBfs(testsuite.getCoverageGraph(),testsuite.getCoverageGraph().getStartNode());
		/*merge two lists of paths*/
		for(int l = 0; l < distributionPath.size(); l ++){
			for(int m = 0; m < allPath.size(); m ++){
				if(pathEqual(allPath.get(m).getIdPath(), distributionPath.get(l).getIdPath())) {
					allPath.remove(m);
					m --;
				}
			}
		}
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
       
		DistributionExcelWriter writer = new DistributionExcelWriter(new File("E:/distribution.xlsx"));
		DistributionRow trial = new DistributionRow();
		trial.setMethodName(targetMethod.getMethodFullName());
		trial.setDistribution(distribution);
		writer.addRowData(trial);

	}

	private List<CoveragePath> findAllPathInSFlowGraphBfs(CoverageSFlowGraph coverageGraph, CoverageSFNode startNode) {
		if(startNode == null) return null;
		List<Integer> Testcases = new ArrayList<Integer>(1);
		List<CoveragePath> allpath = new ArrayList<CoveragePath>();
		CoveragePath newpath = new CoveragePath();
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
		BfsNode nodeProcessing = new BfsNode(null);
		BfsNode newnode = new BfsNode(null);
		List<Integer> tPath = new ArrayList<Integer>();
		do {
            nodeProcessing = queue.poll();
            nodeProcessing.prePath.add(new Integer(nodeProcessing.bfsNode.getCvgIdx()));

			if((nodeProcessing.bfsNode.getBranches().isEmpty())||nodeProcessing.bfsNode.getBranches() == null) {
				tPath = listDeepCopy(nodeProcessing.prePath);
                newpath = new CoveragePath();
                List<CoverageSFNode> nodePath = new ArrayList<>(tPath.size());
                for (int nodeId : tPath) {
                	nodePath.add(coverageGraph.getNodeList().get(nodeId));
                }
				newpath.setPath(nodePath);
				newpath.setCoveredTcs(Testcases);
				allpath.add(newpath);
			}
			else {
				for(int j = 0; j < nodeProcessing.bfsNode.getBranches().size(); j++ ) {
					tPath = listDeepCopy(nodeProcessing.prePath);
					newnode = new BfsNode(nodeProcessing.bfsNode.getBranches().get(j),tPath);
					queue.offer(newnode);
				}
			}
		}while(!queue.isEmpty());
		return allpath;
	
	}

	private List<Integer> listDeepCopy(List<Integer> prePath) {
		List<Integer> toList = new ArrayList<Integer>();
		for(int i = 0; i < prePath.size(); i++) {
			toList.add(new Integer(prePath.get(i).intValue()));
		}
		return toList;
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


	
}
