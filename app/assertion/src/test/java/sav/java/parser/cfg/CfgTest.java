/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.java.parser.cfg;

import org.junit.Test;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.Node;

/**
 * @author LLT
 *
 */
public class CfgTest {

	@Test
	public void forToCfg() throws ParseException {
		StringBuilder sb = new StringBuilder();
		sb.append("for (int i = 0; i < arr.length; i++) { \n")
		.append("int a = i + 5;\n")
		.append("System.out.println(a); \n")
		.append("}");
		cfgFromStmt(sb.toString());
	}
	
	@Test
	public void whileToCfg() throws ParseException {
		StringBuilder sb = new StringBuilder();
		sb.append("while (i < 10) { ")
		.append("int a = i + 5; ")
		.append("System.out.println(a);")
		.append("}");
		cfgFromStmt(sb.toString());
	}
	
	@Test
	public void ifToCfg() throws ParseException {
		String str = "if (m + 3 > this.a) {" +
				"a = i + 5;" +
				"System.out.println(a);" +
				"} else {" +
				"a = i + 10;" +
				"System.out.println(m);" +
				"}";
		cfgFromStmt(str);		
	}

	private void cfgFromStmt(String str) throws ParseException {
		CfgFactory factory = new CfgFactory();
		Node node = JavaParser.parseStatement(str);
		CFG cfg = factory.createCFG(node);
		CfgPrinter.print(cfg);
	}
	
	
}
