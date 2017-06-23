/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine;

import tzuyu.engine.utils.Pair;


/**
 * @author LLT
 *
 */
public class TzConstants {
	protected TzConstants() {}
	public static final String JAVA_SUFFIX = "java";
	public static final String DFA_FILE_EXTENSION = "dot";
	
	public static final Pair<String, Integer> ARRAY_MAX_LENGTH = Pair.of("arrayMaxLength", 5);
	public static final Pair<String, Integer> CLASS_MAX_DEPTH = Pair.of("classMaxDepth", 5);
	public static final Pair<String, Integer> STRING_MAX_LENGTH = Pair.of("stringMaxLength", 10);
	public static final Pair<String, Boolean> LONG_FORMAT = Pair.of("longFormat", true);
	public static final Pair<String, Boolean> OBJECT_TO_INTEGER = Pair.of("objectToInteger", true);
	public static final Pair<String, Integer> TESTS_PER_QUERY = Pair.of("testsPerQuery", 1);
	public static final Pair<String, Boolean> DEBUG_CHECKS = Pair.of("debugChecks", false);
	public static final Pair<String, Boolean> INHERIT_METHOD = Pair.of("inheritMethod", false);
	public static final Pair<String, Boolean> FORBIT_NULL = Pair.of("forbitNull", true);
	public static final Pair<String, Boolean> PRETTY_PRINT = Pair.of("prettyPrint", true);
	public static final Pair<String, Boolean> PRINT_FAIL_TESTS = Pair.of("printFailTests", true);
	public static final Pair<String, Boolean> PRINT_PASS_TESTS = Pair.of("printPassTests", true);
	public static final Pair<String, Integer> MAX_METHODS_PER_GEN_TEST_CLASS = Pair.of("maxMethodsPerGenTestClass", 10);
	public static final Pair<String, Integer> MAX_LINES_PER_GEN_TEST_CLASS = Pair.of("maxLinesPerGenTestClass", 500);
	public static final Pair<String, Integer> NUMBER_OF_TESTCASES = Pair.of("numberOfTestcases", 100);
	public static final Pair<String, Integer> TRACE_MAX_LENGTH = Pair.of("traceMaxLength", 3);
	public static final String DEFAULT_OUTPUT_PACKAGE = "tzuyu";
	public static final String DEFAULT_PASS_PKG_NAME = "pass";
	public static final String DEFAULT_FAIL_PKG_NAME = "fail";
	public static final String CLASS_CLASS_NAME = Class.class.getName();// Class.class.getName();
	public static final String OBJECT_CLASS_NAME = Object.class.getName();
	
	public static final Pair<?, ?>[] ALL_PARAMS = new Pair<?, ?>[] {
		ARRAY_MAX_LENGTH, 
		CLASS_MAX_DEPTH,
		STRING_MAX_LENGTH,
		LONG_FORMAT,
		OBJECT_TO_INTEGER,
		TESTS_PER_QUERY,
		DEBUG_CHECKS,
		INHERIT_METHOD,
		FORBIT_NULL,
		PRETTY_PRINT,
		PRINT_FAIL_TESTS,
		PRINT_PASS_TESTS,
		MAX_METHODS_PER_GEN_TEST_CLASS,
		MAX_LINES_PER_GEN_TEST_CLASS,
		NUMBER_OF_TESTCASES,
		TRACE_MAX_LENGTH
	};
}
