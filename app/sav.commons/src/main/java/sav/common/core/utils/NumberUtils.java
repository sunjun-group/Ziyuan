/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.common.core.utils;

/**
 * @author LLT
 *
 */
public class NumberUtils {
	private NumberUtils() {
	}
	
	public static boolean isNumber(String str)  {
		try {
			Integer.parseInt(str);
			return true;
		} catch(Exception ex) {
			return false;
		}
	}
	
	public static int toNumber(String str, int defaultValue)  {
		try {
			return Integer.parseInt(str);
		} catch(Exception ex) {
			return defaultValue;
		}
	}
	
	public static double round(double val, int fraction) {
		return Math.round(val * fraction) / fraction;
	}
}
