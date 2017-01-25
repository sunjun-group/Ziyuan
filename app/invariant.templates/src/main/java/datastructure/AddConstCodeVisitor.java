package datastructure;

import java.util.List;

import japa.parser.ASTHelper;
import japa.parser.ast.Node;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.AssignExpr.Operator;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.visitor.ModifierVisitorAdapter;
import sav.strategies.dto.BreakPoint.Variable;

public class AddConstCodeVisitor<A> extends ModifierVisitorAdapter<A> {
	
	private int lineNo;
	
	private Variable learnVar;
	
	private int c;
	
	public AddConstCodeVisitor(String methodName, int lineNo, Variable learnVar, int c) {
		this.lineNo = lineNo;
		this.learnVar = learnVar;
		this.c = c;
	}
	
	@Override
	public Node visit(BlockStmt n, A arg) {
		BlockStmt block = new BlockStmt();

		List<Statement> stmts = n.getStmts();

		if (stmts != null) {
			for (int i = 0; i < stmts.size(); i++) {
				Statement stmt = (Statement) stmts.get(i);
				
				if (stmt.getBeginLine() == lineNo) {
					if (learnVar.getType().equals("int")) {
						Expression lhs = new NameExpr(learnVar.getFullName());
						Expression rhs = new IntegerLiteralExpr(c + "");
						
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
