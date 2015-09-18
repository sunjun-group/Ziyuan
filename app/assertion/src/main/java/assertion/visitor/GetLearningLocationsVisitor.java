package assertion.visitor;

import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.stmt.WhileStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;

public class GetLearningLocationsVisitor extends VoidVisitorAdapter<List<BreakPoint>> {

	private String className;
	
	private String methodName;
	
	public GetLearningLocationsVisitor(String className, String methodName) {
		this.className = className;
		this.methodName = methodName;
	}
	
	@Override
	public void visit(final MethodDeclaration n, final List<BreakPoint> arg) {
		if (n.getName().equals(methodName)) {
			BlockStmt body = n.getBody();
			List<Statement> stmts = body.getStmts();
			
			if (stmts != null) {
				BreakPoint bkp1 = new BreakPoint(className, methodName, stmts.get(0).getBeginLine());
				arg.add(bkp1);
				
				super.visit(n, arg);
				
				int size = stmts.size();
				if (size > 1) {
					BreakPoint bkp2 = new BreakPoint(className, methodName, stmts.get(size - 1).getBeginLine());
					arg.add(bkp2);
				}
			}
		}
		
		/*
		BlockStmt body = n.getBody();
		List<Variable> parameters = new ArrayList<Variable>();
		
		if (n.getParameters() != null) {
			for (Parameter p : n.getParameters()) {
				parameters.add(new Variable(p.getId().getName()));
			}
		};
		
		if (body.getStmts() != null) {
			Statement stmt = body.getStmts().get(0);
			BreakPoint bkp = new BreakPoint(className, stmt.getBeginLine());
			bkp.setVars(parameters);
			arg.add(bkp);
			
			if (body.getStmts().size() > 1) {
				stmt = body.getStmts().get(body.getStmts().size() - 1);
				bkp = new BreakPoint(className, stmt.getBeginLine());
				bkp.setVars(parameters);
				arg.add(bkp);
			}
		};
		
		super.visit(n, arg);
		*/
	}
	
	@Override
	public void visit(final WhileStmt n, final List<BreakPoint> arg) {
		BreakPoint bkp1 = new BreakPoint(className, methodName, n.getBeginLine());
		bkp1.setVars(new ArrayList<Variable>());
		arg.add(bkp1);
		
		super.visit(n, arg);
		
		Statement stmt = n.getBody();
		if (stmt instanceof BlockStmt) {
			BlockStmt bs = (BlockStmt) stmt;
			List<Statement> stmts = bs.getStmts();
			
			if (stmts != null) {
				int size = stmts.size();
				BreakPoint bkp2 = new BreakPoint(className, methodName, stmts.get(size - 1).getBeginLine());
				arg.add(bkp2);
			}
		} else {
			BreakPoint bkp2 = new BreakPoint(className, methodName, stmt.getBeginLine());
			arg.add(bkp2);
		}
	}
	
}
