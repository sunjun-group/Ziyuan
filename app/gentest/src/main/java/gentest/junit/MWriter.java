/**
 * Copyright TODO
 */
package gentest.junit;

import gentest.core.data.Sequence;
import gentest.core.data.statement.Statement;
import gentest.junit.CompilationUnitBuilder.MethodBuilder;
import gentest.junit.variable.VariableNamer;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author ZhangHarry
 *
 */
public class MWriter implements ICompilationUnitWriter {
	private static final String DEPRECATION_SUPPRESS_WARNING = "SuppressWarnings(\"deprecation\")";
	private static final String JUNIT_TEST_ANNOTATION_IMPORT = "org.junit.Test";
	private static final String JUNIT_ASSERT_CLAZZ = "org.junit.Assert";
	private String clazzName;
	private String packageName;
	private String methodPrefix;
	private Set<String> duplicateImports;
	
	public MWriter() {
		duplicateImports = new HashSet<String>();
	}
	
	@Override
	public CompilationUnit write(List<Sequence> methods, String pkgName, String className, String methodPrefix) {
		setPackageName(pkgName);
		setClazzName(className);
		setMethodPrefix(methodPrefix);
		return write(methods);
	}
	
	public CompilationUnit write(List<Sequence> methods) {
		VariableNamer varNamer = new VariableNamer();
		CompilationUnitBuilder cu = new CompilationUnitBuilder();
		/* package */
		cu.pakage(getPackageName());
		/* import */
		cu.imports(JUNIT_TEST_ANNOTATION_IMPORT);
		duplicateImports.clear();
		for (Sequence method : methods) {
			duplicateImports.addAll(cu.imports(method.getDeclaredTypes()));
		}
		cu.startType(getClazzName())
			.markAnnotation(DEPRECATION_SUPPRESS_WARNING);
		AstNodeConverter astConverter = new AstNodeConverter(varNamer, duplicateImports);
		for (int i = 0; i < methods.size(); i++) {
			Sequence method = methods.get(i);
			varNamer.reset(method);
			MethodBuilder methodBuilder = cu.startMethod(getMethodName(i + 1));
			methodBuilder.modifiers(ModifierSet.PUBLIC, ModifierSet.STATIC);
			methodBuilder.throwException(Throwable.class.getSimpleName());
			/** parameters */
			List<Parameter> parameters = new LinkedList<>();
			Type type = new ReferenceType(new ClassOrInterfaceType("String"), 1);
			Parameter parameter = new Parameter(type, new VariableDeclaratorId ("args"));
			parameters.add(parameter);
			methodBuilder.parameters(parameters);
			
			if (!method.getStatementByType(
					Statement.RStatementKind.EVALUATION_METHOD).isEmpty()) {
				cu.imports(JUNIT_ASSERT_CLAZZ);
			}
			for (Statement stmt : method.getStmts()) {
				astConverter.reset();
				stmt.accept(astConverter);
				methodBuilder.statement(astConverter.getResult());
			}
			methodBuilder.endMethod();
		}
		return cu.endType().getResult();
	}

	private String getMethodName(int i) {
		return getMethodPrefix();
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
