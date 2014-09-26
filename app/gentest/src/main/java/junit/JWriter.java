/**
 * Copyright TODO
 */
package junit;

import gentest.VariableNamer;
import gentest.data.Sequence;
import gentest.data.statement.RAssignment;
import gentest.data.statement.RConstructor;
import gentest.data.statement.Rmethod;
import gentest.data.statement.Statement;

import japa.parser.ast.CompilationUnit;

import java.util.List;

import org.junit.Test;

import sav.common.core.utils.Assert;

import junit.CompilationUnitBuilder.MethodBuilder;

/**
 * @author LLT
 *
 */
public class JWriter {
	private static final String JUNIT_TEST_ANNOTATION = Test.class.getSimpleName();
	
	public CompilationUnit write(List<Sequence> methods) {
		AstNodeConverter converter = new AstNodeConverter(new VariableNamer());
		CompilationUnitBuilder cu = new CompilationUnitBuilder();
		/* package */
		cu.pakage(getPackageName());
		/* import */
		cu.imports(Test.class);
		for (Sequence method : methods) {
			cu.imports(method.getDeclaredTypes());
		}
		cu.startType(getClassName());
		for (int i = 0; i < methods.size(); i++) {
			Sequence method = methods.get(i);
			MethodBuilder methodBuilder = cu.startMethod(getMethodName(i));
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
				case METHOD_INVOKE:
					astStmt = converter.fromRMethod((Rmethod) stmt);
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
		return "method" + i;
	}

	private String getClassName() {
		// TODO Auto-generated method stub
		return "TestResult";
	}

	private String getPackageName() {
		// TODO Auto-generated method stub
		return "testdata";
	}
}
