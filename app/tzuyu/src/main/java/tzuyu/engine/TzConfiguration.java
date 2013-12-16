/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine;

import java.io.File;


/**
 * @author LLT will be replace Option (not be global variable anymore).
 */
public class TzConfiguration {
	private int testsPerQuery;
	private boolean objectToInteger;
	private boolean inheritedMethod;
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
		// check if entered output dir is exist, if not , reset to null.(move from Learn class)
		File outputDir = null;
		if (!output.equals("")) {
			outputDir = new File(output);
			if (!outputDir.exists() || !outputDir.isDirectory()) {
				TzLogger.log().error("Invalid output directory, use the default");
				outputDir = null;
			}
		}
		this.outputDir = outputDir;
	}
	
	
}
