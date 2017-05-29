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
public class LoopSample {
	
	public void run() {
		int i = 0;
		while(true) {
			System.out.println(i);
		}
	}
}
