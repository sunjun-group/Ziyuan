package assertion.factory;

import java.util.ArrayList;
import java.util.List;

import assertion.utility.Utility;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.stmt.AssertStmt;
import mutation.mutator.VariableSubstitution;
import sav.common.core.utils.CollectionUtils;

public class ScannerAssertionFactory extends VectorAssertionFactory {

	public ScannerAssertionFactory(VariableSubstitution subst) {
		super(subst);
	}
	
	public List<AssertStmt> createAssertion(MethodCallExpr n) {
		List<AssertStmt> al = new ArrayList<AssertStmt>();
			
		String methodName = n.getName();
			
		if (methodName.equals("useRadix"))
		{
			Expression radix = n.getArgs().get(0);
			Expression min = new IntegerLiteralExpr(Character.MIN_RADIX + "");
			Expression max = new IntegerLiteralExpr(Character.MAX_RADIX + "");
				
			// radix >= min
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(radix, min, BinaryExpr.Operator.greaterEquals));
			// radix <= max
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(radix, max, BinaryExpr.Operator.lessEquals));
		}
			
		else
		{
			al.addAll(super.createAssertion(n));
		}
			
		return al;
	}

}
