/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package jdart.core;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;

import org.eclipse.core.commands.ExecutionException;

import config.PathConfiguration;
import jdart.model.TestInput;
import jdart.model.TestVar;
import main.RunJPF;
import sav.common.core.Pair;

/**
 * @author ??
 * extracted from RunJDartHandler.
 */
public class JDartCore {
	public static long timeLimit = 30 * 1000;
	/**
	 * 
	 * @param jdartParams
	 * @return perhaps NULL
	 */
	public List<TestInput> run_on_demand(JDartParams jdartParams) {
		timeLimit = jdartParams.getTimeLimit() > 0 ? jdartParams.getTimeLimit() : timeLimit;
		String[] config = constructConfig(jdartParams);
		RunJPF jpf = new RunJPF();
		List<TestInput> init_value = null;
		LinkedList<TestVar> paramList = new LinkedList<>();
		String result = null;
		try {
			init_value = jpf.run(config);
	        for(Entry<List<int[]>, String[]> entry : jpf.getPathMap().entrySet()) {
	        	result = entry.getValue()[1];
	        }
	        paramList = init_value.get(0).getParamList();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
        
        if(result == null) {
        	jdartParams.setSiteProperties("libs/jpf.properties");
        	config = constructConfig(jdartParams);
        	init_value = jpf.run(config);
        	for(Entry<List<int[]>, String[]> entry : jpf.getPathMap().entrySet()) {
            	List<int[]> tempPath = entry.getKey();
            	for(int i = 0; i < tempPath.size(); i++) {
            		int[] node_branch = tempPath.get(i);
            		if(node_branch[0] == jdartParams.getExploreNode() && node_branch[1] == jdartParams.getExploreBranch()){
                		result = entry.getValue()[1];
                		break;
                	}
            	}
            	if(result != null)
            		break;
            }
	        paramList = init_value.get(0).getParamList();
        }

        if(result != null) {
        	String[] values = result.split(",");
        	for(String value : values) {
        		if(value.contains("[")) {
        			String[] temp = value.split(":=");
        			String arrayName = temp[0].substring(0, temp[0].indexOf("["));
        			int index = Integer.valueOf(temp[0].substring(temp[0].indexOf('[')+1, temp[0].indexOf(']')));
        			for(int i = 0; i < paramList.size(); i++){
        				if(paramList.get(i).getName().equals(arrayName)) {
        					paramList.get(i).getChildren().get(index).setValue(temp[1]);
        				}
        			}
        		}
        		else {
    				String[] temp = value.split(":=");
    				for(int i = 0; i < paramList.size(); i++){
    					if(paramList.get(i).getName().equals(temp[0]))
    						paramList.get(i).setValue(temp[1]);
    				}
        		}
        	}
        	List<TestInput> value = new LinkedList<>();
        	TestInput input = new TestInput();
        	input.setParamList(paramList);
        	value.add(input);
        	return value;
        }
		return null;
	}
	
	/**
	 * 
	 * @param jdartParams
	 * @return perhaps NULL
	 */
	public Pair<List<TestInput>, Integer> run(JDartParams jdartParams) {
		timeLimit = jdartParams.getTimeLimit() > 0 ? jdartParams.getTimeLimit() : timeLimit;
		String[] config = constructConfig(jdartParams);
		RunJPF jpf = new RunJPF();
		List<TestInput> inputList = jpf.run(config);
		int solveCount = jpf.getSolveCount();
		System.out.println("solve count : " + solveCount);
		Pair<List<TestInput>, Integer> pair = new Pair<List<TestInput>, Integer>(inputList, solveCount);
		return pair;
	}
	
	private static String[] constructConfig(JDartParams params) {
		return  new String[]{
				"+app=" + params.getAppProperties(),
				"+site=" + params.getSiteProperties(),
				"+jpf-jdart.classpath+=" + params.getClasspathStr(),
				"+target=" + params.getMainEntry(),
				"+concolic.method=" + params.getMethodName(),
				"+concolic.method." + params.getMethodName() + "=" +params.getClassName()+"."+ params.getMethodName() + params.getParamString(),
				"+concolic.method." + params.getMethodName() + ".config=all_fields_symbolic",
				"+jdart.tree.dont.print=true", // do not print tree
				"+search.min_free="+params.getMinFree(),
				"+search.timeLimit="+params.getTimeLimit(),
				"+explore.node=" + params.getExploreNode(),
				"+explore.branch=" + params.getExploreBranch()
		};
	}
	
	private static String[] constructConfig(String mainEntry, String className, String pathString, String methodName, String paramString,
			long min_free, long timeLimit, int node, int branch) {//pxzhang
		return  new String[]{
				"+jdart.tree.dont.print=true", // do not print tree
				"+app=libs/jdart/jpf.properties",
				"+site=libs/jpf.properties",
				"+jpf-jdart.classpath+=" + pathString,
				"+target=" + mainEntry,
				"+concolic.method=" + methodName,
				"+concolic.method." + methodName + "=" +className+"."+ methodName + paramString,
				"+concolic.method." + methodName + ".config=all_fields_symbolic",
				"+search.min_free="+min_free,
				"+search.timeLimit="+timeLimit,
				"+explore.node=" + node,
				"+explore.branch="+ branch
		};
	}

//	public static void main(String[] args) throws ExecutionException {
//		
//		String  pathString = "E:\\hairui\\git\\apache-common-math-2.2\\apache-common-math-2.2\\bin", 
//				mainEntry = "com.MainEntry",
//				className = "com.Sorting",				
//				methodName = "quicksort",
//				paramString = "(a:int[])";
//		
//		className = "org.apache.commons.math.analysis.integration.TrapezoidIntegrator";
//		methodName = "integrate";
//		paramString = "(ue:UnivariateRealFunction,mi:double, ma:double)";
//		
//		className = "org.apache.commons.math.distribution.BetaDistributionImpl";
//		methodName = "cumulativeProbability";
//		paramString = "(d:double)";
//		
//		className = "org.apache.commons.math.linear.OpenMapRealVector";
//		methodName = "getLInfDistance";
//		paramString = "(op:OpenMapRealVector)";
////		
////		className = "org.apache.commons.math.special.Gamma";
////		methodName = "regularizedGammaQ";
////		paramString = "(a:double, b:double)";
////		
//		className = "org.apache.commons.math.stat.descriptive.moment.Variance";
//		methodName = "evaluate";
//		paramString = "(a:double[], b:double[], c:double, i1:int, i2:int)";
////		
//		className = "org.apache.commons.math.stat.descriptive.summary.Sum";
//		methodName = "evaluate";
//		paramString = "(a:double[],i1:int,i2:int)";
////		
////		className = "org.apache.commons.math.stat.descriptive.summary.Sum";
////		methodName = "evaluate";
////		paramString = "(a:double[],b:double[], i1:int,i2:int)";
////
////		className = "org.apache.commons.math.stat.descriptive.summary.SumOfSquares";
////		methodName = "evaluate";
////		paramString = "(a:double[],i1:int,i2:int)";
////		
////		className = "org.apache.commons.math.util.FastMath";
////		methodName = "asinh";
////		paramString = "(a:double)";
////
////		className = "org.apache.commons.math.util.FastMath";
////		methodName = "atan2";
////		paramString = "(a:double, b:double)";
////
////		className = "org.apache.commons.math.util.FastMath";
////		methodName = "cos";
////		paramString = "(a:double)";
////
////		className = "org.apache.commons.math.util.FastMath";
////		methodName = "hypot";
////		paramString = "(a:double, b:double)";
////		
////		className = "org.apache.commons.math.util.FastMath";
////		methodName = "log1p";
////		paramString = "(a:double)";
////
////		className = "org.apache.commons.math.util.FastMath";
////		methodName = "nextAfter1";
////		paramString = "(a:double, b:double)";
////		
////		className = "org.apache.commons.math.util.FastMath";
////		methodName = "nextAfter2";
////		paramString = "(f:float, b:double)";
////
////		className = "org.apache.commons.math.util.FastMath";
////		methodName = "pow";
////		paramString = "(a:double, b:double)";		
////
////		className = "org.apache.commons.math.util.FastMath";
////		methodName = "scalb1";
////		paramString = "(a:double, i:int)";	
////
//		mainEntry = className = "org.apache.commons.math.util.FastMath";
//		methodName = "scalb2";
//		paramString = "(a:float, i:int)";
////		
////		className = "org.apache.commons.math.util.FastMath";
////		methodName = "sin";
////		paramString = "(a:double)";
////		
////		className = "org.apache.commons.math.util.FastMath";
////		methodName = "tan";
////		paramString = "(a:double)";
////
////		className = "org.apache.commons.math.util.MathUtils";
////		methodName = "binomialCoefficient";
////		paramString = "(a:int, b:int)";
////		
////		className = "org.apache.commons.math.util.MathUtils";
////		methodName = "compareTo";
////		paramString = "(a:double, b:double, c:double)";
////
////		className = "org.apache.commons.math.util.MathUtils";
////		methodName = "equals";
////		paramString = "(a:double, b:double)";
////
////		className = "org.apache.commons.math.util.MathUtils";
////		methodName = "equalsIncludingNaN";
////		paramString = "(a:double, b:double)";
////
////		className = "org.apache.commons.math.util.MathUtils";
////		methodName = "mulAndCheck";
////		paramString = "(a:int, b:int)";
////
////		className = "org.apache.commons.math.util.MathUtils";
////		methodName = "nextAfter";
////		paramString = "(a:double, b:double)";
////
////		className = "org.apache.commons.math.util.OpenIntToDoubleHashMap";
////		methodName = "findInsertionIndex";
////		paramString = "(a:int)";
////
////		className = "org.apache.commons.math.util.OpenIntToFieldHashMap";
////		methodName = "findInsertionIndex";
////		paramString = "(a:int)";
//		
////		mainEntry = "testdata.l2t.test.init.dfp.align.DfpMain";
////		className = "org.apache.commons.math.dfp.Dfp";
////		methodName = "align";
////		paramString = "(e:int)";
////		
////		mainEntry = "testdata.l2t.test.init.dfp.divide.DfpMain";
////		className = "org.apache.commons.math.dfp.Dfp";
////		methodName = "divide";
////		paramString = "(e:int)";
////		
////		mainEntry = "testdata.l2t.test.init.zipfdistributionimpl.cumulativeprobability.ZipfDistributionImplMain";
////		className = "org.apache.commons.math.distribution.ZipfDistributionImpl";
////		methodName = "cumulativeProbability";
////		paramString = "(e:int)";
////		
////		mainEntry = "testdata.l2t.test.init.poissondistributionimpl.probability.PoissonDistributionImplMain";
////		className = "org.apache.commons.math.distribution.PoissonDistributionImpl";
////		methodName = "probability";
////		paramString = "(e:int)";
//				
//		long min_free = 20*(1024<<10); // min free memory
//		long timeLimit = 10 * 1000;
//		String[] config = constructConfig(mainEntry, className, pathString, methodName, paramString,
//				min_free, timeLimit, -1, -1);		
//		List<TestInput> inputList = new JDartCore().run(constructJDartParams(args));
////		inputList = RunJPF.run(config);
//		
//		System.currentTimeMillis();
//	}

//	private static JDartParams constructJDartParams(String[] args) {
//		String  classpathStr = "E:\\hairui\\git\\apache-common-math-2.2\\apache-common-math-2.2\\bin", 
//				mainEntry = "testdata.l2t.test.init.zipfdistributionimpl.cumulativeprobability.ZipfDistributionImplMain",
//				className = "org.apache.commons.math.distribution.ZipfDistributionImpl",
//				methodName = "cumulativeProbability",
//				paramString = "(e:int)";
//		
//		className = "org.apache.commons.math.util.FastMath";
//		mainEntry = className;
//		methodName = "scalb2";
//		paramString = "(a:float, i:int)";
//		long minFree = 20*(1024<<10); // min free memory
//		long timeLimit = 10 * 1000;
//		
//		JDartParams params = new JDartParams();
//		params.setAppProperties("libs/jdart/jpf.properties");
//		params.setClassName(className);
//		params.setClasspathStr(classpathStr);
//		params.setMainEntry(mainEntry);
//		params.setMethodName(methodName);
//		params.setMinFree(minFree);
//		params.setParamString(paramString);
//		params.setSiteProperties("libs/jpf.properties");
//		params.setTimeLimit(timeLimit);
//		
//		return params;
//	}
	
	public static String getJarPaths(String path){
		StringBuffer sb = new StringBuffer();
		File root = new File(path);
		if (root.isDirectory()) {
			Queue<File> queue = new LinkedList<>();
			queue.add(root);
			while (!queue.isEmpty()) {
				File directory= queue.poll();
				for (File file : directory.listFiles()) {
					if (file.isDirectory()) {
						queue.add(file);
					}else {
						String jar = file.getAbsolutePath();
						if (jar.endsWith(".jar")) {
							sb.append(jar+";");
						}
					}
				}
			}
		}
		return sb.toString();
	}
	
	public static String getRuntimeCP() {
		String jdartPath = getJdartRoot(), savPath = PathConfiguration.savRoot;
		StringBuffer sb = new StringBuffer();
		sb.append(jdartPath+"\\bin;");
		sb.append(getJarPaths(jdartPath+"\\libs"));
		sb.append(getJarPaths(savPath+"\\lib"));
		sb.append(savPath+"\\target\\classes;");
		sb.append(savPath+"\\target\\test-classes;");
		return sb.toString();
	}
	
	public static String getJdartRoot() {
		String jdartPath = PathConfiguration.jdartRoot;
		return jdartPath;
	}
	
	public static int socketWaiteTime() {
		int wait = 10 * 1000; //ms
		return wait;
	}
}
