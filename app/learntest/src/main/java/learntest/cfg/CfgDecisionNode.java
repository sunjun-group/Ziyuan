package learntest.cfg;

import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.Node;
import sav.common.core.formula.Formula;

public class CfgDecisionNode implements CfgNode {
	
	private Node astNode;
	private int beginLine ;
	private int trueBeginLine;
	private int parentBeginLine = -1;
	private String stmtType;
	private boolean loop;
	
	private List<Formula> preconditions;
	private Formula condition;
	
	public CfgDecisionNode(Node astNode) {
		this.astNode = astNode;
		beginLine = astNode.getBeginLine();
	}

	public CfgDecisionNode(Node astNode, String stmtType , boolean loop) {
		this.astNode = astNode;
		this.stmtType = stmtType;
		this.loop = loop;
		beginLine = astNode.getBeginLine();
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
		return beginLine;
	}
	
	public void setBeginLine(int beginLine) {
		this.beginLine = beginLine;
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

	public List<Formula> getPreconditions() {
		return preconditions;
	}

	public void addPrecondition(Formula precondition) {
		if (this.preconditions == null) {
			this.preconditions = new ArrayList<Formula>();
		}
		this.preconditions.add(precondition);
	}

	public Formula getCondition() {
		return condition;
	}

	public void setCondition(Formula condition) {
		this.condition = condition;
	}
	
}
