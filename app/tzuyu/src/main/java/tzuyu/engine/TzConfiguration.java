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
public class TzConfiguration implements Cloneable {
	// TODO [LLT]: put default value in configuration file?
	/**
	 * The maximum number of elements in an array when cloning an object. The
	 * array field in a target object may be too long to clone (results in out
	 * of memory problem, so we only want to clone the maximum number of
	 * elements to eradicate the out of memory problem.
	 */
	private int arrayMaxLength = 5;
	/**
	 * The maximum class definition depth used for static analysis and
	 * instrumentation.
	 */
	private int classMaxDepth = 5;
	private boolean debugChecks = false;
	private boolean forbidNull = true;
	private boolean longFormat = true;
	private boolean prettyPrint = true;
	private int stringMaxLength = 10;
	/**
	 * The number of different test cases should we generate for each query
	 */
	private int testsPerQuery = 1;
	private boolean objectToInteger = true;
	private boolean inheritedMethod = false;
	private File outputDir;
	
	public TzConfiguration() {
		
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

	public boolean isObjectToInteger() {
		return objectToInteger;
	}

	public String getAbsoluteAddress(String filename) {
		return outputDir.getAbsolutePath() + Globals.fileSep + filename;
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

	@Override
	public TzConfiguration clone() {
		return new TzConfiguration(this);
	}
}
