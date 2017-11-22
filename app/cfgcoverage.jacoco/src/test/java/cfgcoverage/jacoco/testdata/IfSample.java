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
	
	public void cond(int a, int b) {
		if ((a == 1)) {
			if (b > 0) {
				System.out.println();
				if (b < 3) {
					System.out.println();
				}
			}
		}
		System.out.println();
	}
	
	public void multiCondOr(int a, int b) {
		if ((a == 1) || (b > 0) || (a > b)) {
			System.out.println();
			if (b < 3) {
				System.out.println();
			}
		}
		System.out.println();
	}
	
	public void multiCondAnd(int a, int b) {
		if ((a != 1) && (b > 0) && (a > b)) {
			System.out.println();
			if (b < 3) {
				System.out.println();
			}
		}
		System.out.println();
	}
	
	public void multiCondAndOr(int a, int b) {
		if (((a == 1) || (b > 0)) && (a > b)) {
			System.out.println();
			if (b < 3) {
				System.out.println();
			}
		}
		System.out.println();
	}
}
