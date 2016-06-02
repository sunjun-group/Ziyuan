package learntest.cfg.traveller;

import java.util.ArrayList;
import java.util.List;

import learntest.cfg.CFG;
import learntest.cfg.CfgDecisionNode;
import learntest.cfg.CfgEdge;
import learntest.cfg.CfgNode;

public class CfgTraveller {
	private CFG cfg;
	
	public CfgTraveller(CFG cfg) {
		this.cfg = cfg;
	}
	
	public List<Integer> getTopologicalOrder() {
		List<Integer> res = new ArrayList<Integer>();
		List<CfgNode> vertices = cfg.getVertices();
		for (CfgNode node : vertices) {
			List<CfgEdge> outEdges = cfg.getOutEdges(node);
			for (CfgEdge edge : outEdges) {
				CfgNode dest = edge.getDest();
				if (dest instanceof CfgDecisionNode) {
					((CfgDecisionNode) dest).incIndegree();
				}
			}
		}
		return res;
	}

}
