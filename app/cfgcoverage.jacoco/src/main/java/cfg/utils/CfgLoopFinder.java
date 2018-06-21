package cfg.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cfg.CfgNode;
import sav.common.core.utils.NumberUtils;
import sav.common.core.utils.TextFormatUtils;

/**
 * @author LLT
 *
 */
public class CfgLoopFinder {

	/**
	 * 1. traverse and detect circles in graph.
	 * 2. for each circle, identify candidates for loop header.
	 * 3. update loop header.
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
		assignLoopHeader(loops);
		return loops;
	}
	
	
	/**
	 * A node H is a loop header if:
	 * - node H is inside a loop, means there is a path a --> b -->...> a.
	 * - there is a single edge from outside of the loop to H.
	 */
	private static void assignLoopHeader(List<Loop> loops) {
		Map<Integer, Loop> loopHeaderCandidates = new HashMap<>();
		Map<Integer, List<CfgNode>> loopPotentialHeadersMap = new HashMap<>();
		Map<CfgNode, Loop> loopHeaderMap = new HashMap<>();
		for (Loop loop : new ArrayList<>(loops)) {
			List<CfgNode> loopHeaders = new ArrayList<>();
			for (CfgNode node : loop.nodesInLoop) {
				if (canbeLoopHeader(node, loop)) {
					loopHeaders.add(node);
				}
			}
			if (loopHeaders.size() == 1) {
				loop.loopHeader = loopHeaders.get(0);
				updateLoopHeaderMap(loop, loopHeaderMap, loops);
			} else {
				updateLoopHeaderCandidates(loop, loopPotentialHeadersMap, loopHeaders, loopHeaderCandidates, loopHeaderMap, loops);
			}
		}
		
		while (!loopHeaderCandidates.isEmpty()) {
			List<Integer> keySet = new ArrayList<Integer>(loopHeaderCandidates.keySet());
			for (Integer headerKey : keySet) {
				boolean changed = false;
				Loop curLoop = loopHeaderCandidates.get(headerKey);
				if (curLoop == null) {
					continue;
				}
				List<CfgNode> headers = loopPotentialHeadersMap.get(headerKey);
				for (Iterator<CfgNode> it = headers.iterator(); it.hasNext();) {
					CfgNode header = it.next();
					Loop loop = loopHeaderMap.get(header);
					if (loop != null) {
						curLoop.nodesInLoop.addAll(loop.nodesInLoop);
						changed = true;
					}
				}
				if (changed) {
					for (Iterator<CfgNode> it = headers.iterator(); it.hasNext();) {
						if (!canbeLoopHeader(it.next(), curLoop)) {
							it.remove();
						}
					}
					loopHeaderCandidates.remove(headerKey);
					if (headers.size() == 1) {
						curLoop.loopHeader = headers.get(0);
						updateLoopHeaderMap(curLoop, loopHeaderMap, loops);
					} else {
						updateLoopHeaderCandidates(curLoop, loopPotentialHeadersMap, headers, loopHeaderCandidates, loopHeaderMap, loops);
					}
				}
			}
		}
	}
	
	private static void updateLoopHeaderCandidates(Loop loop, Map<Integer, List<CfgNode>> loopPotentialHeadersMap, List<CfgNode> headers,
			Map<Integer, Loop> loopHeaderCandidates, Map<CfgNode, Loop> loopHeaderMap, List<Loop> loops) {
		int headersKey = getKey(headers);
		Loop duplicateLoop = loopHeaderCandidates.get(headersKey);
		if (duplicateLoop != null && duplicateLoop != loop) {
			duplicateLoop.nodesInLoop.addAll(loop.nodesInLoop);
			loops.remove(loop);
			for (Iterator<CfgNode> it = headers.iterator(); it.hasNext();) {
				if (!canbeLoopHeader(it.next(), duplicateLoop)) {
					it.remove();
				}
			}
			loopHeaderCandidates.remove(headersKey);
			loopPotentialHeadersMap.remove(headersKey);
			if (headers.size() == 1) {
				duplicateLoop.loopHeader = headers.get(0);
				updateLoopHeaderMap(duplicateLoop, loopHeaderMap, loops);
			} else {
				updateLoopHeaderCandidates(duplicateLoop, loopPotentialHeadersMap, headers, loopHeaderCandidates, loopHeaderMap, loops);
			}
		} else {
			loopHeaderCandidates.put(headersKey, loop);
			loopPotentialHeadersMap.put(headersKey, headers);
		}
	}
	
	private static int getKey(List<CfgNode> nodes) {
		List<Integer> idxies = new ArrayList<>(nodes.size());
		for (CfgNode node : nodes) {
			idxies.add(node.getIdx());
		}
		Collections.sort(idxies);
		return idxies.hashCode();
	}

	private static void updateLoopHeaderMap(Loop loop, Map<CfgNode, Loop> loopHeaderMap, List<Loop> loops) {
		Loop duplicateLoop = loopHeaderMap.get(loop.loopHeader);
		if (duplicateLoop != null && duplicateLoop != loop) {
			duplicateLoop.nodesInLoop.addAll(loop.nodesInLoop);
			loops.remove(loop);
		} else {
			loopHeaderMap.put(loop.loopHeader, loop);
		}
	}

	private static boolean canbeLoopHeader(CfgNode node, Loop loop) {
		for (CfgNode precessor : node.getPredecessors()) {
			if (!loop.nodesInLoop.contains(precessor)) {
				return true;
			}
		}
		return false;
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
