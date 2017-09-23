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
public class IfSample {

	public void run(int a) {
		if (a < 0) {
			System.out.println(a);
		}
		
		System.out.println("c");
	}
	
	public void multiCond(int a, int b) {
		if ((a == 1) || (b > 0)) {
			System.out.println();
			if (b < 3) {
				System.out.println();
			}
		}
		
	}
}
