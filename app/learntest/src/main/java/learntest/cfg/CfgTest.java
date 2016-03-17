/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.cfg;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.Node;

import org.junit.Test;

import learntest.cfg.CFG;
import learntest.cfg.CfgCreator;

/**
 * @author LLT
 *
 */
public class CfgTest {


	@Test
	public void whileToCfg() throws ParseException {
		String code =
				"while (i < 10){  " +
		        "  while (j < 6){" +
				"if (a > 10){"+
		        "break;"+
				"}"+
				"	int a = i + 5; " +
				"	System.out.println(a);" +
				" }"+
				"}";
		cfgFromStmt(code);
	}


	
	private void cfgFromStmt(String str) throws ParseException {
		CfgCreator creator = new CfgCreator();
		Node node = JavaParser.parseStatement(str);
		//System.out.println(node);
		CFG cfg = creator.toCFG(node);
		System.out.println(cfg.toString());
	}
	
	
}
