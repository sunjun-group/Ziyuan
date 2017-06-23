package assertion.factory;

import java.util.ArrayList;
import java.util.List;

import assertion.utility.Utility;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.stmt.AssertStmt;
import mutation.mutator.VariableSubstitution;
import sav.common.core.utils.CollectionUtils;

public class StackAssertionFactory extends VectorAssertionFactory {

	public StackAssertionFactory(VariableSubstitution subst) {
		super(subst);
	}
	
	// peek, pop
	public List<AssertStmt> createAssertion(MethodCallExpr n) {
		List<AssertStmt> al = new ArrayList<AssertStmt>();
		
		String methodName = n.getName();
		
		if (methodName.equals("peek") || methodName.equals("pop"))
		{
			Expression size = new MethodCallExpr(n.getScope(), "size");
			
			// size > 0
			CollectionUtils.addIfNotNull(al, Utility.createGtZeroAssertion(size));
		}
		
		else
		{
			al.addAll(super.createAssertion(n));
		}
		
		return al;
	}
	
}
