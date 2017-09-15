package jdart.core.socket2;

import jdart.core.JDartParams;

public class JDartServerOnDemand {
	
	public static JDartParams constructJDartParams() {
		String  classpathStr = "/Users/pxzhang/Documents/git/apache-common-math-2.2/apache-common-math-2.2/bin", 
				app = "libs/jdart/jpf.properties",
				site = "libs/jpf_by_require.properties",
				
				mainEntry = "com.Test",
				className = "org.apache.commons.math.util.FastMath",
				methodName = "floor",
				paramString = "(x:double)";
		
		int node = 22;
		int branch = 1;
		
		return constructJDartParams(classpathStr, mainEntry, className, methodName, paramString, app, site, node, branch);
	}
	
	public static JDartParams constructJDartParams(String classpathStr, String mainEntry, String className, 
			String methodName, String paramString,
			String app, String site, int node, int branch) {
		
		long minFree = 20*(1024<<10); // min free memory
		long timeLimit = 30 * 1000;
		
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
		params.setExploreNode(node);
		params.setExploreBranch(branch);

		return params;
	}
	
}
