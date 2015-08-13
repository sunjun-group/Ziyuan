package assertion.factory;

import java.util.ArrayList;
import java.util.List;

import assertion.utility.Utility;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.stmt.AssertStmt;

public class MathAssertionFactory extends ObjectAssertionFactory {

	// sqrt
	public List<AssertStmt> createAssertion(MethodCallExpr n) {
		List<AssertStmt> al = new ArrayList<AssertStmt>();

		String methodName = n.getName();

		if (methodName.equals("sqrt"))
		{
			Expression e = n.getArgs().get(0);
			
			// e >= 0
			al.add(Utility.createGteZeroAssertion(e));
		}
		
		else
		{
			al.addAll(super.createAssertion(n));
		}

		return al;
	}

}
