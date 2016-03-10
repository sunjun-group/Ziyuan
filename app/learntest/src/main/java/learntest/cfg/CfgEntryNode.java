package learntest.cfg;

import japa.parser.ast.Node;

public class CfgEntryNode implements CfgNode {
	
	private int beginLine;

	@Override
	public Type getType() {
		return Type.ENTRY;
	}

	@Override
	public Node getAstNode() {
		return null;
	}

	@Override
	public int getBeginLine() {
		return beginLine;
	}
	
	public void setStartLine(int startLine) {
		this.beginLine = startLine;
	}

	@Override
	public String toString() {
		return "entry";
	}

}
