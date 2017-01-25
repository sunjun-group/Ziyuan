package datastructure;

import java.util.List;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import sav.strategies.dto.BreakPoint;

public class CollectLearnLocsVisitor extends VoidVisitorAdapter<List<BreakPoint>> {
	
	private String className;
	
	private String methodName;
	
	public CollectLearnLocsVisitor(String className, String methodName) {
		this.className = className;
		this.methodName = methodName;
	}
	
	@Override
	public void visit(final MethodDeclaration n, final List<BreakPoint> arg) {
		if (n.getName().equals(methodName)) {
			int line = n.getBeginLine() + 1;
			BreakPoint bkp = new BreakPoint(className, methodName, line);
			
			arg.add(bkp);
			
			super.visit(n, arg);
		}
	}
	
	@Override
	public void visit(final AssertStmt n, final List<BreakPoint> arg) {
		// skip this one because may be there are function calls inside assertion
	}
	
	@Override
	public void visit(final MethodCallExpr n, final List<BreakPoint> arg) {
		int line = n.getBeginLine() + 1;
		BreakPoint bkp = new BreakPoint(className, methodName, line);
		
		arg.add(bkp);
	}

}
