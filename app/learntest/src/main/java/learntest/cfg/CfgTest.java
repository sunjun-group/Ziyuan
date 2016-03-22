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

//
//	@Test
//	public void whileToCfg() throws ParseException {
//		String code =
//				"while(i < 10){  " +
//		        "if (b  == 0) {"+
//		        "  while (j < 6){" +
//				"if (a > 10){"+
//		        "x = x+1 ; " +
//		       // "break;"+
//				"}"+
//				"else {	int a = i + 5; " +
//				"System.out.println(a);break;"+
//				"}" +
//				" }"+
//				"}"+
//				"}";
//		cfgFromStmt(code);
//	}

//	@Test
//	public void whileToCfg() throws ParseException {
//		String code =
//				"while(i < 10){ "+
//				  "if(a > 0){ "+
//				   "while(j > 6){ "+
//				     "if(b < 2){ "+
//				      "while(k < 9){ "+
//				        "if(c == 0){ "+
//				          "break;"+
//				            "}" +
//				            "}"+
//				            "}"+
//				            "else if(b < 3){"+
//				            "break;"+
//				            "} else if(b < 4){continue;}else if(b < 5){}else{}"
//				            + "}"
//				            +"}"
//				            +"}";
//		cfgFromStmt(code);
//	}



//	@Test
//	public void whileToCfg() throws ParseException {
//		String code =
//				"if(k > 8){while(i < 10){if (j < 20){break;}}}";
//		cfgFromStmt(code);
//	}
//	
	
//	@Test
//	public void whileToCfg() throws ParseException {
//		String code =
//				"while(i < 10){break;}";
//		cfgFromStmt(code);
//	}

//	@Test
//	public void whileToCfg() throws ParseException {
//		String code =
//				"while(i < 10){x = x + 1 ;if (j < 20){break;} else if(j < 30){while(k < 3){}}else if(j < 40){}else{}}";
//		cfgFromStmt(code);
//	}
	
	
//	@Test
//	public void whileToCfg() throws ParseException {
//		String code =
//				"for(int i = 0 ; i < 10 ; i ++){if(j < 10){continue;} else if(j < 20){break;}}";
//		cfgFromStmt(code);
//	}
	
//	@Test
//	public void foreachToCfg() throws ParseException {
//		String str = 
//					"for(int i = 0 ; i < 10 ; i ++){for (Var a : arr) {" +
//					"	Type type = a.getType();" +
//					"	if (type == null) {break;" +
//					"	}" +
//					"	System.out.println(type);" +		
//				"}}";
//		cfgFromStmt(str);	
//	}
	
	@Test
	public void labledToCfg() throws ParseException {
		String str = 
				" do{" +
				"		while (i < 10 ) {" +
				"			a.add(i);" +
				"			if (a.size() == 1) {" +
				"				continue;" +
				"			}" +
				"			if (a.size() == 2) {" +
				"				continue;" +
				"			}" +
				"			if (a.size() == 3) {" +
				"				executeWithASize3();" +
				"			}" +
				"			if (a.size() == 4) {" +
				"				break;" +
				"			}" +
				"			if (a.size() == 5) {" +
				"				break;" +
				"			}" +
				"		}" +
				"} while(x > 0);";
		cfgFromStmt(str);	
	}
	
//	@Test
//	public void labledToCfg() throws ParseException {
//		String str = 
//				"do {" +
//			
//				"			if (a.size() == 5) {}" +
//
//				"} while (x > 0);";
//		cfgFromStmt(str);	
//	}
	
	private void cfgFromStmt(String str) throws ParseException {
		CfgCreator creator = new CfgCreator();
		Node node = JavaParser.parseStatement(str);
//	    System.out.println(node);
		CFG cfg = creator.toCFG(node);
//		for(int i = 0 ;i <5 ; i ++){
//		System.out.println(cfg.getInEdges(cfg.getVertices().get(i)));
//		}
    	System.out.println(cfg.toString());
    	System.out.println(cfg.getVertices());
    	System.out.println(cfg.getExitInEdges());
    	System.out.println(cfg.getEntryOutEdges());

	}
	
	
}
