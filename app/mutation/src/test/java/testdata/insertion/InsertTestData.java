/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package testdata.insertion;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author LLT
 * 
 */
public class InsertTestData {
	private Logger log;

	private int a;
	public boolean isPalindrome1(int x) {
		List<Integer> digits = new ArrayList<Integer>();
		while (x > 0) {
			int digit = x % 10;
			digits.add(digit);
			x = x / 10;
		}
		int i = 0;
		int j = digits.size();
		while (i < j) {
			if (digits.get(i++) != digits.get(--j)) {
				return false;
			}
		}
		return true;
	}
	
	public String concat(String a, String b) {
		return a.concat(b);
	}
	
	public InsertTestData getThis(){
		return this;
	}

	public int getA() {
		return a;
	}	
}
