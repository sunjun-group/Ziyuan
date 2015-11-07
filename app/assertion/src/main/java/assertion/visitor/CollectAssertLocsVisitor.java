package assertion.visitor;

import java.util.List;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import sav.strategies.dto.BreakPoint;

public class CollectAssertLocsVisitor extends VoidVisitorAdapter<List<BreakPoint>> {
	
	private String className;
	
	private String methodName;
	
	private List<String> listOfMethods;
	
	public CollectAssertLocsVisitor(String className, String methodName,
			List<String> listOfMethods) {
		super();
		
		this.className = className;
		this.methodName = methodName;
		this.listOfMethods = listOfMethods;
	}
	
	@Override
	public void visit(final MethodDeclaration n, final List<BreakPoint> arg) {
		if (n.getName().equals(methodName)) {
			super.visit(n, arg);
		}
	}
	
	@Override
	public void visit(final AssertStmt n, final List<BreakPoint> arg) {
		BreakPoint bkp = new BreakPoint(className, methodName, n.getBeginLine());
		arg.add(bkp);
	}
	
	@Override
	public void visit(final MethodCallExpr n, final List<BreakPoint> arg) {
		if (contains(n.getName())) {
			BreakPoint bkp = new BreakPoint(className, methodName, n.getBeginLine());
			arg.add(bkp);
		}
	}
	
	private boolean contains(String name) {
		if (listOfMethods == null) return false;
		
		for (String method : listOfMethods) {
			if (method.equals(name)) {
				return true;
			}
		}
		
		return false;
	}
	
}
