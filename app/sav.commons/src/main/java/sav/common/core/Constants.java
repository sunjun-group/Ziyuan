/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core;

/**
 * @author LLT
 *
 */
public class Constants {
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String DOT = ".";
	public static final String NEW_LINE = "\n";
	public static final String JAVA_EXT_WITH_DOT = ".java";
	public static final String JAVA_EXT = "java";
	public static final String CLASS_EXT = "class";
	public static final String CLASS_EXT_WITH_DOT = ".class";
	public static final String TEXT_FILE_EXT = "txt";
	public static final String LOW_LINE = "_";
	public static final int UNKNOWN_LINE_NUMBER = -1;
	public static final String NESTED_CLASS_SEPARATOR = "$";
	/* time out for each running testcase */
	public static final long DEFAULT_JUNIT_TESTCASE_TIMEOUT = 1000l; // ms
}
