package datastructure;

import java.util.List;

import japa.parser.ASTHelper;
import japa.parser.ast.Node;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.AssignExpr.Operator;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.visitor.ModifierVisitorAdapter;
import sav.strategies.dto.BreakPoint.Variable;

public class AddSwapCodeVisitor<A> extends ModifierVisitorAdapter<A> {
	
	private String methodName;

	private int lineNo;

	private List<Variable> swapVars;
	
	public AddSwapCodeVisitor(String methodName, int lineNo,
			List<Variable> swapVars) {
		this.methodName = methodName;
		this.lineNo = lineNo;
		this.swapVars = swapVars;
	}
	
	@Override
	public Node visit(BlockStmt n, A arg) {
		BlockStmt block = new BlockStmt();

		List<Statement> stmts = n.getStmts();

		if (stmts != null) {
			for (int i = 0; i < stmts.size(); i++) {
				Statement stmt = (Statement) stmts.get(i);
				
				if (stmt.getBeginLine() == lineNo) {
					if (swapVars.get(0).getType().equals(swapVars.get(1).getType())) {
						if (swapVars.get(0).getType().equals("int")) {
							NameExpr var1 = new NameExpr(swapVars.get(0).getFullName());
							NameExpr var2 = new NameExpr(swapVars.get(1).getFullName());
							
							Expression fstAssi = new AssignExpr(var2, var1, Operator.assign);
							NameExpr clazz = new NameExpr("Utility");
							MethodCallExpr swap = new MethodCallExpr(clazz, "swap");
							
							ASTHelper.addArgument(swap, var2);
							ASTHelper.addArgument(swap, fstAssi);
							
							Expression sndAssi = new AssignExpr(var1, swap, Operator.assign);
							
							ASTHelper.addStmt(block, sndAssi);
						} else {
							NameExpr clazz = new NameExpr("Utility");
							MethodCallExpr swap = new MethodCallExpr(clazz, "swap");
		
							NameExpr obj1 = new NameExpr(swapVars.get(0).getFullName());
							NameExpr obj2 = new NameExpr(swapVars.get(1).getFullName());
							
							ASTHelper.addArgument(swap, obj1);
							ASTHelper.addArgument(swap, obj2);
							
							ASTHelper.addStmt(block, swap);
						}
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
