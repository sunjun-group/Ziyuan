package learntest.cfg;

import japa.parser.ast.Node;

public class CfgDecisionNode implements CfgNode {
	
	private Node astNode;
	private int trueBeginLine;
	private String stmtType;
	
	public CfgDecisionNode(Node astNode) {
		this.astNode = astNode;
	}

	public CfgDecisionNode(Node astNode, String stmtType) {
		this.astNode = astNode;
		this.stmtType = stmtType;
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

//	@Override
//	public String toString() {
//		return getBeginLine() + "\t" + AstUtils.toString(getAstNode()) + "? : " + getTrueBeginLine();
//	}
	
	@Override
	public String toString() {
		if(!getStmtType().equals("switch default")){
			//System.out.println(getStmtType());
		return  AstUtils.toString(getAstNode()) + "? ";
		}
		else{
			//System.out.println(getStmtType());
			return  "default";
		}
	}

	public int getTrueBeginLine() {
		return trueBeginLine;
	}

	public void setTrueBeginLine(int trueBeginLine) {
		this.trueBeginLine = trueBeginLine;
	}
	
	
	public String getStmtType(){
		
		return stmtType;
		
	}
	
}
