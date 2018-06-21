package cfg.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cfg.CFG;
import cfg.CfgNode;
import sav.common.core.utils.NumberUtils;
import sav.common.core.utils.TextFormatUtils;

/**
 * @author LLT
 *
 */
public class CfgLoopFinder {

	public static void assignLoopHeader(CFG cfg) {
		List<Loop> loops = CfgLoopFinder.findLoops(cfg.getStartNode(), 0, cfg.size());
		for (Loop loop : loops) {
			CfgNode node = loop.loopHeader;
			node.setLoopHeader(true);
			while (!node.isDecisionNode()) {
				node = node.getNext();
			}
			/* make first decision node be the loopHeader */
			node.setLoopHeader(true);
			for (CfgNode inLoopNode : loop.nodesInLoop) {
				inLoopNode.addLoopHeader(node);
			}
		}
	}
	
	/**
	 * traverse the graph, if there is any back-edge, it forms a circle and
	 * the target of a back-edge is a loop header.
	 * */
	public static List<Loop> findLoops(CfgNode entryNode, int minIdx, int maxIdx) {
		int[] visited = new int[maxIdx - minIdx + 1];
		for (int i = 0; i < visited.length; i++) {
			visited[i] = -1;
		}
		List<CfgNode> stack = new ArrayList<CfgNode>();
		stack.add(entryNode);
		List<Loop> loops = new ArrayList<>();
		while (!stack.isEmpty()) {
			CfgNode curNode = stack.get(stack.size() - 1);
			if (!NumberUtils.isInRange(curNode.getIdx(), minIdx, maxIdx)) {
				stack.remove(stack.size() - 1);
				continue;
			}
			int visitedIdx = curNode.getIdx() - minIdx;
			int stackPos = visited[visitedIdx];
			if (stackPos == stack.size() - 1) {
				stack.remove(stack.size() - 1);
				visited[visitedIdx] = -1;
				continue;
			}
			/* visited --> record loop */
			if (stackPos >= 0) {
				Loop loop = new Loop();
				loop.loopHeader = curNode;
				for (CfgNode node : stack.subList(stackPos, stack.size() - 1)) {
					if (NumberUtils.isInRange(node.getIdx(), minIdx, maxIdx) && visited[node.getIdx() - minIdx] >= 0) {
						loop.nodesInLoop.add(node);
					}
				}
				loops.add(loop);
				stack.remove(stack.size() - 1);
				continue;
			}
			/* not visited --> visit */
			visited[visitedIdx] = stack.size() - 1;
			for (CfgNode branch : curNode.getBranches()) {
				stack.add(branch);
			}
		}
		return loops;
	}
	
	public static class Loop {
		CfgNode loopHeader;
		Set<CfgNode> nodesInLoop = new HashSet<>();
		
		@Override
		public String toString() {
			List<CfgNode> nodes = new ArrayList<>(nodesInLoop);
			Collections.sort(nodes, new Comparator<CfgNode>() {

				@Override
				public int compare(CfgNode o1, CfgNode o2) {
					return Integer.compare(o1.getIdx(), o2.getIdx());
				}
			});
			return "Loop [loopHeader=" + loopHeader + ", nodesInLoop=" + TextFormatUtils.printCol(nodes,  "\n") + "]";
		}
	}
}
