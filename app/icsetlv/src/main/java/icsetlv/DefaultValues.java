/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import java.util.HashSet;
import java.util.Set;

/**
 * @author LLT
 *
 */
public class DefaultValues {
	private DefaultValues(){}
	
	public static final int DEBUG_VALUE_RETRIEVE_LEVEL = 3;
	public static final Set<String> EXTRACT_IGNORE_REFERENCES = new HashSet<String>();
	static {
		EXTRACT_IGNORE_REFERENCES.add(Thread.class.getName());
	}
}
