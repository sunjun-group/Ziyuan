package assertion.factory;

import java.util.ArrayList;
import java.util.List;

import assertion.utility.Utility;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.Type;
import mutation.mutator.VariableSubstitution;
import sav.common.core.utils.CollectionUtils;

public class ListAssertionFactory extends ObjectAssertionFactory {

	public ListAssertionFactory(VariableSubstitution subst) {
		super(subst);
	}
	
	// get, set, listIterator, subList
	// add, addAll
	public List<AssertStmt> createAssertion(MethodCallExpr n) {
		List<AssertStmt> al = new ArrayList<AssertStmt>();
		
		String methodName = n.getName();
		
		if (methodName.equals("get") || methodName.equals("set"))
				// || (methodName.equals("remove") && (n.getArgs().get(0) instanceof IntegerLiteralExpr)))
		{
			Expression index = n.getArgs().get(0);
			Expression size = new MethodCallExpr(n.getScope(), "size");
			
			// index >= 0
			CollectionUtils.addIfNotNull(al, Utility.createGteZeroAssertion(index));
			// index < size()
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(index, size, BinaryExpr.Operator.less));
		}
		
		else if (methodName.equals("listIterator"))
		{
			Expression index = n.getArgs().get(0);
			Expression size = new MethodCallExpr(n.getScope(), "size");
			
			// index >= 0
			CollectionUtils.addIfNotNull(al, Utility.createGteZeroAssertion(index));
			// index <= size()
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(index, size, BinaryExpr.Operator.lessEquals));
		}
		
		else if (methodName.equals("subList"))
		{
			Expression fromIndex = n.getArgs().get(0);
			Expression toIndex = n.getArgs().get(1);
			Expression size = new MethodCallExpr(n.getScope(), "size");
			
			// from >= 0
			CollectionUtils.addIfNotNull(al, Utility.createGteZeroAssertion(fromIndex));
			// to <= size
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(toIndex, size, BinaryExpr.Operator.lessEquals));
			// from <= to
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(fromIndex, toIndex, BinaryExpr.Operator.lessEquals));
		}
		
		else if (methodName.equals("add") && n.getArgs().size() == 2)
		{
			Expression index = n.getArgs().get(0);
			Expression size = new MethodCallExpr(n.getScope(), "size");
			
			// index >= 0
			CollectionUtils.addIfNotNull(al, Utility.createGteZeroAssertion(index));
			// index <= size()
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(index, size, BinaryExpr.Operator.lessEquals));
		}
		
		else if (methodName.equals("addAll") && n.getArgs().size() == 1)
		{
			Expression coll = n.getArgs().get(0);
			
			// coll != null
			CollectionUtils.addIfNotNull(al, Utility.createNeqNullAssertion(coll));
		}
		
		else if (methodName.equals("addAll") && n.getArgs().size() == 2)
		{
			Expression index = n.getArgs().get(0);
			Expression coll = n.getArgs().get(1);
			Expression size = new MethodCallExpr(n.getScope(), "size");
			
			// index >= 0
			CollectionUtils.addIfNotNull(al, Utility.createGteZeroAssertion(index));
			// index <= size()
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(index, size, BinaryExpr.Operator.lessEquals));
			// coll != null
			CollectionUtils.addIfNotNull(al, Utility.createNeqNullAssertion(coll));
		}
		
		else
		{
			al.addAll(super.createAssertion(n));
		}
		
		return al;
	}
	
}
