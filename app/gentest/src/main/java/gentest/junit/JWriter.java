/**
 * Copyright TODO
 */
package gentest.junit;

import gentest.core.data.Sequence;
import gentest.core.data.statement.RArrayAssignment;
import gentest.core.data.statement.RArrayConstructor;
import gentest.core.data.statement.RAssignment;
import gentest.core.data.statement.RConstructor;
import gentest.core.data.statement.REvaluationMethod;
import gentest.core.data.statement.Rmethod;
import gentest.core.data.statement.Statement;
import gentest.junit.CompilationUnitBuilder.MethodBuilder;
import gentest.junit.variable.VariableNamer;
import japa.parser.ast.CompilationUnit;

import java.util.List;


import org.junit.Test;

import sav.common.core.utils.Assert;

/**
 * @author LLT
 *
 */
public class JWriter {
	private static final String JUNIT_TEST_ANNOTATION = Test.class.getSimpleName();
	private String clazzName;
	private String packageName;
	private String methodPrefix;
	
	public CompilationUnit write(List<Sequence> methods) {
		VariableNamer varNamer = new VariableNamer();
		AstNodeConverter converter = new AstNodeConverter(varNamer);
		CompilationUnitBuilder cu = new CompilationUnitBuilder();
		/* package */
		cu.pakage(getPackageName());
		/* import */
		cu.imports(Test.class);
		for (Sequence method : methods) {
			cu.imports(method.getDeclaredTypes());
		}
		cu.startType(getClazzName());
		for (int i = 0; i < methods.size(); i++) {
			Sequence method = methods.get(i);
			varNamer.reset(method);
			MethodBuilder methodBuilder = cu.startMethod(getMethodName(i + 1));
			methodBuilder.throwException(Throwable.class.getSimpleName());
			methodBuilder.markAnnotation(JUNIT_TEST_ANNOTATION);
			for (Statement stmt : method.getStmts()) {
				japa.parser.ast.stmt.Statement astStmt = null;
				switch (stmt.getKind()) {
				case ASSIGNMENT:
					astStmt = converter.fromRAssignment((RAssignment) stmt);
					break;
				case CONSTRUCTOR:
					astStmt = converter.fromRConstructor((RConstructor) stmt);
					break;
				case ARRAY_CONSTRUCTOR:
					astStmt = converter.fromRArrayConstructor((RArrayConstructor) stmt);
					break;
				case ARRAY_ASSIGNMENT:
					astStmt = converter.fromRArrayAssigment((RArrayAssignment) stmt);
					break;
				case METHOD_INVOKE:
				case QUERY_METHOD_INVOKE:
					astStmt = converter.fromRMethod((Rmethod) stmt);
					break;
				case EVALUATION_METHOD:
					cu.imports(org.junit.Assert.class);
					astStmt = converter.fromREvalMethod((REvaluationMethod) stmt);
					break;
				default:
					Assert.fail("not convert to ast from " + stmt);
				}
				methodBuilder.statement(astStmt);
			}
			methodBuilder.endMethod();
		}
		return cu.endType().getResult();
	}

	private String getMethodName(int i) {
		return getMethodPrefix() + i;
	}

	public String getClazzName() {
		return clazzName;
	}

	public void setClazzName(String clazzName) {
		this.clazzName = clazzName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getMethodPrefix() {
		return methodPrefix;
	}

	public void setMethodPrefix(String methodPrefix) {
		this.methodPrefix = methodPrefix;
	}
}
