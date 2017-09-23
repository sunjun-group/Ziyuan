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
public class ForSample {

	public boolean run() {
		for (int i = 0; i < 10; i++) {
			if (i == 2) {
				System.out.println(i);
				return true;
			}
		}
		return false;
	}
}
