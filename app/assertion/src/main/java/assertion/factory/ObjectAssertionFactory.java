package assertion.factory;

import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.stmt.AssertStmt;

public class ObjectAssertionFactory {

	public List<AssertStmt> createAssertion(MethodCallExpr n) {
		return new ArrayList<AssertStmt>();
	}
	
}
