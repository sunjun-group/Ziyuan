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
public class IfInLoop {
	
	public boolean run() {
		for (int i = 0; i < 10; i++) {
			if (i == 2) {
				System.out.println(i);
			}
			System.out.println(i + 1);
		}
		return false;
	}
	
	
}
