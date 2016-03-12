package assertion.factory;

import java.util.ArrayList;
import java.util.List;

import assertion.utility.Utility;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.stmt.AssertStmt;
import mutation.mutator.VariableSubstitution;
import sav.common.core.utils.CollectionUtils;

public class ArrayListAssertionFactory extends ListAssertionFactory {
	
	public ArrayListAssertionFactory(VariableSubstitution subst) {
		super(subst);
	}
	
	// removeRange
	public List<AssertStmt> createAssertion(MethodCallExpr n) {
		List<AssertStmt> al = new ArrayList<AssertStmt>();
		
		String methodName = n.getName();
		
		if (methodName.equals("removeRange"))
		{
			Expression fromIndex = n.getArgs().get(0);
			Expression toIndex = n.getArgs().get(1);
			Expression size = new MethodCallExpr(n.getScope(), "size");
			
			// from >= 0
			CollectionUtils.addIfNotNull(al, Utility.createGteZeroAssertion(fromIndex));
			// from < size
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(fromIndex, size, BinaryExpr.Operator.less));
			// to <= size
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(toIndex, size, BinaryExpr.Operator.lessEquals));
			// from <= to
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(fromIndex, toIndex, BinaryExpr.Operator.lessEquals));
		}
		
		else
		{
			al.addAll(super.createAssertion(n));
		}
		
		return al;
	}

}
