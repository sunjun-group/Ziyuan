/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import evosuite.core.EvosuiteRunner.EvosuiteResult;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.FileUtils;
import sav.common.core.utils.JavaFileUtils;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.vm.JavaCompiler;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 *
 */
public class EvosuiteTestcasesHandler {
	private JavaCompiler jCompiler;
	private AppJavaClassPath appClasspath;
	
	public EvosuiteTestcasesHandler(AppJavaClassPath appClassPath) {
		
		jCompiler = new JavaCompiler(new VMConfiguration(appClassPath));
		this.appClasspath = appClassPath;
	}
	
	public FilesInfo getEvosuiteTestcases(Configuration config, String newPkg, EvosuiteResult result) {
		try {
			System.out.println();
			FilesInfo info = lookupJunitClasses(config, newPkg);
			return info;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SavRtException(e);
		}
	}
	
	public FilesInfo lookupJunitClasses(Configuration config, String newPkg)
			throws FileNotFoundException, IOException, SavException {
		String folder = config.getEvoBaseDir() + "/evosuite-tests";
		Collection<?> files = getAllJavaFiles(folder);
		FilesInfo info = new FilesInfo();
		for (Object obj : files) {
			File file = (File) obj;
			if (!file.getName().contains("ESTest_scaffolding")) {
				info.junitClasses.add(ClassUtils.getCanonicalName(newPkg, file.getName().replace(".java", "")));
			}
			info.allFiles.add(file);
		}
		/* copy all files to source folder */
		String pkgDecl = new StringBuilder("package ").append(newPkg).append(";").toString();
		FileUtils.mkDirs(config.getEvosuitSrcFolder());
		String junitNewFolder = JavaFileUtils.getClassFolder(config.getEvosuitSrcFolder(), newPkg);
		FileUtils.deleteAllFiles(junitNewFolder);
		FileUtils.copyFiles(info.allFiles, junitNewFolder);
		/* update new files */
		info.allFiles = CollectionUtils.toArrayList((File[])(new File(junitNewFolder)).listFiles());
		/* modify package */
		for (File file : info.allFiles) {
			modifyJavaFile(file, pkgDecl);
		}
		/* compile */
		jCompiler.compile(appClasspath.getTestTarget(), getAllJavaFiles(junitNewFolder));
		return info;
	}

	private Collection<File> getAllJavaFiles(String folder) {
		return org.apache.commons.io.FileUtils.listFiles(new File(folder), new String[] { "java" }, true);
	}
	
	private void modifyJavaFile(File file, String newPkgDecl) {
		try {
			List<String> lines = org.apache.commons.io.FileUtils.readLines(file);
			modifyPackage(lines, newPkgDecl);
			modifyClassDeclaration(lines);
			org.apache.commons.io.FileUtils.writeLines(file, lines);
		} catch (IOException e) {
			e.printStackTrace();
			throw new SavRtException(e);
		}
	}
	
	private void modifyPackage(List<String> lines, String newPkgDecl) {
		int i = 0;
		String newPkgLine = null;
		for (; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line.startsWith("package ")) {
				int endDeclIdx = line.indexOf(";");
				if (endDeclIdx == (line.length() - 1)) {
					newPkgLine = newPkgDecl;
				} else {
					newPkgLine = new StringBuilder(newPkgDecl).append(line.substring(endDeclIdx + 1, line.length() - 1)).toString();
				}
				break;
			}
		}
		lines.set(i, newPkgLine);
	}
	
	private void modifyClassDeclaration(List<String> lines) {
		int i = 0;
		String newLine = null;
		for (; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line.startsWith("public class ")) {
				newLine = new StringBuilder("@SuppressWarnings(\"deprecation\")   ").append(line).toString();
				break;
			}
		}
		lines.set(i, newLine);
	}

	public static class FilesInfo {
		List<File> allFiles = new ArrayList<File>();
		List<String> junitClasses = new ArrayList<String>();
	}
}
