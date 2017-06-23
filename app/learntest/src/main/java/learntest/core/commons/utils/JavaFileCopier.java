/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.expr.NameExpr;
import sav.common.core.Constants;
import sav.common.core.SavRtException;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.FileUtils;
import sav.common.core.utils.JavaFileUtils;

/**
 * @author LLT
 *
 */
public class JavaFileCopier {
	private JavaFileCopier() {
	}
	
	public static List<String> copy(List<String> sourceFiles, String fromPkg, String toPkg, String srcFolder) {
		String jSrcPath = fromPkg;
		if (fromPkg != null) {
			jSrcPath = getSourcePath(fromPkg, srcFolder);
		}
		List<String> newFiles = new ArrayList<String>(sourceFiles.size());
		for (String fileName : sourceFiles) {
			if (jSrcPath != null && !fileName.startsWith(jSrcPath)) {
				continue;
			}
			newFiles.add(copy(fileName, toPkg, srcFolder));
		}
		return newFiles;
	}
	
	private static String getSourcePath(String pkg, String srcFolder) {
		StringBuilder sb = new StringBuilder(srcFolder).append(Constants.FILE_SEPARATOR)
				.append(pkg.replace(Constants.DOT, Constants.FILE_SEPARATOR));
		return sb.toString();
	}

	public static String copy(String sourceFile, String toPkg, String targetFolder) {
		try {
			CompilationUnit cu = JavaParser.parse(new File(sourceFile));
			cu.getPackage().setName(new NameExpr(toPkg));
			String srcPath = getSourcePath(toPkg, targetFolder);
			FileUtils.mkDirs(srcPath);
			String className = cu.getTypes().get(0).getName();
			String jFilePath = ClassUtils.getJFilePath(srcPath, className);
			if (new File(jFilePath).exists()) {
				String classPrefix = JavaFileUtils.getClassPrefix(className);
				int newIdx = JavaFileUtils.getMaxIdxOfExistingClass(srcPath, classPrefix) + 1;
				String newClassName = classPrefix + newIdx;
				cu.getTypes().get(0).setName(newClassName);
				jFilePath = ClassUtils.getJFilePath(srcPath, newClassName);
			}
			FileUtils.appendFile(jFilePath, cu.toString());
			return jFilePath;
		} catch (ParseException e) {
			throw new SavRtException(e);
		} catch (IOException e) {
			throw new SavRtException(e);
		}
	}

}
