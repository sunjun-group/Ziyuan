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
	

	public void setBeginLine(int beginLine){
		this.beginLine = beginLine;
	}
	
	public void setStartLine(int startLine) {
		this.beginLine = startLine;
	}

	@Override
	public String toString() {
		return "entry";
	}

	@Override
	public String getStmtType() {
		return null;
	}

	@Override
	public int getTrueBeginLine() {
		return 0;
	}

	@Override
	public boolean isRelevant() {
		return true;
	}

}
