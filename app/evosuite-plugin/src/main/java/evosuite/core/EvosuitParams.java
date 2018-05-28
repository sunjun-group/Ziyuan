/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite.core;

import sav.common.core.Pair;
import sav.strategies.vm.ProgramArgumentBuilder;

/**
 * @author LLT
 *
 */
public class EvosuitParams {
	private String targetClass;
	private String classpath;
	private String baseDir;
	private String method;
	private int[] methodPosition;

	public String getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(String targetClass) {
		this.targetClass = targetClass;
	}

	public String getClasspath() {
		return classpath;
	}

	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}
	
	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}
	
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String[] getCommandLine() {
		ProgramArgumentBuilder argBuilder = new ProgramArgumentBuilder().addOptionArgument("generateSuite")
				.addArgument("class", targetClass).addArgument("projectCP", classpath)
				.addArgument("base_dir", baseDir)
				.addArgument("criterion", "BRANCH");
		return argBuilder.getArgArr();
	}

	public void setMethodPosition(int start, int end) {
		methodPosition = new int[]{start, end};
	}

	public int[] getMethodPosition() {
		return methodPosition;
	}
}
