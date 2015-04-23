package mutation.mutator;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.AssignExpr.Operator;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.WhileStmt;
import japa.parser.ast.visitor.CloneVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mutation.parser.ClassAnalyzer;
import mutation.parser.ClassDescriptor;
import mutation.parser.VariableDescriptor;
import sav.common.core.utils.CollectionUtils;

/**
 * Created by hoangtung on 4/3/15.
 */
public class MutationVisitor extends AbstractMutationVisitor {
	private List<Integer> lineNumbers;
	private Map<Integer, List<MutationNode>> result;
	private MutationMap mutationMap;
	private CloneVisitor nodeCloner;
	private ClassAnalyzer clasAnalyzer;
	private ClassDescriptor classDescriptor;
	
	public void reset(ClassDescriptor classDescriptor, List<Integer> lineNos) {
		this.lineNumbers = lineNos;
		this.classDescriptor = classDescriptor;
		result.clear();
	}

	public MutationVisitor(MutationMap mutationMap, ClassAnalyzer classAnalyzer) {
		lineNumbers = new ArrayList<Integer>();
		result = new HashMap<Integer, List<MutationNode>>();
		setMutationMap(mutationMap);
		setClasAnalyzer(classAnalyzer);
	}
	
	public void run(CompilationUnit cu) {
		cu.accept(this, true);
		
	}
	
	@Override
	protected boolean beforeMutate(Node node) {
		return lineNumbers.contains(node.getBeginLine());
	}
	
	@Override
	public void visit(ForStmt n, Boolean arg) {
		if (beforeVisit(n)) {
			n.getBody().accept(this, arg);
		}
	}
	
	@Override
	public void visit(WhileStmt n, Boolean arg) {
		if (beforeVisit(n)) {
			n.getBody().accept(this, arg);
		}
	}

	@Override
	protected boolean beforeVisit(Node node) {
		for (Integer lineNo : lineNumbers) {
			if (lineNo >= node.getBeginLine() && lineNo <= node.getEndLine()) {
				return true;
			}
		}
		return false;
	}
	
	private MutationNode newNode(Node node) {
		MutationNode muNode = new MutationNode(node);
		CollectionUtils.getListInitIfEmpty(result, node.getBeginLine())
				.add(muNode);
		return muNode;
	}
	
	@Override
	public boolean mutate(AssignExpr n) {
		MutationNode muNode = newNode(n);
		// change the operator
		List<Operator> muOps = mutationMap.getMutationOp(n.getOperator());
		if (!muOps.isEmpty()) {
			for (Operator muOp : muOps) {
				AssignExpr newNode = (AssignExpr) nodeCloner.visit(n, null); 
				newNode.setOperator(muOp);
				muNode.getMutatedNodes().add(newNode);
			}
		}
		return true;
	}
	
	@Override
	public boolean mutate(BinaryExpr n) {
		MutationNode muNode = newNode(n);
		// change the operator
		List<BinaryExpr.Operator> muOps = mutationMap.getMutationOp(n.getOperator());
		if (!muOps.isEmpty()) {
			for (BinaryExpr.Operator muOp : muOps) {
				BinaryExpr newNode = (BinaryExpr) nodeCloner.visit(n, null); 
				newNode.setOperator(muOp);
				muNode.getMutatedNodes().add(newNode);
			}
		}
		return true;
	}
	
	@Override
	public boolean mutate(NameExpr n) {
		MutationNode muNode = newNode(n);
		VariableSubstitution varSubstituion = new VariableSubstitutionImpl(n.getName(), n.getEndLine(), n.getEndColumn(), classDescriptor);
		List<VariableDescriptor> candidates = varSubstituion.find();
		for (VariableDescriptor var : candidates) {
			NameExpr expr = new NameExpr(var.getName());
			muNode.getMutatedNodes().add(expr);
		}
		return false;
	}
	
	public void setMutationMap(MutationMap mutationMap) {
		this.mutationMap = mutationMap;
	}
	
	public void setClasAnalyzer(ClassAnalyzer clasAnalyzer) {
		this.clasAnalyzer = clasAnalyzer;
	}
	
	public CloneVisitor getNodeCloner() {
		return nodeCloner;
	}
	
	public Map<Integer, List<MutationNode>> getResult() {
		return result;
	}
	
	public static class MutationNode {
		private Node orgNode;
		private List<Node> mutatedNodes;
		
		public MutationNode(Node orgNode) {
			mutatedNodes = new ArrayList<Node>();
		}
		
		public static MutationNode of(Node orgNode, Node newNode) {
			MutationNode node = new MutationNode(orgNode);
			node.mutatedNodes.add(newNode);
			return node;
		}

		public Node getOrgNode() {
			return orgNode;
		}

		public List<Node> getMutatedNodes() {
			return mutatedNodes;
		}
	}
}
