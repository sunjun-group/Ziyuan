/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.vm;

import sav.common.core.Constants;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class VmRunnerUtils {

	public static String buildJavaExecArg(VMConfiguration config) {
		return StringUtils.join(Constants.FILE_SEPARATOR, config.getJavaHome(), "bin", "java");
	}
	
	public static String buildJavaCPrefix(VMConfiguration config) {
		
		String javaHome = config.getJavaHome();
		/**
		 * FIX ME:
		 * javac has been moved to jdk folder since java 1.8.
		 */
		if(javaHome.contains("1.8")){
			javaHome = javaHome.replace("jre", "jdk");
		}
		return StringUtils.join(Constants.FILE_SEPARATOR, javaHome, "bin", "javac");
	}
}
