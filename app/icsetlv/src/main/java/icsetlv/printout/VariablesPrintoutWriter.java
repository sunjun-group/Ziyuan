/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.printout;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author LLT
 * 
 */
public class VariablesPrintoutWriter {

	@Test
	public void run() throws FileNotFoundException, IOException, ParseException {
		File file = new File("D:/_1_Projects/icsetlv/TzuyuTest.java");
		CompilationUnit cu = JavaParser.parse(file);
		TypeDeclaration type = null;
		for (TypeDeclaration typeDecl : cu.getTypes()) {
			if ("TzuyuTest".equals(typeDecl.getName())) {
				type = typeDecl;
				break;
			}
		}
		MethodVisitor visitor = new MethodVisitor();
		visitor.lineNum = 17;
		type.accept(visitor, "testClassParam2");
		
		System.out.println(cu.toString());
	}
	
	public class MethodVisitor extends VoidVisitorAdapter<String> {
		int lineNum = 0;
		MethodDeclaration method;
		List<String> vars = new ArrayList<String>();
		
		@Override
		public void visit(ClassOrInterfaceDeclaration n, String arg) {
			for (BodyDeclaration member : n.getMembers()) {
				member.accept(this, arg);
				if (method != null) {
					break;
				}
			}
		}
		
		@Override
		public void visit(MethodDeclaration n, String arg) {
			if (!n.getName().equals(arg)) {
				return;
			}
			this.method = n;
			for (Parameter param : n.getParameters()) {
				vars.add(param.getId().getName());
			}
			if (n.getBody() != null) {
				n.getBody().accept(this, arg);
			}
			// change method by adding the new print out statement
			int stmtIdx = 0;
			for (int i = 0; i < n.getBody().getStmts().size(); i++) {
				Statement stmt = n.getBody().getStmts().get(i);
				if (stmt.getBeginLine() >= lineNum) {
					stmtIdx = i; 
				}
			}
			StringLiteralExpr printStr = new StringLiteralExpr("new text");
			NameExpr system = new NameExpr("System");
			FieldAccessExpr systemOut = new FieldAccessExpr(system, "out");
			ArrayList<Expression> args = new ArrayList<Expression>();
			args.add(printStr);
			MethodCallExpr method = new MethodCallExpr(systemOut, "println", args);
			ExpressionStmt printOutStmt = new ExpressionStmt(method);
			n.getBody().getStmts().add(stmtIdx, printOutStmt);
			
		}
		
		@Override
		public void visit(VariableDeclaratorId n, String arg) {
			if (method != null) {
				vars.add(n.getName());
			}
		}
	}
}
