package assertion.utility;

import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.stmt.AssertStmt;

public class Utility {

	public static AssertStmt createAssertion(Expression lhs, Expression rhs, BinaryExpr.Operator op) {
		BinaryExpr e = new BinaryExpr(lhs, rhs, op);
		return new AssertStmt(e);
	}
	
	public static AssertStmt createNeqNullAssertion(Expression lhs) {
		Expression rhs = new NullLiteralExpr();
		return createAssertion(lhs, rhs, BinaryExpr.Operator.notEquals);
	}
	
	public static AssertStmt createGteZeroAssertion(Expression lhs) {
		Expression rhs = new IntegerLiteralExpr("0");
		return createAssertion(lhs, rhs, BinaryExpr.Operator.greaterEquals);
	}

	public static AssertStmt createGtZeroAssertion(Expression lhs) {
		Expression rhs = new IntegerLiteralExpr("0");
		return createAssertion(lhs, rhs, BinaryExpr.Operator.greater);
	}
	
	public static AssertStmt createNeqZeroAssertion(Expression lhs) {
		Expression rhs = new IntegerLiteralExpr("0");
		return createAssertion(lhs, rhs, BinaryExpr.Operator.notEquals);
	}
	/*
	public static AssertStmt createLtLengthAssertion(Expression lhs, Expression name) {
		Expression rhs = new FieldAccessExpr(name, "length");
		return createAssertion(lhs, rhs, BinaryExpr.Operator.less);
	}
	*/
}
