/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.testdata;

/**
 * @author LLT
 *
 */
public class NestedLoopConditionSample {

	public void run() {
		boolean cond = true;
		int x = 1;
		int y = 3;
		while (cond ? x < 3 : y < 5) {
			x += y;
			x++;
			y++;
		}
		System.out.println();
	}
}
