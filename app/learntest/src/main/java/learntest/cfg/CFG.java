package learntest.cfg;


import learntest.cfg.CfgEdge;
import learntest.cfg.CfgEntryNode;
import learntest.cfg.CfgExitNode;
import learntest.cfg.CfgNode;
import learntest.cfg.graph.Graph;

public class CFG extends Graph<CfgNode, CfgEdge> {

	// TODO: please finish this class
	private CfgEntryNode entry;
	private CfgExitNode exit;

	public CFG(CfgEntryNode entry, CfgExitNode exit) {
		this.entry = entry;
		this.exit = exit;
	}
	
	public boolean isEmpty() {
		return size() == 0;
	}

	public CfgEntryNode getEntry() {
		return entry;
	}

	public void setEntry(CfgEntryNode entry) {
		this.entry = entry;
	}

	public CfgExitNode getExit() {
		return exit;
	}

	public void setExit(CfgExitNode exit) {
		this.exit = exit;
	}
	

	public void addNode(CfgNode node) {
		super.addVertex(node);
	}
	
	public void addEdge(CfgNode source, CfgNode dest) {
		addEdge(new CfgEdge(source, dest));
	}
}
