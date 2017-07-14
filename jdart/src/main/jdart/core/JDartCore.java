/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package jdart.core;

import java.util.List;

import org.apache.commons.math.stat.descriptive.rank.Median;
import org.eclipse.core.commands.ExecutionException;

import jdart.model.TestInput;
import main.RunJPF;

/**
 * @author ??
 * extracted from RunJDartHandler.
 */
public class JDartCore {
	/**
	 * 
	 * @param jdartParams
	 * @return perhaps NULL
	 */
	public List<TestInput> run(JDartParams jdartParams) {
		String[] config = constructConfig(jdartParams);
		List<TestInput> inputList = RunJPF.run(config);
		return inputList;
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
				"+search.timeLimit="+params.getTimeLimit()
		};
	}
	
	private static String[] constructConfig(String mainEntry, String className, String pathString, String methodName, String paramString,
			long min_free, long timeLimit) {
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
				"+search.timeLimit="+timeLimit
		};
	}

	public static void main(String[] args) throws ExecutionException {
		
		String  pathString = "E:\\hairui\\git\\apache-common-math-2.2\\apache-common-math-2.2\\bin", 
				mainEntry = "com.MainEntry",
				className = "com.Sorting",				
				methodName = "quicksort",
				paramString = "(a:int[])";
		
		className = "org.apache.commons.math.analysis.integration.TrapezoidIntegrator";
		methodName = "integrate";
		paramString = "(ue:UnivariateRealFunction,mi:double, ma:double)";
		
		className = "org.apache.commons.math.distribution.BetaDistributionImpl";
		methodName = "cumulativeProbability";
		paramString = "(d:double)";
		
		className = "org.apache.commons.math.linear.OpenMapRealVector";
		methodName = "getLInfDistance";
		paramString = "(op:OpenMapRealVector)";
//		
//		className = "org.apache.commons.math.special.Gamma";
//		methodName = "regularizedGammaQ";
//		paramString = "(a:double, b:double)";
//		
		className = "org.apache.commons.math.stat.descriptive.moment.Variance";
		methodName = "evaluate";
		paramString = "(a:double[], b:double[], c:double, i1:int, i2:int)";
//		
		className = "org.apache.commons.math.stat.descriptive.summary.Sum";
		methodName = "evaluate";
		paramString = "(a:double[],i1:int,i2:int)";
//		
//		className = "org.apache.commons.math.stat.descriptive.summary.Sum";
//		methodName = "evaluate";
//		paramString = "(a:double[],b:double[], i1:int,i2:int)";
//
//		className = "org.apache.commons.math.stat.descriptive.summary.SumOfSquares";
//		methodName = "evaluate";
//		paramString = "(a:double[],i1:int,i2:int)";
//		
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "asinh";
//		paramString = "(a:double)";
//
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "atan2";
//		paramString = "(a:double, b:double)";
//
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "cos";
//		paramString = "(a:double)";
//
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "hypot";
//		paramString = "(a:double, b:double)";
//		
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "log1p";
//		paramString = "(a:double)";
//
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "nextAfter1";
//		paramString = "(a:double, b:double)";
//		
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "nextAfter2";
//		paramString = "(f:float, b:double)";
//
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "pow";
//		paramString = "(a:double, b:double)";		
//
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "scalb1";
//		paramString = "(a:double, i:int)";	
//
		mainEntry = className = "org.apache.commons.math.util.FastMath";
		methodName = "scalb2";
		paramString = "(a:float, i:int)";
//		
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "sin";
//		paramString = "(a:double)";
//		
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "tan";
//		paramString = "(a:double)";
//
//		className = "org.apache.commons.math.util.MathUtils";
//		methodName = "binomialCoefficient";
//		paramString = "(a:int, b:int)";
//		
//		className = "org.apache.commons.math.util.MathUtils";
//		methodName = "compareTo";
//		paramString = "(a:double, b:double, c:double)";
//
//		className = "org.apache.commons.math.util.MathUtils";
//		methodName = "equals";
//		paramString = "(a:double, b:double)";
//
//		className = "org.apache.commons.math.util.MathUtils";
//		methodName = "equalsIncludingNaN";
//		paramString = "(a:double, b:double)";
//
//		className = "org.apache.commons.math.util.MathUtils";
//		methodName = "mulAndCheck";
//		paramString = "(a:int, b:int)";
//
//		className = "org.apache.commons.math.util.MathUtils";
//		methodName = "nextAfter";
//		paramString = "(a:double, b:double)";
//
//		className = "org.apache.commons.math.util.OpenIntToDoubleHashMap";
//		methodName = "findInsertionIndex";
//		paramString = "(a:int)";
//
//		className = "org.apache.commons.math.util.OpenIntToFieldHashMap";
//		methodName = "findInsertionIndex";
//		paramString = "(a:int)";
		
//		mainEntry = "testdata.l2t.test.init.dfp.align.DfpMain";
//		className = "org.apache.commons.math.dfp.Dfp";
//		methodName = "align";
//		paramString = "(e:int)";
//		
//		mainEntry = "testdata.l2t.test.init.dfp.divide.DfpMain";
//		className = "org.apache.commons.math.dfp.Dfp";
//		methodName = "divide";
//		paramString = "(e:int)";
//		
//		mainEntry = "testdata.l2t.test.init.zipfdistributionimpl.cumulativeprobability.ZipfDistributionImplMain";
//		className = "org.apache.commons.math.distribution.ZipfDistributionImpl";
//		methodName = "cumulativeProbability";
//		paramString = "(e:int)";
//		
//		mainEntry = "testdata.l2t.test.init.poissondistributionimpl.probability.PoissonDistributionImplMain";
//		className = "org.apache.commons.math.distribution.PoissonDistributionImpl";
//		methodName = "probability";
//		paramString = "(e:int)";
				
		long min_free = 20*(1024<<10); // min free memory
		long timeLimit = 10 * 1000;
		String[] config = constructConfig(mainEntry, className, pathString, methodName, paramString,
				min_free, timeLimit);		
		List<TestInput> inputList = new JDartCore().run(constructJDartParams(args));
//		inputList = RunJPF.run(config);
		
		System.currentTimeMillis();
	}

	private static JDartParams constructJDartParams(String[] args) {
		String  classpathStr = "E:\\hairui\\git\\apache-common-math-2.2\\apache-common-math-2.2\\bin", 
				mainEntry = "testdata.l2t.test.init.zipfdistributionimpl.cumulativeprobability.ZipfDistributionImplMain",
				className = "org.apache.commons.math.distribution.ZipfDistributionImpl",
				methodName = "cumulativeProbability",
				paramString = "(e:int)";
		
		className = "org.apache.commons.math.util.FastMath";
		mainEntry = className;
		methodName = "scalb2";
		paramString = "(a:float, i:int)";
		long minFree = 20*(1024<<10); // min free memory
		long timeLimit = 10 * 1000;
		
		JDartParams params = new JDartParams();
		params.setAppProperties("libs/jdart/jpf.properties");
		params.setClassName(className);
		params.setClasspathStr(classpathStr);
		params.setMainEntry(mainEntry);
		params.setMethodName(methodName);
		params.setMinFree(minFree);
		params.setParamString(paramString);
		params.setSiteProperties("libs/jpf.properties");
		params.setTimeLimit(timeLimit);
		
		return params;
	}
}
