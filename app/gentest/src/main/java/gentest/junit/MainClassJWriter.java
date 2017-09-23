/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.junit;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gentest.core.data.Sequence;
import gentest.junit.CompilationUnitBuilder;
import gentest.junit.CompilationUnitBuilder.MethodBuilder;
import gentest.junit.ICompilationUnitWriter;
import gentest.junit.JWriter;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;
import sav.common.core.SavRtException;
import sav.common.core.utils.ClassUtils;

/**
 * @author LLT
 *
 */
public class MainClassJWriter extends JWriter implements ICompilationUnitWriter {
	private static final String CLASS_SUFFIX = "Main";
	private static final String TEST_OBJ_VAR_PREFIX = "obj";
	private MethodBuilder mainMethod;
	private CompilationUnitBuilder cuBuilder;
	private int idx;
	private CompilationUnit result;
	
	public MainClassJWriter(String pkgName, String classPrefix) {
		reset(pkgName, classPrefix);
	}

	private void reset(String pkgName, String classPrefix) {
		try {
			result = null;
			initMainClass(pkgName, classPrefix);
		} catch(ParseException ex) {
			throw new SavRtException(ex);
		}
	}
	
	@Override
	public CompilationUnit write(List<Sequence> methods, String pkgName, String className, String methodPrefix) {
		CompilationUnit cu = super.write(methods, pkgName, className, methodPrefix);
		createCallInMainClass(pkgName, className, cu);
		return cu;
	}

	/**
	 *	public static void main(String[] args) {
			TestClass clazz = new TestClass();
			invokeMethod(clazz, "method1");
		}
	 */
	public void createCallInMainClass(String pkgName, String className, CompilationUnit cu) {
		cuBuilder.imports(ClassUtils.getCanonicalName(pkgName, className));
		/* add class initialization statement
		 * TestClass clazz = new TestClass(); */
		Type type = toType(className, 0);
		List<VariableDeclarator> vars = new ArrayList<VariableDeclarator>();
		/* variable name */
		String receiverObjName = getObjVarName();
		VariableDeclarator var = new VariableDeclarator(new VariableDeclaratorId(receiverObjName));
		/* statement */
		Expression initExpr = new ObjectCreationExpr(null, new ClassOrInterfaceType(className), null);
		var.setInit(initExpr);
		vars.add(var);
		Expression expr = new VariableDeclarationExpr(type, vars);
		ExpressionStmt ojbIntialStmt = new ExpressionStmt(expr);
		mainMethod.statement(ojbIntialStmt);
		
		/* invoke method statement */
		for (BodyDeclaration member : cu.getTypes().get(0).getMembers()) {
			if (member instanceof MethodDeclaration) {
				MethodDeclaration method = (MethodDeclaration) member;
				MethodCallExpr methodCallExpr = new MethodCallExpr(null, INVOKE_METHOD_NAME);
				methodCallExpr.setArgs(Arrays.asList(new NameExpr(receiverObjName), new StringLiteralExpr(method.getName())));
				ExpressionStmt stmt = new ExpressionStmt(methodCallExpr);
				mainMethod.statement(stmt);
			}
		}
	}
	
	private String getObjVarName() {
		return TEST_OBJ_VAR_PREFIX + idx++;
	}

	private void initMainClass(String pkgName, String classPrefix) throws ParseException {
		cuBuilder = CompilationUnitBuilder.createNew()
			.pakage(pkgName)
			.startType(classPrefix + CLASS_SUFFIX);
		createInvokeMethod(cuBuilder);
		mainMethod = cuBuilder.startMethod("main")
			.modifiers(ModifierSet.addModifier(ModifierSet.PUBLIC, ModifierSet.STATIC))
			.parameters(toParameter(String.class, "args", 1));
	}
	
	private static final String INVOKE_METHOD_NAME = "invokeMethod";
	public void createInvokeMethod(CompilationUnitBuilder cu) throws ParseException {
		/* needed imports for method */
		cu.imports(Method.class.getCanonicalName());
		/* build method */
		Statement tryStmt = JavaParser.parseStatement(
				"try {"
				+ "		Method method = obj.getClass().getDeclaredMethod(methodName);"
				+ "		method.invoke(obj);"
				+ "} catch (Throwable e) {"
				+ "		return;"
				+ "};");
		cu.startMethod(INVOKE_METHOD_NAME)
			.modifiers(ModifierSet.addModifier(ModifierSet.PRIVATE, ModifierSet.STATIC))
			.parameters(toParameter(Object.class, "obj", 0),
					toParameter(String.class, "methodName", 0))
			.statement(tryStmt)
			.endMethod();
	}
	
	private Parameter toParameter(Class<?> classOrIfaceType, String name, int arrayCount) {
		Type paramType = toType(classOrIfaceType.getSimpleName(), arrayCount);
		VariableDeclaratorId paramId = new VariableDeclaratorId(name);
		return new Parameter(paramType, paramId );
	}

	private Type toType(String className, int arrayCount) {
		ClassOrInterfaceType type = new ClassOrInterfaceType(className);
		Type paramType = (arrayCount == 0) ? new ReferenceType(type) : new ReferenceType(type, arrayCount);
		return paramType;
	}
	
	public CompilationUnit getMainClass() {
		if (result == null) {
			result = mainMethod.endMethod().getResult();
		}
		return result;
	}
}
