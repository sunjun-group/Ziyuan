/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine;

import static tzuyu.engine.TzConstants.ARRAY_MAX_LENGTH;
import static tzuyu.engine.TzConstants.CLASS_MAX_DEPTH;
import static tzuyu.engine.TzConstants.DEBUG_CHECKS;
import static tzuyu.engine.TzConstants.FORBIT_NULL;
import static tzuyu.engine.TzConstants.INHERIT_METHOD;
import static tzuyu.engine.TzConstants.LONG_FORMAT;
import static tzuyu.engine.TzConstants.MAX_LINES_PER_GEN_TEST_CLASS;
import static tzuyu.engine.TzConstants.MAX_METHODS_PER_GEN_TEST_CLASS;
import static tzuyu.engine.TzConstants.NUMBER_OF_TESTCASES;
import static tzuyu.engine.TzConstants.OBJECT_TO_INTEGER;
import static tzuyu.engine.TzConstants.PRETTY_PRINT;
import static tzuyu.engine.TzConstants.PRINT_FAIL_TESTS;
import static tzuyu.engine.TzConstants.PRINT_PASS_TESTS;
import static tzuyu.engine.TzConstants.STRING_MAX_LENGTH;
import static tzuyu.engine.TzConstants.TESTS_PER_QUERY;
import static tzuyu.engine.TzConstants.TRACE_MAX_LENGTH;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.utils.StringUtils;
import tzuyu.engine.utils.Globals;
import tzuyu.engine.utils.PrimitiveGenerator.PrimitiveGeneratorConfig;

/**
 * @author LLT
 */
public class TzConfiguration implements Cloneable, PrimitiveGeneratorConfig {
	private static Logger log = LoggerFactory.getLogger(TzConfiguration.class);
	/**
	 * The maximum number of elements in an array when cloning an object. The
	 * array field in a target object may be too long to clone (results in out
	 * of memory problem, so we only want to clone the maximum number of
	 * elements to eradicate the out of memory problem.
	 */
	private int arrayMaxLength;
	/**
	 * The maximum class definition depth used for static analysis and
	 * instrumentation.
	 */
	private int classMaxDepth;
	private boolean debugChecks;
	private boolean forbidNull;
	/*
	 *format junit file
	 * if long, we will have something like this:
	 * int x = a;
	 * method(x);
	 * if short format, it will turn into:
	 * method(a); 
	 */
	private boolean longFormat;
	private boolean prettyPrint;
	private int stringMaxLength;
	/**
	 * The number of different test cases should we generate for each query
	 */
	private int testsPerQuery;
	private boolean objectToInteger;
	private boolean inheritedMethod;
	private boolean printFailTests;
	private boolean printPassTests;
	private int maxMethodsPerGenTestClass;
	private int maxLinesPerGenTestClass;
	private File outputDir;
	private String outputPath;	//ex: D:/workspace/tzuyu/scr
	private String outputPackage; // ex: tzuyu.test
	private int tcNum;
	private int traceMaxLength;
	
	
	public TzConfiguration(boolean setDefault) {
		if (setDefault) {
			setDefault();
		}
	}
	
	public void setDefault() {
		arrayMaxLength = ARRAY_MAX_LENGTH.b;
		classMaxDepth = CLASS_MAX_DEPTH.b;
		stringMaxLength = STRING_MAX_LENGTH.b;
		debugChecks = DEBUG_CHECKS.b;
		forbidNull = FORBIT_NULL.b;
		longFormat = LONG_FORMAT.b;
		prettyPrint = PRETTY_PRINT.b; 
		testsPerQuery = TESTS_PER_QUERY.b;
		objectToInteger = OBJECT_TO_INTEGER.b;
		inheritedMethod = INHERIT_METHOD.b;
		printFailTests = PRINT_FAIL_TESTS.b;
		printPassTests = PRINT_PASS_TESTS.b;
		maxMethodsPerGenTestClass = MAX_METHODS_PER_GEN_TEST_CLASS.b;
		maxLinesPerGenTestClass = MAX_LINES_PER_GEN_TEST_CLASS.b;
		tcNum = NUMBER_OF_TESTCASES.b;
		traceMaxLength = TRACE_MAX_LENGTH.b;
	}
	
	public TzConfiguration(TzConfiguration config) {
		arrayMaxLength = config.getArrayMaxLength();
		classMaxDepth = config.getClassMaxDepth();
		debugChecks = config.isDebugChecks();
		forbidNull = config.isForbidNull();
		longFormat = config.isLongFormat();
		prettyPrint = config.isPrettyPrint();
		stringMaxLength = config.getStringMaxLength();
		testsPerQuery = config.getTestsPerQuery();
		objectToInteger = config.isObjectToInteger();
		inheritedMethod = config.isInheritedMethod();
		printFailTests = config.isPrintFailTests();
		printPassTests = config.isPrintPassTests();
		maxMethodsPerGenTestClass = config.getMaxMethodsPerGenTestClass();
		maxLinesPerGenTestClass = config.getMaxLinesPerGenTestClass();
		tcNum = config.getNumberOfTcs();
		traceMaxLength = config.getTraceMaxLength();
	}

	public int getTestsPerQuery() {
		return testsPerQuery;
	}

	public void setTestsPerQuery(int testsPerQuery) {
		this.testsPerQuery = testsPerQuery;
	}

	public void setObjectToInteger(boolean objectToInteger) {
		this.objectToInteger = objectToInteger;
	}

	public boolean isInheritedMethod() {
		return inheritedMethod;
	}

	public void setInheritedMethod(boolean inheritedMethod) {
		this.inheritedMethod = inheritedMethod;
	}

	public File getOutputDir() {
		return outputDir;
	}
	
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}
	
	public void setOutputPackage(String pkgName) {
		this.outputPackage = pkgName;
	}
	
	public String getOutputPath() {
		return outputPath;
	}

	public void setOutput(String output) {
		// check if entered output dir is exist, if not , reset to null.(move
		// from Learn class)
		File outputDir = null;
		if (!output.equals("")) {
			outputDir = new File(output);
			if (!outputDir.exists() || !outputDir.isDirectory()) {
				log.error(
						"Invalid output directory, use the default");
				outputDir = null;
			}
		}
		this.outputDir = outputDir;
	}
	
	

	public boolean isObjectToInteger() {
		return objectToInteger;
	}

	public String getAbsoluteAddress(String filename) {
		return StringUtils.join(Globals.fileSep, 
				outputPath, outputPackage, filename);
	}

	public int getArrayMaxLength() {
		return arrayMaxLength;
	}

	public int getClassMaxDepth() {
		return classMaxDepth;
	}

	public boolean isDebugChecks() {
		return debugChecks;
	}

	public boolean isForbidNull() {
		return forbidNull;
	}

	public boolean isLongFormat() {
		return longFormat;
	}

	public boolean isPrettyPrint() {
		return prettyPrint;
	}

	public int getStringMaxLength() {
		return stringMaxLength;
	}

	public void setStringMaxLength(int stringMaxLength) {
		this.stringMaxLength = stringMaxLength;
	}

	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
		this.outputPath = outputDir.getAbsolutePath();
	}

	public void setArrayMaxLength(int arrayMaxLength) {
		this.arrayMaxLength = arrayMaxLength;
	}

	public void setClassMaxDepth(int classMaxDepth) {
		this.classMaxDepth = classMaxDepth;
	}

	public void setDebugChecks(boolean debugChecks) {
		this.debugChecks = debugChecks;
	}

	public void setForbidNull(boolean forbidNull) {
		this.forbidNull = forbidNull;
	}

	public void setLongFormat(boolean longFormat) {
		this.longFormat = longFormat;
	}

	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}

	public boolean isPrintFailTests() {
		return printFailTests;
	}

	public void setPrintFailTests(boolean printFailTests) {
		this.printFailTests = printFailTests;
	}

	public boolean isPrintPassTests() {
		return printPassTests;
	}

	public void setPrintPassTests(boolean printPassTests) {
		this.printPassTests = printPassTests;
	}
	
	@Override
	public TzConfiguration clone() {
		return new TzConfiguration(this);
	}

	public int getMaxMethodsPerGenTestClass() {
		return maxMethodsPerGenTestClass;
	}

	public int getMaxLinesPerGenTestClass() {
		return maxLinesPerGenTestClass;
	}

	public void setMaxMethodsPerGenTestClass(int maxMethodsPerGenTestClass) {
		this.maxMethodsPerGenTestClass = maxMethodsPerGenTestClass;
	}

	public void setMaxLinesPerGenTestClass(int maxLinesPerGenTestClass) {
		this.maxLinesPerGenTestClass = maxLinesPerGenTestClass;
	}
	
	public String getPackageName(boolean type) {
		if (type) {
			return StringUtils.dotJoin(outputPackage, TzConstants.DEFAULT_PASS_PKG_NAME);
		}
		return StringUtils.dotJoin(outputPackage, TzConstants.DEFAULT_FAIL_PKG_NAME);
	}

	public int getNumberOfTcs() {
		return tcNum;
	}

	public void setTcNum(int tcNum) {
		this.tcNum = tcNum;
	}

	public int getTraceMaxLength() {
		return traceMaxLength;
	}

	public void setTraceMaxLength(int traceMaxLength) {
		this.traceMaxLength = traceMaxLength;
	}
	
}
