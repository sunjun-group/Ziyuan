/**
 * Copyright TODO
 */
package gentest.junit;

import gentest.core.commons.utils.TypeUtils;
import gentest.core.data.statement.RArrayAssignment;
import gentest.core.data.statement.RArrayConstructor;
import gentest.core.data.statement.RAssignment;
import gentest.core.data.statement.RConstructor;
import gentest.core.data.statement.REvaluationMethod;
import gentest.core.data.statement.Rmethod;
import gentest.core.data.statement.StatementVisitor;
import gentest.junit.variable.IVariableNamer;
import japa.parser.ASTHelper;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.ArrayCreationExpr;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.AssignExpr.Operator;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.CharLiteralExpr;
import japa.parser.ast.expr.DoubleLiteralExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.LiteralExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.PrimitiveType.Primitive;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 * 
 */
public class AstNodeConverter implements StatementVisitor {
	private IVariableNamer varNamer;
	private Statement result;
	private Set<String> duplicateImports;

	public AstNodeConverter(IVariableNamer varNamer, Set<String> duplicateImports) {
		this.varNamer = varNamer;
		this.duplicateImports = duplicateImports;
	}
	
	public void reset() {
		result = null;
	}
	
	/**
	 * assignment for primitive types, String, enum (constantTypes)
	 */
	@Override
	public boolean visit(RAssignment assignment) {
		/* type */
		Type paramType = null;
		Class<?> vartype = assignment.getType();
		if (TypeUtils.isPrimitive(vartype)) {
			/* primitive */
			paramType = new PrimitiveType(
					TypeUtils.getAssociatePrimitiveType(vartype));
		} else {
			/* primitive wrapper, enum or String */
			paramType = toReferenceType(vartype);
		}

		/* value */
		Object varValue = assignment.getValue();
		/* primitive or primitive wrapper */
		Expression initExpr = toNullLiteralExpr(varValue);
		if (initExpr == null) {
			initExpr = toPrimitiveLiteralExpr(vartype, varValue);
		}
		if (initExpr == null) {
			/* string */
			initExpr = toStringLiteralExpr(vartype, varValue);
		}
		if (initExpr == null) {
			/* enum */
			initExpr = toEnumFieldExpr(vartype, varValue);
		}
		Assert.assertTrue(initExpr != null);

		VariableDeclarator varDecl = new VariableDeclarator();
		varDecl.setId(new VariableDeclaratorId(varNamer.getName(
				assignment.getType(), assignment.getOutVarId())));
		varDecl.setInit(initExpr);
		Expression expr = new VariableDeclarationExpr(paramType,
				CollectionUtils.listOf(varDecl));
		result = new ExpressionStmt(expr);
		return true;
	}

	@Override
	public boolean visitRmethod(Rmethod rmethod) {
		Expression stmtExpr = createMethodCallExpression(rmethod,
				rmethod.hasOutputVar());
		result = new ExpressionStmt(stmtExpr);
		return true;
	}

	@Override
	public boolean visit(RConstructor constructor) {
		/* Type */
		Type type = toReferenceType(constructor.getDeclaringClass());
		List<VariableDeclarator> vars = new ArrayList<VariableDeclarator>();
		/* variable name */
		VariableDeclarator var = new VariableDeclarator(
				new VariableDeclaratorId(varNamer.getName(
						constructor.getOutputType(), constructor.getOutVarId())));
		/* constructor input */
		List<Expression> constructorArgs = new ArrayList<Expression>();
		Class<?>[] inputTypes = constructor.getInputTypes();
		NameExpr scope = null;
		int inVarsLength = constructor.getInVarIds().length;
		boolean scopeRequire = constructor.isMemberNestedConstructor();
		for (int i = 0; i < inVarsLength; i++) {
			NameExpr nameExpr = new NameExpr(varNamer.getName(
					inputTypes[i], constructor.getInVarIds()[i]));
			if (scopeRequire && i == 0) {
				scope = nameExpr;
			} else {
				constructorArgs.add(nameExpr);
			}
		}
		/* statement */
		Expression initExpr = new ObjectCreationExpr(scope,
				new ClassOrInterfaceType(getClassDeclaringName(constructor.getDeclaringClass())),
				constructorArgs);
		var.setInit(initExpr);
		vars.add(var);
		Expression expr = new VariableDeclarationExpr(type, vars);
		result = new ExpressionStmt(expr);
		return true;
	}

	@Override
	public boolean visit(REvaluationMethod stmt) {
		NameExpr scope = new NameExpr("Assert");
		MethodCallExpr assertTrue = new MethodCallExpr(scope, "assertTrue",
				CollectionUtils.listOf(createMethodCallExpression(stmt, false)));

		result = new ExpressionStmt(assertTrue);
		return true;
	}

	@Override
	public boolean visit(RArrayConstructor arrayConstructor) {
		Type arrayContentType = toReferenceType(arrayConstructor.getContentType());
		Type arrayType = new ReferenceType(arrayContentType,
				arrayConstructor.getSizes().length);
		VariableDeclarator var = new VariableDeclarator(
				new VariableDeclaratorId(
						varNamer.getArrVarName(arrayConstructor.getOutVarId())));

		List<Expression> dimensions = new ArrayList<Expression>(
				arrayConstructor.getSizes().length);
		for (int i = 0; i < arrayConstructor.getSizes().length; i++) {
			IntegerLiteralExpr ile = new IntegerLiteralExpr(
					String.valueOf(arrayConstructor.getSizes()[i]));
			dimensions.add(ile);
		}
		Expression arrayInitStatement = new ArrayCreationExpr(arrayContentType,
				dimensions, 0);
		var.setInit(arrayInitStatement);

		Expression variableDeclarationStatement = new VariableDeclarationExpr(
				arrayType, Arrays.asList(var));
		result = new ExpressionStmt(variableDeclarationStatement);
		return true;
	}

	@Override
	public boolean visit(RArrayAssignment stmt) {
		final Expression arrayAccessExp = getArrayAccessExp(
				varNamer.getExistVarName(stmt.getArrayVarID()), stmt.getIndex());
		final Expression valueExp = new NameExpr(varNamer.getExistVarName(stmt
				.getLocalVariableID()));
		final Expression assignmentExpression = new AssignExpr(arrayAccessExp,
				valueExp, Operator.assign);
		result = new ExpressionStmt(assignmentExpression);
		return true;
	}

	private Expression toNullLiteralExpr(Object varValue) {
		if (varValue == null) {
			return new NullLiteralExpr();
		}
		return null;
	}

	private FieldAccessExpr toEnumFieldExpr(Class<?> vartype, Object varValue) {
		if (TypeUtils.isEnum(varValue)) {
			Enum<?> enumValue = (Enum<?>) varValue;
			NameExpr scope = ASTHelper
					.createNameExpr(getClassDeclaringName(vartype));
			return new FieldAccessExpr(scope, enumValue.name());
		}
		return null;
	}

	private LiteralExpr toStringLiteralExpr(Class<?> vartype, Object varValue) {
		if (TypeUtils.isString(vartype)) {
			return new StringLiteralExpr(varValue.toString());
		}
		return null;
	}

	private LiteralExpr toPrimitiveLiteralExpr(Class<?> vartype, Object value) {
		/* primitive or primitiveWrapper */
		Primitive primitiveType = TypeUtils.getAssociatePrimitiveType(vartype);
		if (primitiveType != null) {
			switch (primitiveType) {
			case Boolean:
				return new BooleanLiteralExpr((Boolean) value);
			case Char:
				return new CharLiteralExpr(getDisplayString((Character) value));
			case Byte:
			case Int:
			case Short:
				return new IntegerLiteralExpr(value.toString());
			case Double:
				return new DoubleLiteralExpr(value.toString());
			case Float:
				return new LongLiteralExpr(value.toString() + "f");
			case Long:
				return new LongLiteralExpr(value.toString() + "l");
			default:
				return new StringLiteralExpr(value.toString());
			}
		}
		return null;
	}

	private Expression getArrayAccessExp(final String arrayName, final int[] location) {
		final int arrayLength = location.length;
		if (arrayLength <= 0) {
			return new NameExpr(arrayName);
		} else {
			return new ArrayAccessExpr(getArrayAccessExp(arrayName,
					Arrays.copyOfRange(location, 0, arrayLength - 1)), new NameExpr(
					String.valueOf(location[arrayLength - 1])));
		}
	}

	private Expression createMethodCallExpression(Rmethod rmethod, boolean declareValue) {
		NameExpr scope;
		if (rmethod.getReceiverVarId() == gentest.core.data.statement.Statement.INVALID_VAR_ID) {
			scope = new NameExpr(getClassDeclaringName(rmethod.getDeclaringType()));
		} else {
			scope = new NameExpr(varNamer.getExistVarName(rmethod.getReceiverVarId()));
		}
		List<Expression> inputs = new ArrayList<Expression>();
		for (int inId : rmethod.getInVarIds()) {
			inputs.add(new NameExpr(varNamer.getExistVarName(inId)));
		}
		MethodCallExpr callExpr = new MethodCallExpr(scope, rmethod.getName(), inputs);

		Expression stmtExpr = null;
		if (declareValue) {
			List<VariableDeclarator> vars = new ArrayList<VariableDeclarator>();
			VariableDeclarator varDecl = new VariableDeclarator(new VariableDeclaratorId(
					varNamer.getName(rmethod.getReturnType(), rmethod.getOutVarId())), callExpr);
			vars.add(varDecl);
			stmtExpr = new VariableDeclarationExpr(toReferenceType(rmethod.getReturnType()), vars);
		} else {
			stmtExpr = callExpr;
		}
		return stmtExpr;
	}
	
	private static final List<Character> escapeSequence = Arrays.asList('\t', '\b', '\n', '\r', '\f', '\'', '\"', '\\');
	private String getDisplayString(Character ch) {
		String value;
		if (escapeSequence.contains(ch)) {
			value = new String(new char[] {'\\', ch});
		} else {
			value = ch.toString();
		}
		return value;
	}

	private ReferenceType toReferenceType(Class<?> vartype) {
		String className = getClassDeclaringName(vartype);
		return new ReferenceType(new ClassOrInterfaceType(className));
	}
	
	private String getClassDeclaringName(Class<?> vartype) {
		if (duplicateImports.contains(vartype.getCanonicalName())) {
			return vartype.getCanonicalName();
		}
		return vartype.getSimpleName();
	}

	public Statement getResult() {
		return result;
	}
}
