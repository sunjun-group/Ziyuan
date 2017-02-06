/**
 * Copyright TODO
 */
package gentest.junit;

import gentest.core.data.Sequence;
import gentest.core.data.statement.Statement;
import gentest.junit.CompilationUnitBuilder.MethodBuilder;
import gentest.junit.variable.VariableNamer;
import japa.parser.ast.CompilationUnit;

import java.util.List;

/**
 * @author LLT
 *
 */
public class JWriter {
	private static final String JUNIT_TEST_ANNOTATION = "Test";
	private static final String JUNIT_TEST_ANNOTATION_IMPORT = "org.junit.Test";
	private static final String JUNIT_ASSERT_CLAZZ = "org.junit.Assert";
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
		cu.imports(JUNIT_TEST_ANNOTATION_IMPORT);
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
			if (!method.getStatementByType(
					Statement.RStatementKind.EVALUATION_METHOD).isEmpty()) {
				cu.imports(JUNIT_ASSERT_CLAZZ);
			}
			for (Statement stmt : method.getStmts()) {
				converter.reset();
				stmt.accept(converter);
				methodBuilder.statement(converter.getResult());
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
