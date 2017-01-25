package datastructure;

import java.util.List;

import japa.parser.ASTHelper;
import japa.parser.ast.Node;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.AssignExpr.Operator;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.visitor.ModifierVisitorAdapter;
import sav.strategies.dto.BreakPoint.Variable;

public class AddIncCodeVisitor<A> extends ModifierVisitorAdapter<A> {
	
	private int lineNo;
	
	private Variable fstVar;
	
	private Variable sndVar;
	
	private int off;
	
	public AddIncCodeVisitor(String methodName, int lineNo, Variable fstVar,
			Variable sndVar, int off) {
		this.lineNo = lineNo;
		this.fstVar = fstVar;
		this.sndVar = sndVar;
		this.off = off;
	}
	
	@Override
	public Node visit(BlockStmt n, A arg) {
		BlockStmt block = new BlockStmt();

		List<Statement> stmts = n.getStmts();

		if (stmts != null) {
			for (int i = 0; i < stmts.size(); i++) {
				Statement stmt = (Statement) stmts.get(i);
				
				if (stmt.getBeginLine() == lineNo) {
					if (fstVar.getType().equals("int")) {
						Expression lhs = new NameExpr(fstVar.getFullName());
//						Expression rhs = new BinaryExpr(new NameExpr(sndVar.getFullName()),
//								new IntegerLiteralExpr(off + ""), BinaryExpr.Operator.plus);
						Expression rhs = new IntegerLiteralExpr(off + "");
						
						Expression assi = new AssignExpr(lhs, rhs, AssignExpr.Operator.plus);
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
