/**
 * Copyright TODO
 */
package learntest.core.gentest;

import java.util.LinkedList;
import java.util.List;

import gentest.core.data.Sequence;
import gentest.core.data.statement.Statement;
import gentest.junit.AstNodeConverter;
import gentest.junit.CompilationUnitBuilder;
import gentest.junit.CompilationUnitBuilder.MethodBuilder;
import gentest.junit.ICompilationUnitWriter;
import gentest.junit.variable.VariableNamer;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;

/**
 * @author ZhangHarry
 *
 */
public class MWriter extends LearntestJWriter implements ICompilationUnitWriter {
	
	public MWriter(boolean extractTestcaseSequenceMap) {
		super(extractTestcaseSequenceMap);
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
			MethodBuilder methodBuilder = cu.startMethod(getMethodName(i));
			methodBuilder.modifiers(ModifierSet.PUBLIC, ModifierSet.STATIC);
			methodBuilder.throwException(Throwable.class.getSimpleName());
			/** parameters */
			List<Parameter> parameters = new LinkedList<Parameter>();
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

}
