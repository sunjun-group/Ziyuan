package learntest.activelearning.core.coverage;

import microbat.instrumentation.cfgcoverage.graph.CoverageSFlowGraph;
import sav.common.core.utils.CollectionUtils;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFNode;


public class CoverageUtils {

	public static double getBranchCoverage(CoverageSFlowGraph coverageSFlowGraph, String methodId) {
	  int allBranches = 0;
	  int coveredBranches = 0;
	  System.out.println();
	  for(CoverageSFNode node : coverageSFlowGraph.getNodeList()) {
			if (!node.isConditionalNode()) {
				continue;
			}
			for (CoverageSFNode branchNode : node.getBranches()) {
				allBranches++;
				if (!CollectionUtils.isEmpty(node.getCoveredTestcasesOnBranches().get(branchNode))) {
					coveredBranches++;
				}
			}
	  }
	  if (allBranches == 0) {
		  return 1;
	  }
	  double coverage = coveredBranches / (double)allBranches;
	  return coverage;

	}
}
