/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine.utils;

/**
 * @author LLT
 * 
 */
public class ObjectUtils {

	public static int compare(int o1, int o2) {
		return (o1 < o2 ? -1 : (o1 == o2 ? 0 : 1));
	}
}
