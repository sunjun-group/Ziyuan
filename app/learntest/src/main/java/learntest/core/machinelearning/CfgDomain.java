package learntest.core.machinelearning;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.Queue;
import cfgcoverage.jacoco.analysis.data.CfgNode;

/**
 * @author ZhangHr
 */
public class CfgDomain {
	HashMap<CfgNode, CfgNodeDomainInfo> dominationMap = new HashMap<>();

	public HashMap<CfgNode, CfgNodeDomainInfo> constructDominationMap(CfgNode startNode) {
		/** get post domain relationship */
		initDominationMap(dominationMap, startNode);
		while (travelAndChange(dominationMap)) {
			;
		}
		
		/** get control dependency */
		HashMap<CfgNode, Integer> visited = new HashMap<>();
		Queue<CfgNode> queue = new LinkedList<>();
		queue.add(startNode);
		while (queue.size() > 0){
			CfgNode node = queue.poll();
			visited.put(node, node.getLine());
			CfgNodeDomainInfo domainInfo = dominationMap.get(node);
			HashMap<CfgNode, Integer> postD = domainInfo.postDomain;
			List<CfgNode> children = getChildDecision(node);
			for (CfgNode child : children) {
				if (!postD.containsKey(child)) { // child is reachable from node, and child is not post-dominator of node 
					domainInfo.addDominatee(child);
					dominationMap.get(child).addDominator(node);
				}else{ // child is post-dominator of node , the dominators of node will also dominate child
					for (CfgNode dominator : domainInfo.dominators) {
						dominationMap.get(dominator).addDominatee(child);
						dominationMap.get(child).addDominator(dominator);
					}
				}
				if (!visited.containsKey(child)) {
					queue.add(child);
				}
			}
		}
		for (CfgNodeDomainInfo info : dominationMap.values()) {
			System.out.println(info+"\n");
		}
		return dominationMap;
	}

	private boolean travelAndChange(HashMap<CfgNode, CfgNodeDomainInfo> dominationMap) {
		boolean modified = false;
		Set<Entry<CfgNode, CfgNodeDomainInfo>> set = dominationMap.entrySet();
		for (Entry<CfgNode, CfgNodeDomainInfo> entry : set) {
			CfgNodeDomainInfo curNodeInfo = dominationMap.get(entry.getKey());
			HashMap<CfgNode, Integer> post = new HashMap<>();
			if (curNodeInfo.children.size() == 1) {
				CfgNode child = curNodeInfo.children.keySet().iterator().next();
				post = dominationMap.get(child).postDomain;
				post.put(child, child.getLine());
			}else if (curNodeInfo.children.size() > 1) {
				post = getCommonPostD(curNodeInfo.children);
			}
			int originalSize = curNodeInfo.postDomain.size();
			curNodeInfo.postDomain.putAll(post);
			int curSize = curNodeInfo.postDomain.size();
			if (curSize > originalSize) {
				modified = true;
			}
		}
		return modified;
	}

	private HashMap<CfgNode, Integer> getCommonPostD(HashMap<CfgNode, Integer> parent) {
		CfgNode[] nodes = parent.keySet().toArray(new CfgNode[]{});
		HashMap<CfgNode, Integer> commons = new HashMap<>();
		commons.putAll(dominationMap.get(nodes[0]).postDomain);
		int i =1;
		while (i<nodes.length && commons.size() > 0){
			HashMap<CfgNode, Integer> list = dominationMap.get(nodes[i]).postDomain;
			HashMap<CfgNode, Integer> temp = new HashMap<>(list.size());
			for (CfgNode cfgNode : list.keySet()) {
				if (commons.containsKey(cfgNode)) {
					temp.put(cfgNode, cfgNode.getLine());
				}
			}
			i++;
			commons.clear();
			commons = temp;
		}
		return commons;
	}

	private void initDominationMap(HashMap<CfgNode, CfgNodeDomainInfo> dominationMap, CfgNode startNode) {
		Stack<CfgNode> stack = new Stack<>();
		/** establish direct relationship */
		stack.push(startNode);
		if (!dominationMap.containsKey(startNode)) {
			dominationMap.put(startNode, new CfgNodeDomainInfo(startNode));
		}
		while (!stack.isEmpty()) {
			CfgNode curNode = stack.pop();
			dominationMap.get(curNode).postDomain.put(curNode, curNode.getLine());
			List<CfgNode> children = curNode.getBranches();
			if (children!=null && children.size() > 0) {
				for (CfgNode node : children) {
					if (!dominationMap.containsKey(node)) {
						dominationMap.put(node, new CfgNodeDomainInfo(node));
						stack.push(node);
					}
					dominationMap.get(curNode).children.put(node, node.getLine());
				}
			}
		}
	}
	

	private List<CfgNode> getChildDecision(CfgNode node) {
		List<CfgNode> childDecisonNodes = new LinkedList<>();
		List<CfgNode> children = node.getBranches();
		for (CfgNode child : children) {
			getChildDecision(child, childDecisonNodes);
		}
		return childDecisonNodes;
	}

	private void getChildDecision(CfgNode node, List<CfgNode> list) {
		List<CfgNode> children = node.getBranches();
		if (null == children || children.size() == 0) {
			;
		} else if (children.size() == 1) {
			getChildDecision(children.get(0), list);
		} else if (children.size() >= 2) { /** branch node */
			list.add(node);
		}

	}


}
