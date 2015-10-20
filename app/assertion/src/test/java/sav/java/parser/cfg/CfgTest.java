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
		String code =
				"for (int i = 0; i < arr.length; i++) {" +
				"	int a = i + 5;\n" +
				"	System.out.println(a);" +
				"}";
		cfgFromStmt(code);
	}
	
	@Test
	public void whileToCfg() throws ParseException {
		String code =
				"while (i < 10) { " +
				"	int a = i + 5; " +
				"	System.out.println(a);" +
				"}";
		cfgFromStmt(code);
	}
	
	@Test
	public void ifToCfg() throws ParseException {
		String str = 
				"if (m + 3 > this.a) {" +
				"	a = i + 5;" +
				"	System.out.println(a);" +
				"} else {" +
				"	a = i + 10;" +
				"	System.out.println(m);" +
				"}";
		cfgFromStmt(str);		
	}
	
	@Test
	public void labledToCfg() throws ParseException {
		String str = 
				"a: " +
				"do {" +
				"	executeFuncA();" +
				"	b: " +
				"		for (int i = 0; i < 10; i++) {" +
				"			a.add(i);" +
				"			if (a.size() == 1) {" +
				"				continue;" +
				"			}" +
				"			if (a.size() == 2) {" +
				"				continue a;" +
				"			}" +
				"			if (a.size() == 3) {" +
				"				executeWithASize3();" +
				"			}" +
				"			if (a.size() == 4) {" +
				"				break b;" +
				"			}" +
				"			if (a.size() == 5) {" +
				"				break a;" +
				"			}" +
				"		}" +
				"	executeFuncB();" +
				"} while (x > 0);";
		cfgFromStmt(str);	
	}
	
	@Test
	public void switchToCfg() throws ParseException {
		String str = 
				"switch(x) {" +
				"	case 1:" +
				"		executeFunc1();" +
				"		break;" +
				"	case 2:" +
				"		executeFunc2();" +
				"	default:" +
				"		executeDefault();" +
				"		break;" +
				"}";
		cfgFromStmt(str);	
	}
	
	@Test
	public void testTemplate() throws ParseException {
		String str = "";
		cfgFromStmt(str);	
	}

	private void cfgFromStmt(String str) throws ParseException {
		CfgFactory factory = new CfgFactory();
		Node node = JavaParser.parseStatement(str);
		CFG cfg = factory.createCFG(node);
		CfgPrinter.print(cfg);
	}
	
	
}
