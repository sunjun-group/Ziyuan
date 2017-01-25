package datastructure;

import java.util.List;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.stmt.ExplicitConstructorInvocationStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.type.Type;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.BreakPoint;

public class CollectConstsVisitor extends VoidVisitorAdapter<List<Integer>> {
	
	private String className;
	
	private String methodName;
	
	public CollectConstsVisitor(String className, String methodName) {
		this.className = className;
		this.methodName = methodName;
	}
	
	@Override
	public void visit(final AssertStmt n, final List<Integer> arg) {
		return;
	}
	
	@Override 
	public void visit(final ExplicitConstructorInvocationStmt n, final List<Integer> arg) {
		return;
	}

	@Override public void visit(final ExpressionStmt n, final List<Integer> arg) {
		return;
	}
	
	@Override
	public void visit(final IntegerLiteralExpr n, final List<Integer> arg) {
		int i1 = Integer.parseInt(n.getValue());
		int i2 = -i1;
		
		CollectionUtils.addIfNotNullNotExist(arg, new Integer(i1));
		CollectionUtils.addIfNotNullNotExist(arg, new Integer(i2));
	}

}
