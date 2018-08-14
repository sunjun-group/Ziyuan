package learntest.activelearning.core.coverage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cfg.utils.OpcodeUtils;
import microbat.codeanalysis.bytecode.CFGNode;
import microbat.instrumentation.cfgcoverage.graph.Branch;
import microbat.instrumentation.cfgcoverage.graph.CFGInstance;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFNode;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFlowGraph;
import sav.common.core.utils.CollectionUtils;


public class CoverageUtils {

	public static double getBranchCoverage(CoverageSFlowGraph coverageSFlowGraph, String methodId) {
	  int allBranches = 0;
	  int coveredBranches = 0;
	  for(CoverageSFNode node : coverageSFlowGraph.getDecisionNodes()) {
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
	
	public static Set<Branch> getCoveredBranches(CoverageSFlowGraph coverageSFlowGraph, String methodId) {
		Set<Branch> branches = new HashSet<>();
		for (CoverageSFNode node : coverageSFlowGraph.getDecisionNodes()) {
			for (CoverageSFNode branchNode : node.getBranches()) {
				if (!CollectionUtils.isEmpty(node.getCoveredTestcasesOnBranches().get(branchNode))) {
					branches.add(new Branch(node.getCvgIdx(), branchNode.getCvgIdx()));
				}
			}
		}
		return branches;
	}

	public static Set<Branch> getAllBranches(CoverageSFlowGraph coverageSFlowGraph) {
		Set<Branch> branches = new HashSet<>();
		for (CoverageSFNode node : coverageSFlowGraph.getDecisionNodes()) {
			for (CoverageSFNode branchNode : node.getBranches()) {
				branches.add(new Branch(node.getCvgIdx(), branchNode.getCvgIdx()));
			}
		}
		return branches;
	}
	
	public static List<String> getBranchCoverageDisplayTexts(CoverageSFlowGraph coverageSFlowGraph, CFGInstance cfg) {
		List<String> lines = new ArrayList<>(coverageSFlowGraph.getDecisionNodes().size());
		for (CoverageSFNode node : coverageSFlowGraph.getDecisionNodes()) {
			StringBuilder sb = new StringBuilder();
			sb.append("NodeCoverage [id=").append(node.getId()).append(", ");
			sb.append(toString(cfg.getNodeList().get(node.getStartIdx()), null));
			sb.append(", covered=").append(node.isCovered())
				.append(", coveredBranches=").append(toBranchString(node.getCoveredBranches(), cfg))
			.append("]");
			lines.add(sb.toString());
		}
		return lines;
	}
	
	public static String toBranchString(List<CoverageSFNode> vals, CFGInstance cfg) {
		if (vals == null || vals.isEmpty()) {
			return "{}";
		}
		StringBuilder sb = new StringBuilder("{");
		int i = 0;
		for (CoverageSFNode val : vals) {
			i++;
			if (val != null) {
				CFGNode cfgNode = cfg.getNodeList().get(val.getStartIdx());
				sb.append(cfgNode.getIdx());
				if (i != vals.size()) {
					sb.append(",");
				}
			}
		}
		sb.append("}");
		return sb.toString();
	}
	
	public static String toString(CFGNode node, String classSimpleName) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("node[%d,%s,line %d]", node.getIdx(),
				OpcodeUtils.getCode(node.getInstructionHandle().getInstruction().getOpcode()), 
				node.getLineNo()));
		if (node.isConditional()) {
			sb.append(", decis={");
			for (int i = 0; i < node.getChildren().size();) {
				CFGNode child = node.getChildren().get(i++);
				sb.append(String.format("node[%d,%s,line %d]", child.getIdx(),
						OpcodeUtils.getCode(child.getInstructionHandle().getInstruction().getOpcode()),
						child.getLineNo()));
				if (i < node.getChildren().size()) {
					sb.append(",");
				}
			}
			sb.append("}");
		}
		return sb.toString();
	}
}
