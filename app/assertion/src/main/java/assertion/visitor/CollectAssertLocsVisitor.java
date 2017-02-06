package assertion.visitor;

import java.util.List;

import assertion.utility.Utility;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.SuperExpr;
import japa.parser.ast.expr.BinaryExpr.Operator;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.stmt.ExplicitConstructorInvocationStmt;
import japa.parser.ast.stmt.ThrowStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import sav.strategies.dto.BreakPoint;

public class CollectAssertLocsVisitor extends VoidVisitorAdapter<List<BreakPoint>> {
	
	private String className;
	
	private String methodName;
	
	private List<String> listOfMethods;
	
	private boolean isInMethod;
	
	public CollectAssertLocsVisitor(String className, String methodName,
			List<String> listOfMethods) {
		super();
		
		this.className = className;
		this.methodName = methodName;
		this.listOfMethods = listOfMethods;
	}
	
	@Override
	public void visit(final MethodDeclaration n, final List<BreakPoint> arg) {
		if (methodName != null) {
			if (methodName.indexOf('(') > 0) {
				String fullMethodName = Utility.getSigType(n);
				
				if (fullMethodName.equals(methodName)) {
					isInMethod = true;
					super.visit(n, arg);
					isInMethod = false;
				}
			} else if (n.getName().equals(methodName)) {
				isInMethod = true;
				super.visit(n, arg);
				isInMethod = false;
			};
		}
	}
	
	@Override
	public void visit(final ConstructorDeclaration n, final List<BreakPoint> arg) {
		if (methodName != null) {
			if (methodName.indexOf('(') > 0) {
				String fullMethodName = Utility.getSigType(n);
				
				if (fullMethodName.equals(methodName)) {
					isInMethod = true;
					System.out.println("here");
					super.visit(n, arg);
					isInMethod = false;
				}
			} else if (n.getName().equals(methodName)) {
				isInMethod = true;
				super.visit(n, arg);
				isInMethod = false;
			};
		}
	}
	
	@Override
	public void visit(final AssertStmt n, final List<BreakPoint> arg) {
		if (isInMethod) {
			BreakPoint bkp = new BreakPoint(className, methodName, n.getBeginLine());
			arg.add(bkp);
		}
	}
	
	
	@Override
	public void visit(final ExplicitConstructorInvocationStmt n, final List<BreakPoint> arg) {
		if (isInMethod) {
			BreakPoint bkp = new BreakPoint(className, methodName, n.getBeginLine());
			arg.add(bkp);
		}
	}
	
	@Override
	public void visit(final ThrowStmt n, final List<BreakPoint> arg) {
		if (isInMethod) {
			BreakPoint bkp = new BreakPoint(className, methodName, n.getBeginLine());
			arg.add(bkp);
		}
	}
	
	@Override
	public void visit(final MethodCallExpr n, final List<BreakPoint> arg) {
		if (isInMethod) {
			// if (contains(n.getName())) {
				BreakPoint bkp = new BreakPoint(className, methodName, n.getBeginLine());
				arg.add(bkp);
			// }
		}
	}
	
//	@Override
//	public void visit(final ArrayAccessExpr n, final List<BreakPoint> arg) {
//		if (isInMethod) {
//			// if (contains(n.getName())) {
//				BreakPoint bkp = new BreakPoint(className, methodName, n.getBeginLine());
//				arg.add(bkp);
//			// }
//		}
//	}
//	
//	@Override
//	public void visit(final FieldAccessExpr n, final List<BreakPoint> arg) {
//		if (isInMethod) {
//			// if (contains(n.getName())) {
//				BreakPoint bkp = new BreakPoint(className, methodName, n.getBeginLine());
//				arg.add(bkp);
//			// }
//		}
//	}
//	
//	@Override
//	public void visit(final BinaryExpr n, final List<BreakPoint> arg) {
//		if (isInMethod && (n.getOperator() == Operator.divide || n.getOperator() == Operator.remainder)) {
//			// if (contains(n.getName())) {
//				BreakPoint bkp = new BreakPoint(className, methodName, n.getBeginLine());
//				arg.add(bkp);
//			// }
//		} else {
//			super.visit(n, arg);
//		}
//	}
	
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
