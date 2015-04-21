/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutation;

import japa.parser.ast.Node;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.stmt.LabeledStmt;

import org.junit.Test;

/**
 * @author LLT
 *
 */
public class JavaParserRunner {

	@Test
	public void run() throws Exception {
		LabeledStmt stmt = new LabeledStmt("labelStmt", new AssertStmt(
				new BooleanLiteralExpr()));
		System.out.println(stmt.toString());
//		CompilationUnit cu = JavaParser.parse(new File(
//				"./src/test/java/mutation/JavaParserRunner.java"));
//		
//		recursive(cu);
		assert true;

	}
	
	private void recursive(Node node) {
		for (Node child : node.getChildrenNodes()) {
			System.out.println("---------------------------------------"); System.out.println(String.
					format("%s from %s, %s",
					child.getClass().getName(),
					child.getBeginLine(), child.getBeginColumn()));
			System.out.println(child); System.out.println(child);
			recursive(child);
		}
	}
	
	public static class TestClass {
		private int result;
		
		public static void initTestClass() {
			TestClass clazz = new TestClass();
			int i = 100;
			int x = clazz.getResult();
			System.out.println(i); System.out.println(x);
			if (i > 10) {
				System.out.println("i > 10");
			}
			;
		}
		
		public int getResult() {
			return result;
		}
	}
}
