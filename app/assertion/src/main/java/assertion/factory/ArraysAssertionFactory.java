package assertion.factory;

import java.util.ArrayList;
import java.util.List;

import assertion.utility.Utility;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.stmt.AssertStmt;
import mutation.mutator.VariableSubstitution;
import sav.common.core.utils.CollectionUtils;

public class ArraysAssertionFactory extends ObjectAssertionFactory {
	
	public ArraysAssertionFactory(VariableSubstitution subst) {
		super(subst);
	}
	
	public List<AssertStmt> createAssertion(MethodCallExpr n) {
		List<AssertStmt> al = new ArrayList<AssertStmt>();
		
		String methodName = n.getName();
		
		if ((methodName.equals("sort") && (n.getArgs().size() == 3 || n.getArgs().size() == 4))
				|| (methodName.equals("parallelSort") && (n.getArgs().size() == 3 || n.getArgs().size() == 4))
				|| (methodName.equals("parallelPrefix") && n.getArgs().size() == 4)
				|| (methodName.equals("binarySearch") && (n.getArgs().size() == 4 || n.getArgs().size() == 5))
				|| (methodName.equals("fill") && n.getArgs().size() == 4)
				|| (methodName.equals("spliterator") && n.getArgs().size() == 3)
				|| (methodName.equals("stream") && n.getArgs().size() == 3))
		{
			Expression arr = n.getArgs().get(0);
			Expression fromIndex = n.getArgs().get(1);
			Expression toIndex = n.getArgs().get(2);
			
			Expression length = new FieldAccessExpr(arr, "length");
			
			// arr != null
			CollectionUtils.addIfNotNull(al, Utility.createNeqNullAssertion(arr));
			// toIndex >= fromIndex
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(toIndex, fromIndex, BinaryExpr.Operator.greaterEquals));
			// fromIndex >= 0
			CollectionUtils.addIfNotNull(al, Utility.createGteZeroAssertion(fromIndex));
			// toIndex <= length
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(toIndex, length, BinaryExpr.Operator.lessEquals));
		}
		
		else if (methodName.equals("parallelPrefix") && n.getArgs().size() == 2)
		{
			Expression arr = n.getArgs().get(0);
		
			// arr != null
			CollectionUtils.addIfNotNull(al, Utility.createNeqNullAssertion(arr));
		}
		
		else if (methodName.equals("copyOf"))
		{
			Expression arr = n.getArgs().get(0);
			Expression newLength = n.getArgs().get(1);
			
			// arr != null
			CollectionUtils.addIfNotNull(al, Utility.createNeqNullAssertion(arr));
			// newLength >= 0
			CollectionUtils.addIfNotNull(al, Utility.createGteZeroAssertion(newLength));
		}
		
		else if (methodName.equals("copyOfRange"))
		{
			Expression arr = n.getArgs().get(0);
			Expression fromIndex = n.getArgs().get(1);
			Expression toIndex = n.getArgs().get(2);
			
			Expression length = new FieldAccessExpr(arr, "length");
			
			// arr != null
			CollectionUtils.addIfNotNull(al, Utility.createNeqNullAssertion(arr));
			// toIndex >= fromIndex
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(toIndex, fromIndex, BinaryExpr.Operator.greaterEquals));
			// fromIndex >= 0
			CollectionUtils.addIfNotNull(al, Utility.createGteZeroAssertion(fromIndex));
			// fromIndex <= length
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(fromIndex, length, BinaryExpr.Operator.lessEquals));
		}
		
		else if (methodName.equals("setAll") || methodName.equals("parallelSetAll"))
		{
			Expression generator = n.getArgs().get(1);
			
			// generator != null
			CollectionUtils.addIfNotNull(al, Utility.createNeqNullAssertion(generator));
		}
		
		else
		{
			al.addAll(super.createAssertion(n));
		}
		
		return al;
	}

}
