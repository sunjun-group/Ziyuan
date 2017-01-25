package datastructure;

import java.util.List;

import japa.parser.ASTHelper;
import japa.parser.ast.Node;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.AssignExpr.Operator;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.visitor.ModifierVisitorAdapter;
import sav.strategies.dto.BreakPoint.Variable;

public class AddOthersCodeVisitor<A> extends ModifierVisitorAdapter<A> {
	
	private String methodName;

	private int lineNo;

	private List<Variable> pairVars;
	
	public AddOthersCodeVisitor(String methodName, int lineNo,
			List<Variable> pairVars) {
		this.methodName = methodName;
		this.lineNo = lineNo;
		this.pairVars = pairVars;
	}
	
	@Override
	public Node visit(BlockStmt n, A arg) {
		BlockStmt block = new BlockStmt();

		List<Statement> stmts = n.getStmts();

		if (stmts != null) {
			for (int i = 0; i < stmts.size(); i++) {
				Statement stmt = (Statement) stmts.get(i);
				
				if (stmt.getBeginLine() == lineNo) {
					if (pairVars.get(0).getType().equals(pairVars.get(1).getType())) {
						NameExpr lhs = new NameExpr(pairVars.get(0).getFullName());
						NameExpr rhs = new NameExpr(pairVars.get(1).getFullName());
						
						Expression assi = new AssignExpr(lhs, rhs, Operator.assign);
						
						ASTHelper.addStmt(block, assi);
					}
				}

				ASTHelper.addStmt(block, stmt);
				stmts.set(i, (Statement) stmt.accept(this, arg));
			}

			removeNulls(stmts);
		}

		return block;
	}
	
	private void removeNulls(final List<?> list) {
		for (int i = list.size() - 1; i >= 0; i--) {
			if (list.get(i) == null) {
				list.remove(i);
			}
		}
	}

}
