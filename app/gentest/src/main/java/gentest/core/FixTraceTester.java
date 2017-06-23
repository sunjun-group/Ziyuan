/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.core;

import java.util.List;

/**
 * @author LLT
 *
 */
public class FixTraceTester extends RandomTester implements ITester {
	
	public FixTraceTester(int numberOfTcs, ClassLoader prjClassLoader) {
		super(-1, numberOfTcs, numberOfTcs, prjClassLoader);
	}
	
	public FixTraceTester(int numberOfTcs) {
		super(-1, numberOfTcs, numberOfTcs);
	}

	/**
	 * always fix the init trace.
	 */
	@Override
	protected <T>List<T> randomWalk(List<T> methodcalls) {
		return methodcalls;
	}
}
