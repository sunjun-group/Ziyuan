/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package codecoverage.jacoco.agent;

/**
 * @author LLT
 *
 */
public class JaCoCoUtils {
	public static final char JACOCO_FILE_SEPARATOR = '/';
	public static final String INIT_METHOD_NAME = "<init>";
	
	public static String getClassName(String jacocoClassName) {
		return jacocoClassName.replace(JACOCO_FILE_SEPARATOR, '.');
	}
	
	public static String getClassResourceStr(String className) {
		String resource = JACOCO_FILE_SEPARATOR
				+ className.replace('.', JACOCO_FILE_SEPARATOR) + ".class";
		return resource;
	}
}
