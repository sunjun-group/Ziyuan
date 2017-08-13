package learntest.core.machinelearning;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cfgcoverage.jacoco.analysis.data.CfgNode;

/** 
* @author ZhangHr 
*/
public class CfgNodeDomainInfo {
		CfgNode node;
		HashMap<CfgNode, Integer> postDomain = new HashMap<>();
		HashMap<CfgNode, Integer> children = new HashMap<>();
		
		List<CfgNode> dominatees = new LinkedList<>();
		List<CfgNode> dominators = new LinkedList<>();

		public CfgNodeDomainInfo(CfgNode node) {
			this.node = node;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(node+"\n");
			sb.append("dominatees : ");
			for (CfgNode cfgNode : dominatees) {
				sb.append(cfgNode+",");
			}
			sb.append("\ndominators : ");
			for (CfgNode cfgNode : dominators) {
				sb.append(cfgNode+",");
			}			
			sb.append("\npostDomain : ");
			for (CfgNode cfgNode : postDomain.keySet()) {
				sb.append(cfgNode+",");
			}			
			sb.append("\nchildren : ");
			for (CfgNode cfgNode : children.keySet()) {
				sb.append(cfgNode+",");
			}			
			return sb.toString();
		}
		
		/**
		 * return false if dominators contain node, otherwise true
		 * @param node
		 * @return
		 */
		public boolean addDominator(CfgNode node){
			if (!dominators.contains(node)) {
				dominators.add(node);
				return true;
			}
			return false;
		}

		/**
		 * return false if dominatees contain node, otherwise true
		 * @param node
		 * @return
		 */
		public boolean addDominatee(CfgNode node){
			if (!dominatees.contains(node)) {
				dominatees.add(node);
				return true;
			}
			return false;
		}

		public List<CfgNode> getDominatees() {
			return dominatees;
		}
		
		
}
