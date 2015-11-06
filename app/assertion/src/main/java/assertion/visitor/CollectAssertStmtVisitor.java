package assertion.visitor;

import java.util.List;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import sav.strategies.dto.BreakPoint;

public class CollectAssertStmtVisitor extends VoidVisitorAdapter<List<BreakPoint>> {
	
	private String className;
	
	private String methodName;
	
	public CollectAssertStmtVisitor(String className, String methodName) {
		super();
		
		this.className = className;
		this.methodName = methodName;
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
	
}
