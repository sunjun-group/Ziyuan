package assertion.visitor;

import java.util.List;

import assertion.creator.AssertionCreator;
import assertion.utility.Utility;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.Node;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import mutation.mutator.VariableSubstitution;
import mutation.mutator.insertdebugline.AddedLineData;
import mutation.mutator.insertdebugline.DebugLineData;

public class AddAssertStmtVisitor extends VoidVisitorAdapter<List<DebugLineData>> {
	
	private AssertionCreator creator;
	
	private String methodName;
	
	private boolean isInMethod;
	
	public AddAssertStmtVisitor(VariableSubstitution subst) {
		super();
		this.creator = new AssertionCreator(subst);
	}
	
	public AddAssertStmtVisitor(List<ImportDeclaration> imports, VariableSubstitution subst) {
		super();
		this.creator = new AssertionCreator(imports, subst);
	}
	
	public AddAssertStmtVisitor(List<ImportDeclaration> imports, VariableSubstitution subst, String methodName) {
		super();
		this.creator = new AssertionCreator(imports, subst);
		this.methodName = methodName;
	}
	
	@Override
	public void visit(final ConstructorDeclaration n, final List<DebugLineData> arg) {
		// method name is not null means we only want to add assertion for that specific method
		if (methodName != null) {
			String currMethodName = "";
			if (methodName.indexOf('(') > 0) {
				currMethodName = Utility.getSigType(n);
			} else {
				currMethodName = n.getName();
			}
				
			if (currMethodName.equals(methodName)) {
				isInMethod = true;
				super.visit(n, arg);
				isInMethod = false;
			}
		} else {
			isInMethod = true;
			super.visit(n, arg);
			isInMethod = false;
		}
	}
	
	@Override
	public void visit(final MethodDeclaration n, final List<DebugLineData> arg) {
		// method name is not null means we only want to add assertion for that specific method
		if (methodName != null) {
			String currMethodName = "";
			if (methodName.indexOf('(') > 0) {
				currMethodName = Utility.getSigType(n);
			} else {
				currMethodName = n.getName();
			}
				
			if (currMethodName.equals(methodName)) {
				isInMethod = true;
				super.visit(n, arg);
				isInMethod = false;
			}
		} else {
			isInMethod = true;
			super.visit(n, arg);
			isInMethod = false;
		}
	}
	
	@Override
	public void visit(final MethodCallExpr n, final List<DebugLineData> arg) {
		if (isInMethod) {
			List<AssertStmt> al = creator.createAssertionForMethodCallExpr(n);
			addAssertStmt(al, n, arg);
			
			super.visit(n, arg);
		}
	}
	
	@Override
	public void visit(final BinaryExpr n, final List<DebugLineData> arg) {
		if (isInMethod && !hasShortCircuit(n)) {
			List<AssertStmt> al = creator.createAssertionForBinaryExpr(n);
			addAssertStmt(al, n, arg);
			
			super.visit(n, arg);
		}
	}
	
	public void visit(final FieldAccessExpr n, final List<DebugLineData> arg) {
		if (isInMethod) {
			List<AssertStmt> al = creator.createAssertionForFieldAccessExpr(n);
			addAssertStmt(al, n, arg);
			
			super.visit(n, arg);
		}
	}
	
	@Override
	public void visit(final ArrayAccessExpr n, final List<DebugLineData> arg) {
		if (isInMethod) {
			List<AssertStmt> al = creator.createAssertionForArrayAccessExpr(n);
			addAssertStmt(al, n, arg);
			
			super.visit(n, arg);
		}
	}
	
	private boolean hasShortCircuit(Expression n) {
		if (n instanceof BinaryExpr) {
			BinaryExpr bn = (BinaryExpr) n;
			if (bn.getOperator() == BinaryExpr.Operator.and ||
					bn.getOperator() == BinaryExpr.Operator.or) {
				return true;
			} else {
				return hasShortCircuit(bn.getLeft()) || hasShortCircuit(bn.getRight());
			}
		} else {
			return false;
		}
	}
	
	private boolean hasInnerMethod(Expression n) {
		if (n instanceof MethodCallExpr) {
			return true;
		} else if (n instanceof BinaryExpr) {
			BinaryExpr bn = (BinaryExpr) n;
			return hasInnerMethod(bn.getLeft()) || hasInnerMethod(bn.getRight());
		} else if (n instanceof UnaryExpr) {
			UnaryExpr un = (UnaryExpr) n;
			return hasInnerMethod(un.getExpr());
		} else if (n instanceof ArrayAccessExpr) {
			ArrayAccessExpr an = (ArrayAccessExpr) n;
			return hasInnerMethod(an.getIndex());
		} else {
			return false;
		}
	}
	
	private void addAssertStmt(List<AssertStmt> al, Node n, List<DebugLineData> arg) {
		if (al != null) {
			for (AssertStmt a : al) {
				if (!hasInnerMethod(a.getCheck())) {
					a.setBeginLine(n.getBeginLine());
					AddedLineData d = new AddedLineData(n.getBeginLine(), a);
					arg.add(d);
				}
			}
		}
	}
	
}
