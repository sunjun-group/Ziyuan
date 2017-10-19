/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.evosuite.runtime.annotation.EvoSuiteClassExclude;
import org.evosuite.runtime.annotation.EvoSuiteExclude;
import org.evosuite.runtime.annotation.EvoSuiteInclude;

import sav.common.core.utils.JavaFileUtils;
import sav.common.core.utils.TextFormatUtils;

/**
 * @author LLT
 *
 */
public class EvoJavaFileAdaptor {
	private static final String INCLUDE_ANNOTATION;
	private static final String EXCLUDE_ANNOTATION;
	private static final String CLASS_EXCLUDE_ANNOTATION;
	private static final String ANNOTATION_IMPORTS;
	static {
		String annFormat = "@%s ";
		INCLUDE_ANNOTATION = String.format(annFormat, EvoSuiteInclude.class.getSimpleName());
		EXCLUDE_ANNOTATION = String.format(annFormat, EvoSuiteExclude.class.getSimpleName());
		CLASS_EXCLUDE_ANNOTATION = String.format(annFormat, EvoSuiteClassExclude.class.getSimpleName());
		String importFormat = "import %s;";
		ANNOTATION_IMPORTS = new StringBuilder().append(String.format(importFormat, EvoSuiteExclude.class.getName()))
					.append(String.format(importFormat, EvoSuiteInclude.class.getName())).toString();
	}
	private List<String> content;
	private int prevIncludeLine = -1;
	private File sourceFile;
	private int importLine;
	private int classLine;
	private Map<Integer, Integer> methodStartLineMap;
	private List<Integer> allMethodStartLine;
	
	public EvoJavaFileAdaptor(File sourceFile, List<Integer> lines) throws Exception {
		content = IOUtils.readLines(new FileInputStream(sourceFile));
		this.sourceFile = sourceFile;
		ClassInfoMapping mapping = new ClassInfoMapping(sourceFile, lines);
		classLine = mapping.getClassLine();
		importLine = mapping.getPackageLine();
		methodStartLineMap = mapping.getMethodStartLineMap();
		allMethodStartLine = mapping.getAllMethodStartLine();
		revertAll();
		insertImports();
//		disableClass();
		disableAllMethods();
	}
	
	public EvoJavaFileAdaptor(String src, TargetClass targetClass) throws Exception {
		this(JavaFileUtils.getSourceFile(Arrays.asList(src), targetClass.getClassName()),
				targetClass.getMethodStartLines());
	}

	private void disableClass() {
		StringBuilder sb = new StringBuilder();
		sb.append(CLASS_EXCLUDE_ANNOTATION).append(classLine);
		content.set(classLine, sb.toString());
	}


	private void insertImports() {
		addAfter(importLine, ANNOTATION_IMPORTS);
	}
	
	private void disableAllMethods() {
		for (Integer line : allMethodStartLine) {
			addFront(line, EXCLUDE_ANNOTATION);
		}
	}

	public void enableMethod(int line) throws Exception {
		int lineIdx = methodStartLineMap.get(line);
		revertPrevIncludeLine();
		String code = content.get(lineIdx);
		code = code.replace(EXCLUDE_ANNOTATION, INCLUDE_ANNOTATION);
		content.set(lineIdx, code);
		prevIncludeLine = lineIdx;
		updateFile();
	}

	private void updateFile() throws Exception {
		IOUtils.writeLines(content, null, new FileOutputStream(sourceFile));
	}

	private boolean revertPrevIncludeLine() {
		if (prevIncludeLine >= 0) {
			revert(prevIncludeLine, INCLUDE_ANNOTATION);
			prevIncludeLine = -1;
			return true;
		}
		return false;
	}
	
	public void revertAll() throws Exception {
		revertPrevIncludeLine();
		revert(importLine, ANNOTATION_IMPORTS);
		revert(classLine, EXCLUDE_ANNOTATION);
		for (int line : allMethodStartLine) {
			revert(line, EXCLUDE_ANNOTATION);
			revert(line, INCLUDE_ANNOTATION);
		}
		updateFile();
	}
	
	private void revert(int line, String addedStr) {
		if (line >= 0) {
			String code = content.get(line);
			code = code.replace(addedStr, "");
			content.set(line, code);
		}
	}
	
	private void addFront(int line, String addedStr) {
		if (line >= 0) {
			String code = content.get(line);
			StringBuilder sb = new StringBuilder(addedStr)
					.append(code);
			content.set(line, sb.toString());
		}
	}
	
	private void addAfter(int line, String addedStr) {
		if (line >= 0) {
			String code = content.get(line);
			StringBuilder sb = new StringBuilder(code)
					.append(addedStr);
			content.set(line, sb.toString());
		}
	}
	
	public File getSourceFile() {
		return sourceFile;
	}
	
	@Override
	public String toString() {
		return TextFormatUtils.printCol(content, "\n");
	}
}
