/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.vm;

/**
 * @author LLT
 *
 */
@SuppressWarnings("deprecation")
public class TargetClass {

	public void wrongMethod() {
		DeprecatedClass clazz = new DeprecatedClass(1, 3);
		String  a = "safsdf";
	}
}
