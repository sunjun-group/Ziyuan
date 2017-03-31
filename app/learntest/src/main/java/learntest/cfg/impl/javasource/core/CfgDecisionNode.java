package learntest.cfg.impl.javasource.core;

import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.Node;
import libsvm.core.CategoryCalculator;
import libsvm.core.Divider;
import sav.common.core.formula.Formula;

public class CfgDecisionNode implements CfgNode {
	
	private Node astNode;
	private int beginLine ;
	private int trueBeginLine;
	private int parentBeginLine = -1;
	private String stmtType;
	private boolean loop;
	
	private List<List<CategoryCalculator>> preconditions = new ArrayList<List<CategoryCalculator>>();
	private List<Divider> dividers;
	private Formula trueFalse;
	private Formula oneMore;
	private boolean relevant = true;
	
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
		return  AstUtils.toString(getAstNode()) + "?  [preconditions: " + preconditions.size() + "]";
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

	public List<List<CategoryCalculator>> getPreconditions() {
		return preconditions;
	}

	public void addPrecondition(List<CategoryCalculator> precondition) {
		this.preconditions.add(precondition);
	}

	public List<Divider> getDividers() {
		return dividers;
	}

	public void setDividers(List<Divider> dividers) {
		this.dividers = dividers;
	}

	public Formula getTrueFalse() {
		return trueFalse;
	}

	public void setTrueFalse(Formula trueFalse) {
		this.trueFalse = trueFalse;
	}

	public Formula getOneMore() {
		return oneMore;
	}

	public void setOneMore(Formula oneMore) {
		this.oneMore = oneMore;
	}

	public void setRelevant(boolean relevant) {
		this.relevant = relevant;
	}
	
	@Override
	public boolean isRelevant() {
		return relevant;
	}
	
}
