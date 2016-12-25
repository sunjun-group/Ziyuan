package assertion.factory;

import java.util.ArrayList;
import java.util.List;

import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.stmt.AssertStmt;
import mutation.mutator.VariableSubstitution;

public class ObjectAssertionFactory {
	
	protected VariableSubstitution subst;
	
	public ObjectAssertionFactory() {
		
	}
	
	public ObjectAssertionFactory(VariableSubstitution subst) {
		this.subst = subst;
	}

	public List<AssertStmt> createAssertion(MethodCallExpr n) {
		return new ArrayList<AssertStmt>();
	}
	
}
