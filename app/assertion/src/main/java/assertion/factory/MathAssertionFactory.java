package assertion.factory;

import java.util.ArrayList;
import java.util.List;

import assertion.utility.Utility;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.PrimitiveType.Primitive;
import japa.parser.ast.type.Type;
import mutation.mutator.VariableSubstitution;
import sav.common.core.utils.CollectionUtils;

public class MathAssertionFactory extends ObjectAssertionFactory {
	
	public MathAssertionFactory(VariableSubstitution subst) {
		super(subst);
	}

	public List<AssertStmt> createAssertion(MethodCallExpr n) {
		List<AssertStmt> al = new ArrayList<AssertStmt>();

		String methodName = n.getName();

		if (methodName.equals("addExact") || methodName.equals("substractExact") ||
				methodName.equals("multiplyExact"))
		{
			Expression e1 = n.getArgs().get(0);
			Expression e2 = n.getArgs().get(1);
			
			Type t1 = Utility.getTypeExpr(subst, e1);
			Type t2 = Utility.getTypeExpr(subst, e2);
			
			if (t1 != null && t2 != null && t1 instanceof PrimitiveType &&
					t2 instanceof PrimitiveType)
			{
				Expression lhs = null;
						
				if (methodName.equals("addExact")) {
					lhs = new BinaryExpr(e1, e2, BinaryExpr.Operator.plus);
				} else if (methodName.equals("substractExact")) {
					lhs = new BinaryExpr(e1, e2, BinaryExpr.Operator.minus);
				} else if (methodName.equals("multiplyExact")) {
					lhs = new BinaryExpr(e1, e2, BinaryExpr.Operator.times);
				}
				
				PrimitiveType.Primitive pt1 = ((PrimitiveType) t1).getType();
				PrimitiveType.Primitive pt2 = ((PrimitiveType) t2).getType();
				
				Expression min = null;
				Expression max = null;
				
				if (pt1 == Primitive.Long || pt2 == Primitive.Long) {
					min = new LongLiteralExpr(Long.MIN_VALUE + "");
					max = new LongLiteralExpr(Long.MAX_VALUE + "");
				} else {
					min = new IntegerLiteralExpr(Integer.MIN_VALUE + "");
					max = new IntegerLiteralExpr(Integer.MAX_VALUE + "");
				}
				
				// lhs >= min
				CollectionUtils.addIfNotNull(al, Utility.createAssertion(lhs, min, BinaryExpr.Operator.greaterEquals));
				// lhs <= max
				CollectionUtils.addIfNotNull(al, Utility.createAssertion(lhs, max, BinaryExpr.Operator.lessEquals));
			}
		}
		
		else if (methodName.equals("negateExact") || methodName.equals("incrementExact") ||
				methodName.equals("decrementExact"))
		{
			Expression e1 = n.getArgs().get(0);
			Expression e2 = new IntegerLiteralExpr("1");
			
			Type t1 = Utility.getTypeExpr(subst, e1);
			
			if (t1 != null && t1 instanceof PrimitiveType)
			{
				Expression lhs = null;
						
				if (methodName.equals("negateExact")) {
					lhs = new UnaryExpr(e1, UnaryExpr.Operator.negative);
				} else if (methodName.equals("incrementExact")) {
					lhs = new BinaryExpr(e1, e2, BinaryExpr.Operator.plus);
				} else if (methodName.equals("decrementExact")) {
					lhs = new BinaryExpr(e1, e2, BinaryExpr.Operator.minus);
				}
				
				PrimitiveType.Primitive pt1 = ((PrimitiveType) t1).getType();
				
				Expression min = null;
				Expression max = null;
				
				if (pt1 == Primitive.Long) {
					min = new LongLiteralExpr(Long.MIN_VALUE + "");
					max = new LongLiteralExpr(Long.MAX_VALUE + "");
				} else {
					min = new IntegerLiteralExpr(Integer.MIN_VALUE + "");
					max = new IntegerLiteralExpr(Integer.MAX_VALUE + "");
				}
				
				// lhs >= min
				CollectionUtils.addIfNotNull(al, Utility.createAssertion(lhs, min, BinaryExpr.Operator.greaterEquals));
				// lhs <= max
				CollectionUtils.addIfNotNull(al, Utility.createAssertion(lhs, max, BinaryExpr.Operator.lessEquals));
			}
		}
		
		else if (methodName.equals("toIntExact"))
		{
			Expression lhs = n.getArgs().get(0);
			Expression min = new IntegerLiteralExpr(Integer.MIN_VALUE + "");
			Expression max = new IntegerLiteralExpr(Integer.MAX_VALUE + "");
			
			// lhs >= min
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(lhs, min, BinaryExpr.Operator.greaterEquals));
			// lhs <= max
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(lhs, max, BinaryExpr.Operator.lessEquals));
		}
		
		else if (methodName.equals("floorDiv") || methodName.equals("floorMod"))
		{
			Expression lhs = n.getArgs().get(0);
			
			// lhs != 0
			CollectionUtils.addIfNotNull(al, Utility.createNeqZeroAssertion(lhs));
		}
		
		else
		{
			al.addAll(super.createAssertion(n));
		}

		return al;
	}

}
