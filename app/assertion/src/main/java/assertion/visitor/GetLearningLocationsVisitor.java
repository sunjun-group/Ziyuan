package assertion.visitor;

import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;

public class GetLearningLocationsVisitor extends VoidVisitorAdapter<List<BreakPoint>> {

	private String className;
	
	public GetLearningLocationsVisitor(String className) {
		this.className = className;
	}
	
	@Override
	public void visit(final MethodDeclaration n, final List<BreakPoint> arg) {
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
		};
		
		super.visit(n, arg);
	}
	
}
