package datastructure;

import java.util.List;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import sav.common.core.Pair;

public class CollectVisibleVarsVisitor extends VoidVisitorAdapter<List<Pair<String,String>>> {
	
	private String className;
	
	private String methodName;
	
	private int lineNo;
	
	public CollectVisibleVarsVisitor(String className, String methodName, int lineNo) {
		this.className = className;
		this.methodName = methodName;
		this.lineNo = lineNo;
	}
	
	@Override
	public void visit(final MethodDeclaration n, final List<Pair<String,String>> arg) {
		if (n.getName().equals(methodName)) {
			for (Parameter p : n.getParameters()) {
				String type = p.getType().toString();
				String name = p.getId().getName();
				
				Pair<String,String> var = new Pair<String,String>(type, name);
				arg.add(var);
			}
			
			super.visit(n, arg);
		}
	}
	
	@Override
	public void visit(final IfStmt n, final List<Pair<String,String>> arg) {
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
	public void visit(final VariableDeclarationExpr n, final List<Pair<String,String>> arg) {
		if (n.getBeginLine() < lineNo) {
			String type = n.getType().toString();
			
			for (VariableDeclarator vd : n.getVars()) {
				String name = vd.getId().getName();
				
				Pair<String,String> var = new Pair<String,String>(type, name);
				arg.add(var);
			}
		}
	}

}
