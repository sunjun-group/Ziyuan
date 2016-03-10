package learntest.cfg;

import japa.parser.ast.Node;

public class CfgDecisionNode implements CfgNode {
	
	private Node astNode;
	private int trueBeginLine;
	
	public CfgDecisionNode(Node astNode) {
		this.astNode = astNode;
	}

	@Override
	public Type getType() {
		return Type.DECISIONS;
	}

	@Override
	public Node getAstNode() {
		return astNode;
	}

	@Override
	public int getBeginLine() {
		return astNode.getBeginLine();
	}

	@Override
	public String toString() {
		return getBeginLine() + "\t" + AstUtils.toString(getAstNode()) + "? : " + getTrueBeginLine();
	}

	public int getTrueBeginLine() {
		return trueBeginLine;
	}

	public void setTrueBeginLine(int trueBeginLine) {
		this.trueBeginLine = trueBeginLine;
	}
	
}
