package assertion.factory;

import java.util.ArrayList;
import java.util.List;

import assertion.utility.Utility;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.stmt.AssertStmt;
import mutation.mutator.VariableSubstitution;
import sav.common.core.utils.CollectionUtils;

public class HashMapAssertionFactory extends ObjectAssertionFactory {

	public HashMapAssertionFactory(VariableSubstitution subst) {
		super(subst);
	}
	
	public List<AssertStmt> createAssertion(MethodCallExpr n) {
		List<AssertStmt> al = new ArrayList<AssertStmt>();

		String methodName = n.getName();

		if (methodName.equals("putAll"))
		{
			Expression map = n.getArgs().get(0);
			
			// map != null
			CollectionUtils.addIfNotNull(al, Utility.createNeqNullAssertion(map));
		}
		
		else
		{
			al.addAll(super.createAssertion(n));
		}

		return al;
	}
}
