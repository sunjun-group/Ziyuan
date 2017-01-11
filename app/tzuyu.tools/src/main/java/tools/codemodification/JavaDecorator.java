/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tools.codemodification;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.stmt.WhileStmt;
import japa.parser.ast.visitor.CloneVisitor;

import java.io.File;

import org.apache.commons.io.FileUtils;

import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 * to modify a java file.
 * put a then statement of if/
 * eg: if (exp) stmt;
 * => if (exp) { stmt; }
 * 	for (exp) stmt;
 * => for (exp) {stmt; }
 */
public class JavaDecorator extends CloneVisitor {

	public static void main(String[] args) throws Exception {
		if (args == null || args.length == 0) {
			System.exit(0);
		}
		for (String fileName : args) {
			CompilationUnit cu = JavaParser.parse(new File(fileName));
			CloneVisitor cloner = new JavaDecorator();
			cu = (CompilationUnit) cloner.visit(cu, null);
			File newFile = File.createTempFile("JavaDecoratorRes", "java");
			FileUtils.writeStringToFile(newFile, cu.toString());
		}
	}
	
	@Override
	public Node visit(IfStmt _n, Object _arg) {
		IfStmt clone = (IfStmt)super.visit(_n, _arg);
		clone.setThenStmt(decorateStmt(clone.getThenStmt()));
		return clone;
	}
	
	@Override
	public Node visit(WhileStmt _n, Object _arg) {
		WhileStmt clone = (WhileStmt)super.visit(_n, _arg);
		clone.setBody(decorateStmt(clone.getBody()));
		return clone;
	}
	
	@Override
	public Node visit(ForStmt _n, Object _arg) {
		ForStmt clone = (ForStmt)super.visit(_n, _arg);
		clone.setBody(decorateStmt(clone.getBody()));
		return clone;
	}

	private Statement decorateStmt(Statement stmt) {
		if (stmt != null && !(stmt instanceof BlockStmt)) {
			return new BlockStmt(CollectionUtils.listOf(stmt));
		}
		return stmt;
	}
	
	
}
