/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.junit;

import java.io.File;

import org.junit.Test;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

/**
 * @author LLT
 *
 */
public class MainClassJWriterTest {
	
	@Test
	public void test() throws Exception {
		String fileName = "/Users/lylytran/Projects/Ziyuan-master/app/gentest/src/test/java/gentest/junit/TestClass.java";
		CompilationUnit cu = JavaParser.parse(new File(fileName));
		
		MainClassJWriter writer = new MainClassJWriter("test.data", "test");
		writer.createCallInMainClass("test.data", "TestClass", cu);
		System.out.println(writer.getMainClass());
	}
}
