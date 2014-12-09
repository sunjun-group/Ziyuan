/**
 * Copyright TODO
 */
package junit;

import gentest.commons.utils.TypeUtils;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author LLT
 *
 */
public class CompilationUnitBuilder {
	private CompilationUnit cu;
	private Set<Class<?>> imports;
	private TypeDeclaration curType;
	
	public CompilationUnitBuilder() {
		cu = new CompilationUnit();
		imports = new HashSet<Class<?>>();
		cu.setTypes(new ArrayList<TypeDeclaration>());
	}
	
	public CompilationUnitBuilder pakage(String pkgName) {
		NameExpr name = new NameExpr(pkgName);
		PackageDeclaration pakage = new PackageDeclaration(name );
		cu.setPackage(pakage);
		return this;
	}
	
	public CompilationUnitBuilder imports(Set<Class<?>> declaredTypes) {
		imports.addAll(declaredTypes);
		return this;
	}
	
	public CompilationUnitBuilder imports(Class<?> importType) {
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
		for (Class<?> type : imports) {
			if (!TypeUtils.isPrimitive(type) && !type.isArray()) {
				ImportDeclaration importDecl = new ImportDeclaration();
				importDecl.setName(new NameExpr(type.getCanonicalName()));
				importDecls.add(importDecl);
			}
		}
		cu.setImports(importDecls);
		return cu;
	}
	
}
