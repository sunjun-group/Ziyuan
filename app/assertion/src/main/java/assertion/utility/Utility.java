package assertion.utility;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.CastExpr;
import japa.parser.ast.expr.CharLiteralExpr;
import japa.parser.ast.expr.DoubleLiteralExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.LiteralExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.PrimitiveType.Primitive;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;
import japa.parser.ast.type.VoidType;
import mutation.mutator.VariableSubstitution;

public class Utility {

	public static AssertStmt createAssertion(Expression lhs, Expression rhs, BinaryExpr.Operator op) {
		if (lhs instanceof LiteralExpr && rhs instanceof LiteralExpr) return null;
		BinaryExpr e = new BinaryExpr(lhs, rhs, op);
		return new AssertStmt(e);
	}
	
	public static AssertStmt createNeqNullAssertion(Expression lhs) {
		Expression rhs = new NullLiteralExpr();
		return createAssertion(lhs, rhs, BinaryExpr.Operator.notEquals);
	}
	
	public static AssertStmt createGteZeroAssertion(Expression lhs) {
		if (lhs instanceof LiteralExpr) return null;
		Expression rhs = new IntegerLiteralExpr("0");
		return createAssertion(lhs, rhs, BinaryExpr.Operator.greaterEquals);
	}

	public static AssertStmt createGtZeroAssertion(Expression lhs) {
		if (lhs instanceof LiteralExpr) return null;
		Expression rhs = new IntegerLiteralExpr("0");
		return createAssertion(lhs, rhs, BinaryExpr.Operator.greater);
	}
	
	public static AssertStmt createNeqZeroAssertion(Expression lhs) {
		if (lhs instanceof LiteralExpr) return null;
		else {
			if (lhs instanceof FieldAccessExpr) {
				FieldAccessExpr field = (FieldAccessExpr) lhs;
				String name = field.getFieldExpr().getName();
				if (StringUtils.isAllUpperCase(name.replaceAll("_", ""))) return null;
			}
			
			Expression rhs = new IntegerLiteralExpr("0");
			return createAssertion(lhs, rhs, BinaryExpr.Operator.notEquals);
		}
	}
	
	public static String getSigType(MethodDeclaration n) {
		List<Parameter> params = n.getParameters();
		Type typ = n.getType();
		
		StringBuilder s = new StringBuilder("(");
		if (params != null) {
			for (Parameter param : params) {
				s.append(getSigType(param.getType()));
			}
		}
		
		s.append(")");
		s.append(getSigType(typ));
		
		return n.getName() + s.toString();
	}
	
	public static String getSigType(ConstructorDeclaration n) {
		List<Parameter> params = n.getParameters();
		
		StringBuilder s = new StringBuilder("(");
		if (params != null) {
			for (Parameter param : params) {
				s.append(getSigType(param.getType()));
			}
		}
		
		s.append(")");
		
		return n.getName() + s.toString();
	}
	
	public static String getSigType(Type typ) {
		StringBuilder s = new StringBuilder("");
		
		if (typ instanceof PrimitiveType) {
			PrimitiveType pTyp = (PrimitiveType) typ;
			if (pTyp.getType() == Primitive.Int) {
				s.append("I");
			} else if (pTyp.getType() == Primitive.Byte) {
				s.append("B");
			} else if (pTyp.getType() == Primitive.Long) {
				s.append("J");
			} else if (pTyp.getType() == Primitive.Float) {
				s.append("F");
			} else if (pTyp.getType() == Primitive.Double) {
				s.append("D");
			} else if (pTyp.getType() == Primitive.Short) {
				s.append("S");
			} else if (pTyp.getType() == Primitive.Char) {
				s.append("C");
			} else if (pTyp.getType() == Primitive.Boolean) {
				s.append("Z");
			}
		} else if (typ instanceof VoidType) {
			s.append("V");
		} else if (typ instanceof ReferenceType) {
			ReferenceType rTyp = (ReferenceType) typ;
			for (int i = 0; i < rTyp.getArrayCount(); i++) {
				s.append("[");
			}
			s.append(getSigType(rTyp.getType()));
		} else if (typ instanceof ClassOrInterfaceType) {
			ClassOrInterfaceType cTyp = (ClassOrInterfaceType) typ;
			s.append("L" + cTyp.getName().replace('.', '/') + ";");
		}
		
		return s.toString();
	}
	
	public static Type getTypeExpr(VariableSubstitution subst, Expression n) {
		if (n instanceof BooleanLiteralExpr) {
			return new PrimitiveType(Primitive.Boolean);
		} else if (n instanceof CharLiteralExpr) {
			return new PrimitiveType(Primitive.Char);
		} else if (n instanceof DoubleLiteralExpr) {
			return new PrimitiveType(Primitive.Double);
		} else if (n instanceof IntegerLiteralExpr) {
			return new PrimitiveType(Primitive.Int);
		} else if (n instanceof LongLiteralExpr) {
			return new PrimitiveType(Primitive.Long);
		} else if (n instanceof NameExpr) {
			return subst.getType(((NameExpr) n).getName(), n.getBeginLine(), n.getBeginColumn());
		} else if (n instanceof BinaryExpr) {
			BinaryExpr.Operator op = ((BinaryExpr) n).getOperator();
			if (op == BinaryExpr.Operator.and || op == BinaryExpr.Operator.or ||
					op == BinaryExpr.Operator.equals || op == BinaryExpr.Operator.notEquals ||
					op == BinaryExpr.Operator.greater || op == BinaryExpr.Operator.greaterEquals ||
					op == BinaryExpr.Operator.less || op == BinaryExpr.Operator.lessEquals) {
				return new PrimitiveType(Primitive.Boolean); 
			} else {
				Type t1 = getTypeExpr(subst, ((BinaryExpr) n).getLeft());
				Type t2 = getTypeExpr(subst, ((BinaryExpr) n).getRight());
				
				if (t1 instanceof PrimitiveType && t2 instanceof PrimitiveType) {
					PrimitiveType.Primitive pt1 = ((PrimitiveType) t1).getType();
					PrimitiveType.Primitive pt2 = ((PrimitiveType) t2).getType();
					
					if (pt1 == pt2) {
						return new PrimitiveType(pt1);
					} else if (pt1 == Primitive.Double || pt2 == Primitive.Double) {
						return new PrimitiveType(Primitive.Double);
					} else if (pt1 == Primitive.Float || pt2 == Primitive.Float) {
						return new PrimitiveType(Primitive.Float);
					} else if (pt1 == Primitive.Long || pt2 == Primitive.Long) {
						return new PrimitiveType(Primitive.Long);
					} else if (pt1 == Primitive.Int || pt2 == Primitive.Int) {
						return new PrimitiveType(Primitive.Int);
					} else if (pt1 == Primitive.Short || pt2 == Primitive.Short) {
						return new PrimitiveType(Primitive.Short);
					} else {
						return new PrimitiveType(pt1);
					}
				} else {
					return null;
				}
			}
		} else if (n instanceof UnaryExpr) {
			return getTypeExpr(subst, ((UnaryExpr) n).getExpr());
		} else if (n instanceof CastExpr) {
			return ((CastExpr) n).getType();
		} else if (n instanceof ArrayAccessExpr) {
			Type t = subst.getType(((ArrayAccessExpr) n).getName().toString(), n.getBeginLine(), n.getBeginColumn());
			while (t instanceof ReferenceType) {
				t = ((ReferenceType) t).getType();
			}
			
			return t;
		} else {
			return null;
		}
	}
	
}
