package datastructure;

import java.util.List;

import japa.parser.ASTHelper;
import japa.parser.ast.Node;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.visitor.ModifierVisitorAdapter;
import sav.strategies.dto.BreakPoint.Variable;

public class AddBagCodeVisitor<A> extends ModifierVisitorAdapter<A> {
	
	private String methodName;

	private int lineNo;

	// variables at learning location {x, y, ...}
	private Variable learnVar;
	
	private int newValue;
	
	public AddBagCodeVisitor(String methodName, int lineNo,
			Variable learnVar, int newValue) {
		this.methodName = methodName;
		this.lineNo = lineNo;
		this.learnVar = learnVar;
		this.newValue = newValue;
	}
	
	@Override
	public Node visit(BlockStmt n, A arg) {
		BlockStmt block = new BlockStmt();

		List<Statement> stmts = n.getStmts();

		if (stmts != null) {
			for (int i = 0; i < stmts.size(); i++) {
				Statement stmt = (Statement) stmts.get(i);
				
				if (stmt.getBeginLine() == lineNo) {
					NameExpr clazz = new NameExpr("Reachability");
					MethodCallExpr change = new MethodCallExpr(clazz, "changeData");

					ASTHelper.addArgument(change, new NameExpr(learnVar.getFullName()));
					ASTHelper.addArgument(change, new IntegerLiteralExpr(newValue + ""));

					ASTHelper.addStmt(block, change);
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
