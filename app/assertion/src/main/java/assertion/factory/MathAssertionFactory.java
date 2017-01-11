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
		
		if (methodName.equals("addExact"))
		{
			Expression x = n.getArgs().get(0);
			Expression y = n.getArgs().get(1);
			
			Expression zero = new IntegerLiteralExpr("0");
			
			// x >= 0
			Expression xGe0 = Utility.createBinExpr(x, zero, BinaryExpr.Operator.greaterEquals);
			// y <= 0
			Expression yLe0 = Utility.createBinExpr(y, zero, BinaryExpr.Operator.lessEquals);
			
			// x <= 0
			Expression xLe0 = Utility.createBinExpr(x, zero, BinaryExpr.Operator.lessEquals);
			// y >= 0
			Expression yGe0 = Utility.createBinExpr(y, zero, BinaryExpr.Operator.greaterEquals);
			
			// x + y
			Expression plus = Utility.createBinExpr(x, y, BinaryExpr.Operator.plus);
			// x + y >= 0
			Expression pGe0 = Utility.createBinExpr(plus, zero, BinaryExpr.Operator.greaterEquals);
			// x + y <= 0
			Expression pLe0 = Utility.createBinExpr(plus, zero, BinaryExpr.Operator.lessEquals);
			
			// (x >= 0 && y <= 0) 
			Expression disj1 = Utility.createBinExpr(xGe0, yLe0, BinaryExpr.Operator.and);
			
			// (x <= 0 && y >= 0) 
			Expression disj2 = Utility.createBinExpr(xLe0, yGe0, BinaryExpr.Operator.and);
			
			// (x >= 0 && y >= 0 && x + y >= 0)
			Expression disj3 = Utility.createBinExpr(BinaryExpr.Operator.and, xGe0, yGe0, pGe0);
			
			// (x <= 0 && y <= 0 && x + y <= 0)
			Expression disj4 = Utility.createBinExpr(BinaryExpr.Operator.and, xLe0, yLe0, pLe0);
			
			Expression e = Utility.createBinExpr(BinaryExpr.Operator.or, disj1, disj2, disj3, disj4);
			
			CollectionUtils.addIfNotNull(al, new AssertStmt(e));
		}
		
		else if (methodName.equals("decrementExact"))
		{
			Expression a = n.getArgs().get(0);
			
			Expression zero = new IntegerLiteralExpr("0");
			Expression one = new IntegerLiteralExpr("1");
			
			// a >= 0
			Expression aGe0 = Utility.createBinExpr(a, zero, BinaryExpr.Operator.greaterEquals);
			
			// a <= 0
			Expression aLe0 = Utility.createBinExpr(a, zero, BinaryExpr.Operator.lessEquals);
			// a - 1
			Expression aM1 = Utility.createBinExpr(a, one, BinaryExpr.Operator.minus);
			// a - 1 <= 0
			Expression aM1Le0 = Utility.createBinExpr(aM1, zero, BinaryExpr.Operator.lessEquals);
			
			// (a <= 0 && a - 1 <= 0)
			Expression disj2 = Utility.createBinExpr(aLe0, aM1Le0, BinaryExpr.Operator.and);
			
			Expression e = Utility.createBinExpr(aGe0, disj2, BinaryExpr.Operator.or);
			
			CollectionUtils.addIfNotNull(al, new AssertStmt(e));
		}
		
		else if (methodName.equals("floorDiv") || methodName.equals("floorMod"))
		{
			Expression y = n.getArgs().get(0);
			
			// y != 0
			CollectionUtils.addIfNotNull(al, Utility.createNeqZeroAssertion(y));
		}
		
		else if (methodName.equals("incrementExact"))
		{
			Expression a = n.getArgs().get(0);
			
			Expression zero = new IntegerLiteralExpr("0");
			Expression one = new IntegerLiteralExpr("1");
			
			// a <= 0
			Expression aLe0 = Utility.createBinExpr(a, zero, BinaryExpr.Operator.lessEquals);
			
			// a >= 0
			Expression aGe0 = Utility.createBinExpr(a, zero, BinaryExpr.Operator.greaterEquals);
			// a + 1
			Expression aP1 = Utility.createBinExpr(a, one, BinaryExpr.Operator.plus);
			// a + 1 >= 0
			Expression aP1Ge0 = Utility.createBinExpr(aP1, zero, BinaryExpr.Operator.greaterEquals);
			
			// (a >= 0 && a + 1 >= 0)
			Expression disj2 = Utility.createBinExpr(aGe0, aP1Ge0, BinaryExpr.Operator.and);
			
			Expression e = Utility.createBinExpr(aLe0, disj2, BinaryExpr.Operator.or);
			
			CollectionUtils.addIfNotNull(al, new AssertStmt(e));
		}
		
		else if (methodName.equals("multiplyExact"))
		{
			Expression x = n.getArgs().get(0);
			Expression y = n.getArgs().get(1);
			
			Expression zero = new IntegerLiteralExpr("0");
			Expression min = null;
			Expression max = null;
			
			Type t1 = Utility.getTypeExpr(subst, x);
			Type t2 = Utility.getTypeExpr(subst, y);
			
			if (t1 != null && t2 != null && t1 instanceof PrimitiveType &&
					t2 instanceof PrimitiveType)
			{
				PrimitiveType.Primitive pt1 = ((PrimitiveType) t1).getType();
				PrimitiveType.Primitive pt2 = ((PrimitiveType) t2).getType();
				
				if (pt1 == Primitive.Long || pt2 == Primitive.Long) {
					min = new LongLiteralExpr(Long.MIN_VALUE + "");
					max = new LongLiteralExpr(Long.MAX_VALUE + "");
				} else {
					min = new IntegerLiteralExpr(Integer.MIN_VALUE + "");
					max = new IntegerLiteralExpr(Integer.MAX_VALUE + "");
				}
				
				// x = 0
				Expression xEq0 = Utility.createBinExpr(x, zero, BinaryExpr.Operator.equals);
				
				// y = 0
				Expression yEq0 = Utility.createBinExpr(y, zero, BinaryExpr.Operator.equals);
				
				// x < 0
				Expression xLt0 = Utility.createBinExpr(x, zero, BinaryExpr.Operator.less);
				// y < 0
				Expression yLt0 = Utility.createBinExpr(y, zero, BinaryExpr.Operator.less);
				// x > 0
				Expression xGt0 = Utility.createBinExpr(x, zero, BinaryExpr.Operator.greater);
				// y > 0
				Expression yGt0 = Utility.createBinExpr(x, zero, BinaryExpr.Operator.greater);
				// x <= y
				Expression xLey = Utility.createBinExpr(x, y, BinaryExpr.Operator.lessEquals);
				// x >= y
				Expression xGey = Utility.createBinExpr(x, y, BinaryExpr.Operator.greaterEquals);
				
				// min / x
				Expression minDivx = Utility.createBinExpr(min, x, BinaryExpr.Operator.divide);
				// max / x
				Expression maxDivx = Utility.createBinExpr(max, x, BinaryExpr.Operator.divide);
				// min / y
				Expression minDivy = Utility.createBinExpr(min, y, BinaryExpr.Operator.divide);
				// max / y
				Expression maxDivy = Utility.createBinExpr(max, y, BinaryExpr.Operator.divide);
				
				// x >= min / y
				Expression xGeminDivy = Utility.createBinExpr(x, minDivy, BinaryExpr.Operator.greaterEquals);
				// x >= max / y
				Expression xGemaxDivy = Utility.createBinExpr(x, maxDivy, BinaryExpr.Operator.greaterEquals);
				// x <= max / y
				Expression xLemaxDivy = Utility.createBinExpr(x, maxDivy, BinaryExpr.Operator.lessEquals);
				
				// y >= min / x
				Expression yGeminDivx = Utility.createBinExpr(y, minDivx, BinaryExpr.Operator.greaterEquals);
				// y >= max / x
				Expression yGemaxDivx = Utility.createBinExpr(y, maxDivx, BinaryExpr.Operator.greaterEquals);
				// y <= max / x
				Expression yLemaxDivx = Utility.createBinExpr(y, maxDivx, BinaryExpr.Operator.lessEquals);
				
				// x <= y && x < 0 && y < 0 && x >= max / y
				Expression disj3 = Utility.createBinExpr(BinaryExpr.Operator.and, xLey, xLt0, yLt0, xGemaxDivy);
				
				// x <= y && x < 0 && y > 0 && x >= min / y 
				Expression disj4 = Utility.createBinExpr(BinaryExpr.Operator.and, xLey, xLt0, yGt0, xGeminDivy);
				
				// x <= y && x > 0 && x <= max / y
				Expression disj5 = Utility.createBinExpr(BinaryExpr.Operator.and, xLey, xGt0, xLemaxDivy);
				
				// x >= y && x < 0 && y < 0 && y >= max / x
				Expression disj6 = Utility.createBinExpr(BinaryExpr.Operator.and, xGey, xLt0, yLt0, yGemaxDivx);
				
				// x >= y && x > 0 && y < 0 && y >= min / x 
				Expression disj7 = Utility.createBinExpr(BinaryExpr.Operator.and, xGey, xGt0, yLt0, yGeminDivx);
				
				// x >= y && y > 0 && y <= max / x
				Expression disj8 = Utility.createBinExpr(BinaryExpr.Operator.and, xGey, yGt0, yLemaxDivx);
				
				Expression e = Utility.createBinExpr(BinaryExpr.Operator.or, xEq0, yEq0, disj3, disj4, disj5, disj6, disj7, disj8);
				
				CollectionUtils.addIfNotNull(al, new AssertStmt(e));
			}
		}
		
		else if (methodName.equals("negateExact"))
		{
			Expression a = n.getArgs().get(0);
			
			Type t = Utility.getTypeExpr(subst, a);
			
			if (t != null && t instanceof PrimitiveType)
			{
				PrimitiveType.Primitive pt = ((PrimitiveType) t).getType();
				
				Expression min = null;
				
				if (pt == Primitive.Long) {
					min = new LongLiteralExpr(Long.MIN_VALUE + "");
				} else {
					min = new IntegerLiteralExpr(Integer.MIN_VALUE + "");
				}
				
				Expression e = Utility.createBinExpr(a, min, BinaryExpr.Operator.notEquals);
				
				CollectionUtils.addIfNotNull(al, new AssertStmt(e));
			}
		}

		if (methodName.equals("substractExact"))
		{
			Expression x = n.getArgs().get(0);
			Expression y = n.getArgs().get(1);
			
			Expression zero = new IntegerLiteralExpr("0");
			
			// x >= 0
			Expression xGe0 = Utility.createBinExpr(x, zero, BinaryExpr.Operator.greaterEquals);
			// y >= 0
			Expression yGe0 = Utility.createBinExpr(y, zero, BinaryExpr.Operator.greaterEquals);
			
			// x <= 0
			Expression xLe0 = Utility.createBinExpr(x, zero, BinaryExpr.Operator.lessEquals);
			// y <= 0
			Expression yLe0 = Utility.createBinExpr(y, zero, BinaryExpr.Operator.lessEquals);
			
			// x - y
			Expression minus = Utility.createBinExpr(x, y, BinaryExpr.Operator.minus);
			// x - y >= 0
			Expression mGe0 = Utility.createBinExpr(minus, zero, BinaryExpr.Operator.greaterEquals);
			// x - y <= 0
			Expression mLe0 = Utility.createBinExpr(minus, zero, BinaryExpr.Operator.lessEquals);
			
			// (x >= 0 && y >= 0) 
			Expression disj1 = Utility.createBinExpr(xGe0, yGe0, BinaryExpr.Operator.and);
			
			// (x <= 0 && y <= 0) 
			Expression disj2 = Utility.createBinExpr(xLe0, yLe0, BinaryExpr.Operator.and);
			
			// (x >= 0 && y <= 0 && x - y >= 0)
			Expression disj3 = Utility.createBinExpr(BinaryExpr.Operator.and, xGe0, yLe0, mGe0);
			
			// (x <= 0 && y >= 0 && x - y <= 0)
			Expression disj4 = Utility.createBinExpr(BinaryExpr.Operator.and, xLe0, yGe0, mLe0);
			
			Expression e = Utility.createBinExpr(BinaryExpr.Operator.or, disj1, disj2, disj3, disj4);
			
			CollectionUtils.addIfNotNull(al, new AssertStmt(e));
		}
		
		else if (methodName.equals("toIntExact"))
		{
			Expression a = n.getArgs().get(0);
			Expression min = new IntegerLiteralExpr(Integer.MIN_VALUE + "");
			Expression max = new IntegerLiteralExpr(Integer.MAX_VALUE + "");
			
			// a >= min
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(a, min, BinaryExpr.Operator.greaterEquals));
			// a <= max
			CollectionUtils.addIfNotNull(al, Utility.createAssertion(a, max, BinaryExpr.Operator.lessEquals));
		}
		
		else
		{
			al.addAll(super.createAssertion(n));
		}

		return al;
	}

}
