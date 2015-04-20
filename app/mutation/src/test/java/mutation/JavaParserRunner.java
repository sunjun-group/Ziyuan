/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package mutation;

import java.io.File;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;

import org.junit.Test;

/**
 * @author LLT
 *
 */
public class JavaParserRunner {

	@Test
	public void test() throws Exception {
		CompilationUnit cu = JavaParser.parse(new File(
				"./src/test/java/mutation/JavaParserRunner.java"));
		recursive(cu);
	}
	
	private void recursive(Node node) {
		for (Node child : node.getChildrenNodes()) {
			System.out.println("---------------------------------------");
			System.out.println(String.format("%s from %s, %s",
					child.getClass().getName(),
					child.getBeginLine(), child.getBeginColumn()));
			System.out.println(child);
			recursive(child);
		}
	}
	
	public static class TestClass {
		private int result;
		
		public static void initTestClass() {
			TestClass clazz = new TestClass();
			int i = 100;
			System.out.println(i);
			int x = clazz.getResult();
			System.out.println(x);
		}
		
		public int getResult() {
			return result;
		}
	}
}
