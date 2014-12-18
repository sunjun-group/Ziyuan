/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.value.generator;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * @author LLT
 *
 */
public class SetValueGenerator extends ListValueGenerator {

	public SetValueGenerator(Type type) {
		super(HashSet.class, type);
	}
	
	public static boolean accept(Class<?> type) {
		return type == Set.class;
	}

	
}
