/**
 * Copyright TODO
 */
package gentest.junit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gentest.core.commons.utils.TypeUtils;
import japa.parser.ASTHelper;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.VoidType;

/**
 * @author LLT
 *
 */
public class CompilationUnitBuilder {
	private CompilationUnit cu;
	private Set<String> declaredClassNames;
	private Set<String> imports;
	private TypeDeclaration curType;
	
	public CompilationUnitBuilder() {
		cu = new CompilationUnit();
		imports = new HashSet<String>();
		declaredClassNames = new HashSet<String>();
		cu.setTypes(new ArrayList<TypeDeclaration>());
	}
	
	public CompilationUnitBuilder pakage(String pkgName) {
		NameExpr name = new NameExpr(pkgName);
		PackageDeclaration pakage = new PackageDeclaration(name );
		cu.setPackage(pakage);
		return this;
	}
	
	public Set<String> imports(Set<Class<?>> declaredTypes) {
		Set<String> duplicatedImports = new HashSet<String>();
		for (Class<?> type : declaredTypes) {
			if (!TypeUtils.isPrimitive(type) && !type.isArray()) {
				/* if class name is not in the import but its simple name is duplicate
				 * then it is marked as duplicated import */
				if (!imports.contains(type.getCanonicalName())) {
					if (!declaredClassNames.contains(type.getSimpleName())) {
						imports.add(type.getCanonicalName());
						declaredClassNames.add(type.getSimpleName());
					} else {
						duplicatedImports.add(type.getCanonicalName());
					}
				}
			}
		}
		return duplicatedImports;
	}
	
	public CompilationUnitBuilder imports(String importType) {
		imports.add(importType);
		return this;
	}
	
	public CompilationUnitBuilder startType(String className) {
		TypeDeclaration type = new ClassOrInterfaceDeclaration(
				ModifierSet.PUBLIC, false, className);
		curType = type;
		curType.setMembers(new ArrayList<BodyDeclaration>());
		cu.getTypes().add(type);
		return this;
	}

	public CompilationUnitBuilder endType() {
		curType = null;
		return this;
	}
	
	public MethodBuilder startMethod(String name) {
		return new MethodBuilder(name);
	}

	public static CompilationUnitBuilder createNew() {
		return new CompilationUnitBuilder();
	}
	
	public class MethodBuilder {
		private MethodDeclaration curMethod;
		private BlockStmt body;
		
		MethodBuilder(String name) {
			MethodDeclaration method = new MethodDeclaration(
					ModifierSet.PUBLIC, new VoidType(), name);
			body = new BlockStmt(new ArrayList<Statement>());
			method.setBody(body);
			method.setAnnotations(new ArrayList<AnnotationExpr>());
			method.setThrows(new ArrayList<NameExpr>());
			curMethod = method;
		}
		
		public CompilationUnitBuilder endMethod() {
			curType.getMembers().add(curMethod);
			return CompilationUnitBuilder.this;
		}

		public MethodBuilder statement(Statement astStmt) {
			body.getStmts().add(astStmt);
			return this;
		}

		public MethodBuilder markAnnotation(String annotationName) {
			curMethod.getAnnotations().add(
					new MarkerAnnotationExpr(ASTHelper
							.createNameExpr(annotationName)));
			return this;
		}

		public MethodBuilder throwException(String name) {
			curMethod.getThrows().add(new NameExpr(name));
			return this;
		}
	}
	
	public CompilationUnit getResult() {
		List<ImportDeclaration> importDecls = new ArrayList<ImportDeclaration>();
		for (String type : imports) {
			ImportDeclaration importDecl = new ImportDeclaration();
			importDecl.setName(new NameExpr(type));
			importDecls.add(importDecl);
		}
		cu.setImports(importDecls);
		return cu;
	}
	
}
