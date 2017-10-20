/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite;

import evosuite.EvosuiteRunner.EvosuiteResult;

/**
 * @author LLT
 *
 */
public class ExportData {
	private int rowNum;
	private String methodName;
	private int startLine;
	private EvosuiteResult evoResult;
	private boolean evoCvgExisted;

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public int getStartLine() {
		return startLine;
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	public EvosuiteResult getEvoResult() {
		return evoResult;
	}

	public void setEvoResult(EvosuiteResult evoResult) {
		this.evoResult = evoResult;
	}

	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	public boolean isEvoCvgExisted() {
		return evoCvgExisted;
	}

	public void setEvoCvgExisted(boolean evoCvgExisted) {
		this.evoCvgExisted = evoCvgExisted;
	}

	
}
