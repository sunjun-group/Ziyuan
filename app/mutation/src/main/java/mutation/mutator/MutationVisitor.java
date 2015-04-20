package mutation.mutator;

import japa.parser.ast.Node;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.AssignExpr.Operator;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.CastExpr;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BreakStmt;
import japa.parser.ast.stmt.ContinueStmt;
import japa.parser.ast.visitor.CloneVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.ClassLocation;

/**
 * Created by hoangtung on 4/3/15.
 */
public class MutationVisitor extends AbstractMutationVisitor {
	private List<Integer> lineNumbers;
	private Map<Integer, List<MutationNode>> result;
	private MutationMap mutationMap;
	private CloneVisitor nodeCloner;
	
	/**
	 * locations must be in the same class
	 * */
	public void reset(List<ClassLocation> locations) {
		lineNumbers.clear();
		for (ClassLocation location : locations) {
			lineNumbers.add(location.getLineNo());
		}
		result.clear();
	}

	public MutationVisitor(MutationMap mutationMap) {
		lineNumbers = new ArrayList<Integer>();
		result = new HashMap<Integer, List<MutationNode>>();
		setMutationMap(mutationMap);
	}

	@Override
	protected boolean allowToVisit(Node node) {
		return true;
	}
	
	@Override
	public boolean mutate(BreakStmt n) {
		getMutationNodeByLine(n.getBeginLine())
			.add(MutationNode.of(n, new ContinueStmt(n.getId())));
		return false;
	}
	
	@Override
	public boolean mutate(ContinueStmt n) {
		getMutationNodeByLine(n.getBeginLine())
			.add(MutationNode.of(n, new BreakStmt(n.getId())));
		return false;
	}
	
	private List<MutationNode> getMutationNodeByLine(int line) {
		return CollectionUtils.getListInitIfEmpty(result, line);
	}
	
	@Override
	public boolean mutate(AssignExpr n) {
		MutationNode muNode = new MutationNode(n);
		getMutationNodeByLine(n.getBeginLine()).add(muNode);
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
		MutationNode muNode = new MutationNode(n);
		getMutationNodeByLine(n.getBeginLine()).add(muNode);
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
	public boolean mutate(CastExpr n) {
		return super.mutate(n);
	}
	
	@Override
	public boolean mutate(FieldAccessExpr n) {
		// TODO Auto-generated method stub
		return super.mutate(n);
	}
	
	@Override
	public boolean mutate(MethodCallExpr n) {
		// TODO Auto-generated method stub
		return super.mutate(n);
	}
	
	@Override
	public boolean mutate(NameExpr n) {
		// TODO Auto-generated method stub
		return super.mutate(n);
	}
	
	@Override
	public boolean mutate(UnaryExpr n) {
		// TODO Auto-generated method stub
		return super.mutate(n);
	}
	
	@Override
	public boolean mutate(VariableDeclarationExpr n) {
		// TODO Auto-generated method stub
		return super.mutate(n);
	}
	
	public void setMutationMap(MutationMap mutationMap) {
		this.mutationMap = mutationMap;
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
			mutatedNodes = new ArrayList<>();
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
