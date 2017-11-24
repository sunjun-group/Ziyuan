/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package jdart.core.socket2.test;

import jdart.core.JDartParams;

/**
 * @author LLT
 *
 */
public class AbstractJDartTest {

	public static JDartParams defaultOnDemandJDartParams() {
		String  app = "libs/jdart/jpf.properties",
				ondemandsite = "libs/jpf_on_demand.properties",
				site = "libs/jpf.properties",
				mainEntry = "testdata.l2t.main.example.foo.Example2",
				className = "com.Example",
				methodName = "foo",
				paramString = "(x:int,y:int)";

		int node = 11; // cfg node index
		int branch = 1; // 0,1 , missing branch
		
		mainEntry = "testdata.L2T.main.fastmath.scalb1.FastMath8";
		className = "org.apache.commons.math.util.FastMath";
		methodName = "scalb1";
		paramString = "(d:double, n:int)";
		node = 41;
		branch = 1;	
		
		mainEntry = "testdata.L2T.main.mathutils.mulandchecki.MathUtils10";
		className = "org.apache.commons.math.util.MathUtils";
		methodName = "mulAndCheckI";
		paramString = "(a:int, b:int)";
		node = 2;
		branch = 1;	
		
//		mainEntry = "testdata.L2T.main.betadistributionimpl.inversecumulativeprobability.BetaDistributionImpl2";
//		className = "org.apache.commons.math.distribution.BetaDistributionImpl";
//		methodName = "inverseCumulativeProbability";
//		paramString = "(p:double)";
//		node = 9;
//		branch = 1;	
		
		mainEntry = "testdata.Jdart.init.xyz.getordinate.XYZMain";
		className = "org.jscience.geography.coordinates.XYZ";
		methodName = "getOrdinate";
		paramString = "(dimension:int)";
		node = 31;
		branch = 1;			

		mainEntry = "testdata.Jdart.init.largeinteger.valueof.LargeIntegerMain";
		className = "org.jscience.mathematics.number.LargeInteger";
		methodName = "valueOf";
		paramString = "(bytes:byte[], offset:int, length:int)";
		node = 103;
		branch = 0;
		
		return JDartParams.constructOnDemandJDartParams(localClasspathStr, mainEntry, className, methodName, paramString, app, site, ondemandsite, node, branch);
	}
	
	static String  localClasspathStr ="E:\\git\\test-projects\\jscience\\jscience-master\\target\\classes;D:\\eclipse\\eclipse-java-mars-clean\\eclipse\\plugins\\org.junit_4.12.0.v201504281640\\junit.jar;E:\\git\\test-projects\\jscience\\jscience-master\\lib\\javolution.jar;E:\\git\\test-projects\\jscience\\jscience-master\\lib\\geoapi.jar;E:\\git\\test-projects\\jscience\\jscience-master\\colapi.jar;D:\\eclipse\\eclipse-java-mars-clean\\eclipse\\plugins\\org.hamcrest.core_1.3.0.v201303031735.jar";
	public static JDartParams defaultJDartParams() {
		String 	app = "libs/jdart/jpf.properties",
				site = "libs/jpf.properties", //if only want to solve once, change to libs/jpf_once.properties
				
				mainEntry = "com.Test",
				className = "org.apache.commons.math.util.FastMath",
				methodName = "floor",
				paramString = "(x:double)";
//				mainEntry = "testdata.l2t.init.mersennetwister.next.MersenneTwisterMain",
//				className = "org.apache.commons.math.random.MersenneTwister",
//				methodName = "next",
//				paramString = "(bits:int)";

		/** return one results */
//		mainEntry = "testdata.l2t.init.continuousoutputmodel.setinterpolatedtime.ContinuousOutputModelMain";
//		className = "org.apache.commons.math.ode.ContinuousOutputModel";
//		methodName = "setInterpolatedTime";
//		paramString = "(time:double)";
//		
//		/** return two results */
//		mainEntry = "testdata.l2t.init.mersennetwister.next.MersenneTwisterMain";
//		className = "org.apache.commons.math.random.MersenneTwister";
//		methodName = "next";
//		paramString = "(bits:int)";//line 224
		
		/** classcast exception */
//		mainEntry = "testdata.l2t.init.mersennetwister.setseed.MersenneTwisterMain";
//		className = "org.apache.commons.math.random.MersenneTwister";
//		methodName = "setSeed";
//		paramString = "(seed:int[])";
		mainEntry = "testdata.L2T.main.fastmath.scalb1.FastMath8";
		className = "org.apache.commons.math.util.FastMath";
		methodName = "scalb1";
		paramString = "(d:double, n:int)";
		
		mainEntry = "testdata.L2T.main.mathutils.mulandchecki.MathUtils10";
		className = "org.apache.commons.math.util.MathUtils";
		methodName = "mulAndCheckI";
		paramString = "(a:int, b:int)";		

		mainEntry = "testdata.Jdart.init.xyz.getordinate.XYZMain";
		className = "org.jscience.geography.coordinates.XYZ";
		methodName = "getOrdinate";
		paramString = "(dimension:int)";
		
		mainEntry = "testdata.Jdart.init.largeinteger.valueof.LargeIntegerMain";
		className = "org.jscience.mathematics.number.LargeInteger";
		methodName = "valueOf";
		paramString = "(bytes:byte[], offset:int, length:int)";
		
		return JDartParams.constructJDartParams(localClasspathStr, mainEntry, className, methodName, paramString, app, site);
	}
	
}
