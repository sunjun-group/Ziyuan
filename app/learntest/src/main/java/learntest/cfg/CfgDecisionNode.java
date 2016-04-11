package learntest.cfg;

import japa.parser.ast.Node;

public class CfgDecisionNode implements CfgNode {
	
	private Node astNode;
	private int trueBeginLine;
	private int parentBeginLine = -1;
	private String stmtType;
	private boolean loop;
	
	public CfgDecisionNode(Node astNode) {
		this.astNode = astNode;
	}

	public CfgDecisionNode(Node astNode, String stmtType , boolean loop) {
		this.astNode = astNode;
		this.stmtType = stmtType;
		this.loop = loop;
		//TODO please determine whether this node is a loop node
	}
	
	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	public boolean getLoop() {
		return loop;
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
	
	public int getParentBeginLine() {
		return parentBeginLine;
	}

	public void setParentBeginLine(int parentBeginLine) {
		this.parentBeginLine = parentBeginLine;
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

	public boolean isLoop() {
		return loop;
	}
	
}
