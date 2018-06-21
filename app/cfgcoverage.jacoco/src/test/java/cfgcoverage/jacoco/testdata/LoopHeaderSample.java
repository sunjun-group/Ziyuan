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
public class LoopHeaderSample {
	
	public void multiLoopCond1() {
		int x = 1;
		int y = 3;
		while (x < 4 
				|| (y < 5 && y > 1)) {
			x += y;
		}
	}

	public void multiLoopCond() {
		int x = 1;
		int y = 3;
		while (x < 4 
				&& y < 5) {
			x += y;
		}
		
		while (x < 4 && (x + y) < 10 &&
				(x * y) < 20
				&& y < 5) {
			x += y;
		}
	}
	
	public void multiLoopCondNeg() {
		int x = 1;
		int y = 3;
		while (!(x < 4) 
				&& y < 5) {
			x += y;
		}
	}
	
	public void singleLoopCond() {
		int x = 1;
		int y = 3;
		while (y < 5) {
			x += y;
		}
	}
	
	public void forLoop() {
		int x = 1;
		int y = 3;
		for (int i = 0; i < 10; i++) {
			x += y * i;
		}
		System.out.println(x);
	}
	
	public void forLoop2() {
		int x = 1;
		int y = 3;
		for (int i = 0; i < 10; i++) {
			if (i == 2) {
				continue;
			}
			x += y * i;
		}
		System.out.println(x);
	}
	
	public void doWhileMultiCond() {
		int x = 1;
		int y = 3;
		do {
			x += y;
		} while (x < 4 
				&& y < 5);
	}
	
	public void doWhileSingleCondWithInLoopCond() {
		int x = 1;
		int y = 3;
		do {
			x += y;
			if (x >= 4) {
				return;
			}
		} while (y < 5);
	}
	
	public void innerLoop() {
		int[][] a = new int[2][10];
		int val = 0;
		for (int i = 0; i < 2; i++) {
			System.out.println(i);
			for (int j = 0; j < 10; j++) {
				a[i][j] = val++;
				if (val == 10) {
					val = 0;
				}
			}
		}
	}
	
	public void innerLoop2(int x, int y) {
		int[][] a = new int[2][10];
		int val = 0;
		for (int i = 0; i < 2; i++) {
			System.out.println(i);
			do {
				x += y;
				if (x >= 4) {
					return;
				} else {
					for (int j = 0; j < 10; j++) {
						a[i][j] = val++;
						if (val == 10) {
							val = 0;
						}
					}
				}
			} while (y < 5);
		}
	}
}
