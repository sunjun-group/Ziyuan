package assertion.factory;

import java.util.ArrayList;
import java.util.List;

import assertion.utility.Utility;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.stmt.AssertStmt;

public class VectorAssertionFactory extends ListAssertionFactory {

	// containsAll, firstElement, lastElement, setSize
	// elementAt, removeElementAt, insertElementAt, setElementAt,
	// indexOf, lastIndexOf, toArray 
	public List<AssertStmt> createAssertion(MethodCallExpr n) {
		List<AssertStmt> al = new ArrayList<AssertStmt>();
		
		String methodName = n.getName();
		
		if (methodName.equals("containsAll"))
		{
			Expression coll = n.getArgs().get(0);
			
			// coll != null
			al.add(Utility.createNeqNullAssertion(coll));
		}
		
		else if (methodName.equals("firstElement") || methodName.equals("lastElement"))
		{
			Expression size = new MethodCallExpr(n.getScope(), "size");
			
			// size > 0
			al.add(Utility.createGtZeroAssertion(size));
		}
		
		else if (methodName.equals("setSize"))
		{
			Expression size = n.getArgs().get(0);
			
			// size >= 0
			al.add(Utility.createGteZeroAssertion(size));
		}
		
		else if (methodName.equals("elementAt") || methodName.equals("removeElementAt"))
		{
			Expression index = n.getArgs().get(0);
			Expression size = new MethodCallExpr(n.getScope(), "size");
			
			// index >= 0
			al.add(Utility.createGteZeroAssertion(index));
			// index < size
			al.add(Utility.createAssertion(index, size, BinaryExpr.Operator.less));
		}
		
		else if (methodName.equals("insertElementAt"))
		{
			Expression index = n.getArgs().get(1);
			Expression size = new MethodCallExpr(n.getScope(), "size");
			
			// index >= 0
			al.add(Utility.createGteZeroAssertion(index));
			// index <= size
			al.add(Utility.createAssertion(index, size, BinaryExpr.Operator.lessEquals));
		}
		
		else if (methodName.equals("setElementAt"))
		{
			Expression index = n.getArgs().get(1);
			Expression size = new MethodCallExpr(n.getScope(), "size");
			
			// index >= 0
			al.add(Utility.createGteZeroAssertion(index));
			// index < size
			al.add(Utility.createAssertion(index, size, BinaryExpr.Operator.less));
		}
		
		else if (methodName.equals("indexOf") && n.getArgs().size() == 2)
		{
			Expression index = n.getArgs().get(1);
			
			// index >= 0
			al.add(Utility.createGteZeroAssertion(index));
		}
		
		else if (methodName.equals("lastIndexOf") && n.getArgs().size() == 2)
		{
			Expression index = n.getArgs().get(1);
			Expression size = new MethodCallExpr(n.getScope(), "size");
			
			// index < size
			al.add(Utility.createAssertion(index, size, BinaryExpr.Operator.less));
		}
		
		else if (methodName.equals("toArray") && n.getArgs().size() == 1)
		{
			Expression arr = n.getArgs().get(0);
			
			// arr != null
			al.add(Utility.createNeqNullAssertion(arr));
		}
		
		else
		{
			al.addAll(super.createAssertion(n));
		}
		
		return al;
	}
	
}
