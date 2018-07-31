package learntest.activelearning.core.coverage;

import microbat.instrumentation.cfgcoverage.graph.CoverageSFlowGraph;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFNode;


public class CoverageUtils {

	public static double getBranchCoverage(CoverageSFlowGraph coverageSFlowGraph, String methodId) {
	  double allBranches = 0.00001;
	  int coveredBranches = 0;
	  for(CoverageSFNode node : coverageSFlowGraph.getNodeList()) {
		  if(node.getBranches().isEmpty()) continue;
		  for(CoverageSFNode branchNode : node.getBranches()) {
			  allBranches ++;
			  if(!(branchNode.getCoveredTestcases().isEmpty())) coveredBranches ++;
		  }
	  }
	  double coverage = coveredBranches / (double)allBranches;
	  return coverage;

	}
}
