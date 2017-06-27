/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons;

import learntest.core.commons.data.classinfo.TargetMethod;

/**
 * @author LLT
 *
 */
public class LearntestConstants {
	private LearntestConstants(){}
	
	public static long GENTEST_METHOD_EXEC_TIMEOUT = 200l; //ms
	/* testdata.[approachName].result.[init_test_package_name]*/
	private static final String RESULT_TEST_PKG_FORMAT = "testdata.%s.result.%s";
	/* testdata.[approachName].test.init.[classSimpleName].[methodName]*/
	private static final String INIT_TEST_PKF_FORMAT = "testdata.%s.test.init.%s.%s";
	
	public static String getResultTestPackage(String approachName, TargetMethod targetMethod){
		return String.format(RESULT_TEST_PKG_FORMAT, approachName,
				getInitTestPackage(approachName, targetMethod));
	}
	
	public static String getInitTestPackage(String approachName, TargetMethod targetMethod) {
		return String.format(INIT_TEST_PKF_FORMAT, approachName,
				targetMethod.getTargetClazz().getClassSimpleName().toLowerCase(),
				targetMethod.getMethodName().toLowerCase());
	}
}
