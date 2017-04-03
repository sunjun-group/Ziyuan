package common.cfg;

import japa.parser.ast.Node;

public interface CfgNode {
	
	public Type getType();
	
	public Node getAstNode();
	
	public int getBeginLine();
	
	public int getTrueBeginLine();
	
	public String getStmtType();
	
	public boolean isRelevant();

	public static enum Type {
		ENTRY,
		EXIT,
		DECISIONS
	}

}
