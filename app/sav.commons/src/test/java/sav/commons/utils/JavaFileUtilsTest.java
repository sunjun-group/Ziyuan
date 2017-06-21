/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.commons.utils;

import org.junit.Test;

import sav.common.core.utils.JavaFileUtils;

/**
 * @author LLT
 *
 */
public class JavaFileUtilsTest {

	@Test
	public void testGetClassPrefix() {
		String classPrefix = JavaFileUtils.getClassPrefix("classA123");
		System.out.println(classPrefix);
		classPrefix = JavaFileUtils.getClassPrefix("classA");
		System.out.println(classPrefix);
		classPrefix = JavaFileUtils.getClassPrefix("classA1");
		System.out.println(classPrefix);
	}
}
