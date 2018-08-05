package learntest.activelearning.core.distribution;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import learntest.core.commons.data.classinfo.MethodInfo;
import microbat.instrumentation.cfgcoverage.graph.CoveragePath;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFNode;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFlowGraph;
import sav.common.core.utils.FileUtils;

public class CoverageDistributionRecorder {
	private List<CoveragePath> allPath = new ArrayList<CoveragePath>();

	public void record(String prefix, String outputFolder,MethodInfo targetMethod, CoverageSFlowGraph coverageGraph) throws Exception  {
		String distributionFile = FileUtils.getFilePath(outputFolder, prefix + "_distribution.xlsx");
		List<CoveragePath> distributionPath = coverageGraph.getCoveragePaths();

		allPath = findAllPathInSFlowGraphBfs(coverageGraph,coverageGraph.getStartNode());
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
       
		DistributionExcelWriter writer = new DistributionExcelWriter(new File( distributionFile));
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
			toList.add(prePath.get(i));
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
