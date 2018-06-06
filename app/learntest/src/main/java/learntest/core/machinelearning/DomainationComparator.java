package learntest.core.machinelearning;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cfg.CfgNode;

/** 
* @author ZhangHr 
*/
public class DomainationComparator implements Comparator<CfgNode>{
	HashMap<CfgNode, CfgNodeDomainInfo> dominationMap;
	public DomainationComparator(HashMap<CfgNode, CfgNodeDomainInfo> dominationMap) {
		this.dominationMap = dominationMap;
	}

	@Override
	public int compare(CfgNode o1, CfgNode o2) {
		HashMap<CfgNode, Boolean> visited = new HashMap<>();
		Queue<CfgNode> queue = new LinkedList<>();
		queue.add(o1);
		while (!queue.isEmpty()) {
			CfgNode node = queue.poll();
			if (visited.containsKey(node)) {
				
			}else {
				visited.put(node, true);
				List<CfgNode> dominatees = dominationMap.get(node).getDominatees();
				if (dominatees.contains(o2)) {
					return -1;
				}else {
					queue.addAll(dominatees);
				}
			}			
		}
		visited.clear();
		queue.add(o1);
		while (!queue.isEmpty()) {
			CfgNode node = queue.poll();
			if (visited.containsKey(node)) {
				
			}else {
				visited.put(node, true);
				List<CfgNode> dominatees = dominationMap.get(node).getDominatees();
				if (dominatees.contains(o1)) {
					return 1;
				}else {
					queue.addAll(dominatees);
				}
			}			
		}
		return 0;
	}
	
}

