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

public class StringAssertionFactory extends ObjectAssertionFactory {
	
	public StringAssertionFactory(VariableSubstitution subst) {
		super(subst);
	}
	
	public List<AssertStmt> createAssertion(MethodCallExpr n) {
		List<AssertStmt> al = new ArrayList<AssertStmt>();

		String methodName = n.getName();

		if (methodName.equals("charAt") || methodName.equals("codePointAt"))
		{
			Expression index = n.getArgs().get(0);
			Expression length = new MethodCallExpr(n.getScope(), "length");
			
			// index >= 0
			CollectionUtils.addIfNotNull(al, Utility.createGteZeroAssertion(index));
			// index < length
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(index, length, BinaryExpr.Operator.less));
		}
		
		else if (methodName.equals("codePointBefore"))
		{
			Expression index = n.getArgs().get(0);
			Expression length = new MethodCallExpr(n.getScope(), "length");
			
			// index > 0
			CollectionUtils.addIfNotNull(al, Utility.createGtZeroAssertion(index));
			// index <= length
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(index, length, BinaryExpr.Operator.lessEquals));
		}
		
		else if (methodName.equals("codePointCount") || methodName.equals("subSequence")
					|| (methodName.equals("substring") && n.getArgs().size() == 2))
		{
			Expression begin = n.getArgs().get(0);
			Expression end = n.getArgs().get(1);
			Expression length = new MethodCallExpr(n.getScope(), "length");
			
			// begin >= 0
			CollectionUtils.addIfNotNull(al, Utility.createGteZeroAssertion(begin));
			// end <= length
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(end, length, BinaryExpr.Operator.lessEquals));
			// begin <= end
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(begin, end, BinaryExpr.Operator.lessEquals));
		}
		
		else if (methodName.equals("offsetByCodePoints"))
		{
			Expression index = n.getArgs().get(0);
			Expression off = n.getArgs().get(1);
			Expression length = new MethodCallExpr(n.getScope(), "length");
			
			// index >= 0
			CollectionUtils.addIfNotNull(al, Utility.createGteZeroAssertion(index));
			// index <= length
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(index, length, BinaryExpr.Operator.lessEquals));
			// index + off >= 0
			CollectionUtils.addIfNotNull(al, Utility.createGteZeroAssertion(new BinaryExpr(index, off, BinaryExpr.Operator.plus)));
			// index + off <= length
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(new BinaryExpr(index, off, BinaryExpr.Operator.plus),
					length, BinaryExpr.Operator.lessEquals));
		}
		
		else if (methodName.equals("getChars"))
		{
			Expression srcBegin = n.getArgs().get(0);
			Expression srcEnd = n.getArgs().get(1);
			Expression dstBegin = n.getArgs().get(3);
			Expression srcLength = new MethodCallExpr(n.getScope(), "length");
			Expression dstLength = new MethodCallExpr(n.getArgs().get(2), "length");
			
			// srcBegin >= 0
			CollectionUtils.addIfNotNull(al, Utility.createGteZeroAssertion(srcBegin));
			// srcBegin <= srcEnd
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(srcBegin, srcEnd, BinaryExpr.Operator.lessEquals));
			// srcEnd <= srcLength
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(srcEnd, srcLength, BinaryExpr.Operator.lessEquals));
			// dstBegin >= 0
			CollectionUtils.addIfNotNull(al, Utility.createGteZeroAssertion(dstBegin));
			// dstBegin + (srcEnd - srcBegin) <= dst.length
			Expression bin = new BinaryExpr(dstBegin, new BinaryExpr(srcEnd, srcBegin, BinaryExpr.Operator.minus), BinaryExpr.Operator.plus);
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(bin, dstLength, BinaryExpr.Operator.lessEquals));
		}
		
		else if (methodName.equals("substring") && n.getArgs().size() == 1)
		{
			Expression begin = n.getArgs().get(0);
			Expression length = new MethodCallExpr(n.getScope(), "length");
			
			// begin >= 0
			CollectionUtils.addIfNotNull(al, Utility.createGteZeroAssertion(begin));
			// begin <= length
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(begin, length, BinaryExpr.Operator.lessEquals));
		}
		
		else if (methodName.equals("join"))
		{
			Expression del = n.getArgs().get(0);
			Expression ele = n.getArgs().get(1);
			
			// del != null
			CollectionUtils.addIfNotNull(al, Utility.createNeqNullAssertion(del));
			// ele != null
			CollectionUtils.addIfNotNull(al, Utility.createNeqNullAssertion(ele));
		}
		
		else if ((methodName.equals("valueOf") && n.getArgs().size() == 3)
					|| (methodName.equals("copyValueOf") && n.getArgs().size() == 3))
		{
			Expression data = n.getArgs().get(0);
			Expression length = new MethodCallExpr(data, "length");
			Expression off = n.getArgs().get(1);
			Expression count = n.getArgs().get(2);
			
			// data != null
			CollectionUtils.addIfNotNull(al, Utility.createNeqNullAssertion(data));
			// off >= 0
			CollectionUtils.addIfNotNull(al, Utility.createGteZeroAssertion(off));
			// count >= 0
			CollectionUtils.addIfNotNull(al, Utility.createGteZeroAssertion(count));
			// off + count <= length
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(new BinaryExpr(off, count, BinaryExpr.Operator.plus),
					length, BinaryExpr.Operator.lessEquals));
		}
		
		else
		{
			al.addAll(super.createAssertion(n));
		}

		return al;
	}

}
