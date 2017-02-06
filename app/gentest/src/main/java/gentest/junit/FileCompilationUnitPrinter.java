/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.junit;

import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.Constants;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class FileCompilationUnitPrinter implements ICompilationUnitPrinter {
	private static Logger log = LoggerFactory.getLogger(FileCompilationUnitPrinter.class);
	private String srcFolder;
	private List<File> files;
	
	public FileCompilationUnitPrinter(String srcFolderPath) {
		this.srcFolder = srcFolderPath;
		files = new ArrayList<File>();
	}
	
	public void print(List<CompilationUnit> compilationUnits) {
		Map<String, List<CompilationUnit>> cuGroup = groupByClassFolder(compilationUnits);
		for (String classFolder : cuGroup.keySet()) {
			File folder = new File(classFolder);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			for (CompilationUnit cu : cuGroup.get(classFolder)) {
				/* create java file */
				String filePath = getFilePath(classFolder, cu.getTypes().get(0).getName());
				try {
					File file = new File(filePath);
					if (!file.exists()) {
						file.createNewFile();
					}
					/* write content */
					PrintStream stream = new PrintStream(file);
					stream.println(cu.toString());
					stream.close();
					files.add(file);
				} catch (IOException e) {
					log.error(e.getMessage());
					log.error("cannot create file " + filePath);
				}
			}
		}
	}
	
	private Map<String, List<CompilationUnit>> groupByClassFolder(
			List<CompilationUnit> compilationUnits) {
		Map<String, List<CompilationUnit>> result = new HashMap<String, List<CompilationUnit>>();
		for (CompilationUnit cu : compilationUnits) {
			String pkgFolder = PrinterUtils.getClassFolder(srcFolder, 
					cu.getPackage().getName().getName());
			CollectionUtils.getListInitIfEmpty(result, pkgFolder)
					.add(cu);
		}
		return result;
	}

	private String getFilePath(String pkg, String clazz) {
		return pkg + Constants.FILE_SEPARATOR + clazz + Constants.JAVA_EXT_WITH_DOT;
	}

	public List<File> getGeneratedFiles() {
		return files;
	}
	
}
