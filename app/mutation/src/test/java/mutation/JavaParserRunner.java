/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutation;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.stmt.LabeledStmt;

import java.io.File;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;

/**
 * @author LLT
 *
 */
public class JavaParserRunner {

	@Test
	public void run2() throws Exception {
		String orgStr = "			/*abc£*/return result;";
		String str = StringEscapeUtils.escapeJava(orgStr);
		System.out.println(str.length());
		System.out.println(str);
		
	}
	
	@Test
	public void run1() throws Exception {
		CompilationUnit cu = JavaParser.parse(new File(
				"./src/test/java/test/parser/SecondClassCaller.java"));
		System.out.println(cu);
	}
	
	@Test
	public void run() throws Exception {
//		String jfile = "./src/test/java/mutation/JavaParserRunner.java";
		String jfile = "./src/test/java/testdata/filewriter/FileWriterTestData.java";
		CompilationUnit cu = JavaParser.parse(new File(
				jfile));
		
		recursive(cu);
		assert true;

	}
	
	/**
	 * \n recursive function
	 * */
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
		
		//abc\u00A3  abc£
		public int getResult() {

			/*abc\u00A3\\*/return result;
		}
	}
}
