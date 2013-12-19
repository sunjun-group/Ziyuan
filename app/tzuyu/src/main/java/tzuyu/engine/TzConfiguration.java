/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine;

import java.io.File;

import tzuyu.engine.utils.Globals;

/**
 * @author LLT
 */
public class TzConfiguration {
	// TODO [LLT]: should these constants be configurable or not?
	/**
	 * The maximum number of elements in an array when cloning an object. The
	 * array field in a target object may be too long to clone (results in out
	 * of memory problem, so we only want to clone the maximum number of
	 * elements to eradicate the out of memory problem.
	 */
	private static final int ARRAY_MAX_LENGTH = 5;
	/**
	 * The maximum class definition depth used for static analysis and
	 * instrumentation.
	 */
	private static final int CLASS_MAX_DEPTH = 5;
	private static final boolean DEBUG_CHECKS = false;
	private static final boolean FORBID_NULL = true;
	private static final boolean LONG_FORMAT = true;
	private static final boolean PRETTY_PRINT = true;
	private static int STRING_MAX_LEN = 10;

	/**
	 * The number of different test cases should we generate for each query
	 */
	private int testsPerQuery = 1;
	private boolean objectToInteger = true;
	private boolean inheritedMethod = false;
	private File outputDir;

	public int getTestsPerQuery() {
		return testsPerQuery;
	}

	public void setTestsPerQuery(int testsPerQuery) {
		this.testsPerQuery = testsPerQuery;
	}

	public boolean isObjectToInteger() {
		return objectToInteger;
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

	public void setOutput(String output) {
		// check if entered output dir is exist, if not , reset to null.(move
		// from Learn class)
		File outputDir = null;
		if (!output.equals("")) {
			outputDir = new File(output);
			if (!outputDir.exists() || !outputDir.isDirectory()) {
				TzLogger.log().error(
						"Invalid output directory, use the default");
				outputDir = null;
			}
		}
		this.outputDir = outputDir;
	}

	public boolean alwaysUseIntsAsObjects() {
		return objectToInteger;
	}

	public String getAbsoluteAddress(String filename) {
		return outputDir.getAbsolutePath() + Globals.fileSep + filename;
	}

	public int getArrayMaxLength() {
		return ARRAY_MAX_LENGTH;
	}

	public int getClassMaxDepth() {
		return CLASS_MAX_DEPTH;
	}

	public boolean isDebugChecks() {
		return DEBUG_CHECKS;
	}

	public boolean isForbidNull() {
		return FORBID_NULL;
	}

	public boolean isLongFormat() {
		return LONG_FORMAT;
	}

	public boolean isPrettyPrint() {
		return PRETTY_PRINT;
	}

	public int getStringMaxLength() {
		return STRING_MAX_LEN;
	}

}
