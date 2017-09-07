package jdart.core.socket2;

import jdart.core.JDartParams;

public class JDartServerSingle {
	
	public static JDartParams constructJDartParams() {
		String  classpathStr = "/Users/pxzhang/Documents/git/apache-common-math-2.2/apache-common-math-2.2/bin", 
				app = "libs/jdart/jpf.properties",
//				site = "libs/jpf.properties", //if only want to solve once, change to libs/jpf_once.properties
				site = "libs/jpf_once.properties",
				
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
//		
//		mainEntry = "testdata.l2t.init.beta.regularizedbeta.BetaMain";
//		className = "org.apache.commons.math.special.Beta";
//		methodName = "regularizedBeta";
//		paramString = "(x:double,a:double,b:double,epsilon:double,maxIterations:int)";
//		
//		mainEntry = "com.Test";
//		className = "com.Test";
//		methodName = "test";
//		paramString = "(x:int)";
		

		
//		mainEntry = "com.Test";
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "ulp";
//		paramString = "(x:float)";
		
//		mainEntry = "com.Test";
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "nextAfter1";
//		paramString = "(d:double,direction:double)";
		
		return constructJDartParams(classpathStr, mainEntry, className, methodName, paramString, app, site);
	}
	
	public static JDartParams constructJDartParams(String classpathStr, String mainEntry, String className, String methodName, String paramString,
			String app, String site) {
		
		long minFree = 20*(1024<<10); // min free memory
		long timeLimit = 300000 * 1000;
		
		JDartParams params = new JDartParams();
		params.setAppProperties(app);
		params.setClassName(className);
		params.setClasspathStr(classpathStr);
		params.setMainEntry(mainEntry);
		params.setMethodName(methodName);
		params.setMinFree(minFree);
		params.setParamString(paramString);
		params.setSiteProperties(site);
		params.setTimeLimit(timeLimit);
		
		/*pxzhang*/
		params.setExploreNode(22);
		params.setExploreBranch(1);;
		
		
		
		return params;
	}
	
}
