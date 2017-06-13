/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package jdart.core;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;

import jdart.handler.PluginUtils;
import jdart.model.TestInput;
import main.RunJPF;

/**
 * @author ??
 * extracted from RunJDartHandler.
 */
public class JDartCore {
	
	public List<TestInput> run(JDartParams jdartParams) {
		String[] config = constructConfig(jdartParams);
		List<TestInput> inputList = RunJPF.run(config);
		return inputList;
	}
	
	private static String[] constructConfig(JDartParams params) {
		return  new String[]{
				"+app=" + PluginUtils.loadAbsolutePath("libs/jdart/jpf.properties"),
				"+site=" + PluginUtils.loadAbsolutePath("libs/jpf.properties"),
				"+jpf-jdart.classpath+=" + params.getClasspathStr(),
				"+target=" + params.getMainEntry(),
				"+concolic.method=" + params.getMethodName(),
				"+concolic.method." + params.getMethodName() + "=" +params.getClassName()+"."+ params.getMethodName() + params.getParamString(),
				"+concolic.method." + params.getMethodName() + ".config=all_fields_symbolic"
		};
	}
	
	private static String[] constructConfig(String mainEntry, String className, String pathString, String methodName, String paramString) {
		return  new String[]{
				"+app=" + PluginUtils.loadAbsolutePath("libs/jdart/jpf.properties"),
				"+site=" + PluginUtils.loadAbsolutePath("libs/jpf.properties"),
				"+jpf-jdart.classpath+=" + pathString,
				"+target=" + className,
				"+concolic.method=" + methodName,
				"+concolic.method." + methodName + "=" +className+"."+ methodName + paramString,
				"+concolic.method." + methodName + ".config=all_fields_symbolic"
		};
	}

	public static void main(String[] args) throws ExecutionException {

		String  pathString = "E:\\workspace\\JPF\\data\\apache-common-math-2.2\\bin", 
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
//		className = "org.apache.commons.math.util.FastMath";
//		methodName = "scalb2";
//		paramString = "(a:float, i:int)";
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
				
		String[] config = constructConfig(mainEntry, className, pathString, methodName, paramString);		
		List<TestInput> inputList = RunJPF.run(config);
	}

}
