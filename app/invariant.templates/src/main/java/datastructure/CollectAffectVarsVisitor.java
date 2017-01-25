package datastructure;

import java.util.List;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import sav.common.core.utils.CollectionUtils;

public class CollectAffectVarsVisitor extends VoidVisitorAdapter<List<String>> {
	
	private String className;
	
	private String methodName;
	
	private int lineNo;
	
	public CollectAffectVarsVisitor(String className, String methodName, int lineNo) {
		this.className = className;
		this.methodName = methodName;
		this.lineNo = lineNo;
	}
	
	@Override
	public void visit(final MethodDeclaration n, final List<String> arg) {
		if (n.getName().equals(methodName)) {
			super.visit(n, arg);
		}
	}
	
	@Override
	public void visit(final IfStmt n, final List<String> arg) {
		int begin = n.getBeginLine();
		int end = n.getEndLine();
		
		if (begin >= lineNo)
			super.visit(n, arg);
		else if (end >= lineNo) {
			int thenBegin = n.getThenStmt().getBeginLine();
			int thenEnd = n.getThenStmt().getEndLine();
			
			int elseBegin = n.getElseStmt().getBeginLine();
			int elseEnd = n.getElseStmt().getEndLine();
			
			if (thenBegin <= lineNo && lineNo <= thenEnd)
				n.getThenStmt().accept(this, arg);
			if (elseBegin <= lineNo && lineNo <= elseEnd)
				n.getElseStmt().accept(this, arg);
		}
	}
	
	@Override
	public void visit(final NameExpr n, final List<String> arg) {
		if (n.getBeginLine() >= lineNo)
			CollectionUtils.addIfNotNullNotExist(arg, n.getName());
	}

}
