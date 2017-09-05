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
public class MultiLevelLoopSample {

	public void run(int x, int y, int z, int f) {
		for (int i = 0; i < x; i++) {
			int a = x + i;
			for (int j = 0; j < y; j++) {
				int b = 0;
				if (j < 2) {
					b = j + y;
				}
				System.out.println(a + b);
			}
		}
		
//		for (int i = 0; i < x; i++) {
//			int a = x + i;
//			for (int j = 0; j < y; j++) {
//				int b = j + y;
//				System.out.println(a + b);
//			}
//		}
	}
}
