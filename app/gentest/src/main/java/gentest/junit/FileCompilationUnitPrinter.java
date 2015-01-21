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
import java.util.List;

import org.apache.commons.lang.StringUtils;

import sav.common.core.Constants;
import sav.common.core.Logger;

/**
 * @author LLT
 *
 */
public class FileCompilationUnitPrinter implements ICompilationUnitPrinter {
	private Logger<?> logger = Logger.getDefaultLogger();
	
	public void print(String srcFolderPath,
			List<CompilationUnit> compilationUnits) {
		for (CompilationUnit cu : compilationUnits) {
			/* create folder if does not exist */
			String classFolder = getClassFolder(srcFolderPath, 
						cu.getPackage().getName().getName());
			new File(classFolder).mkdirs();
			/* create java file */
			String filePath = getFilePath(classFolder, cu.getTypes().get(0).getName());
			try {
				File file = new File(filePath);
				file.createNewFile();
				/* write content */
				PrintStream stream = new PrintStream(file);
				stream.println(cu.toString());
				stream.close();
			} catch (IOException e) {
				logger.logEx(e, "cannot create file " + filePath);
			}
		}
	}

	private String getClassFolder(String srcFolderPath, String pkg) {
		if (!new File(srcFolderPath).exists()) {
			throw new IllegalArgumentException(String.format(
					"src folder %s does not exist", srcFolderPath));
		}
		String classFolder = srcFolderPath;
		if (pkg != null) {
			classFolder += Constants.FILE_SEPARATOR + StringUtils.replace(pkg, 
					".", Constants.FILE_SEPARATOR);
		}
		return classFolder;
	}

	private String getFilePath(String pkg, String clazz) {
		return pkg + Constants.FILE_SEPARATOR + clazz + Constants.JAVA_EXT;
	}

}
