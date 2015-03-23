/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.mutantbug;

import java.io.File;

/**
 * @author LLT
 *
 */
public class Recompiler {
	private String classpath;
	private String targetFolder;
	
	public Recompiler(String classpath, String targetFolder) {
		this.classpath = classpath;
		this.targetFolder = targetFolder;
	}
	
	public boolean recompile(File mutatedFile) {
		int errorCode = com.sun.tools.javac.Main.compile(new String[] {
	            "-classpath", classpath,
	            "-d", targetFolder,
	            mutatedFile.getAbsolutePath()});
		if (errorCode == 0) {
			return true;
		}
		return false;
	}
}
