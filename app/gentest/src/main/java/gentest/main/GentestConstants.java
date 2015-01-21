/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest.main;

import gentest.core.value.generator.ArrayValueGenerator;
import gentest.core.value.generator.ExtObjectValueGenerator;
import gentest.core.value.generator.ValueGenerator;

/**
 * @author LLT
 *
 */
public class GentestConstants {
	private GentestConstants() {}
	public static final int DEFAULT_QUERY_MAX_LENGTH = 4;
	public static final int DEFAULT_TEST_PER_QUERY = 5;
	/** level of generated value from root statment
	 * ex: generate value for parameter p1 of method:
	 * methodA(List<Interger> p1)
	 * we do 2 generation step:
	 * generate list -> level 1
	 * generate values for list -> level 2
	 * see {@link ValueGenerator} 
	 * */
	public static final int VALUE_GENERATION_MAX_LEVEL = 10;
	/**
	 * see {@link ArrayValueGenerator}
	 * NOTE This must be less than 255
	 */
	public static final int VALUE_GENERATION_ARRAY_MAXLENGTH = 10;
	public static final int OBJECT_VALUE_GENERATOR_MAX_SELECTED_METHODS = 10;
	public static final double PROBABILITY_OF_CACHE_VALUE = 0.5;
	public static final double PROBABILITY_OF_PUBLIC_NO_PARAM_CONSTRUCTOR = 0.99;
	public static final int MAX_VALUE_FOR_A_CLASS_STORED_IN_CACHE = 50; 
	/**
	 * for {@link ExtObjectValueGenerator}
	 */
	public static final String[] OBJ_INIT_EXCLUDED_METHOD_PREFIXIES = new String[] {
			"get", "is", "equal" };
	public static final Class<?>[] CANDIDATE_DELEGATES_FOR_OBJECT = new Class<?>[] {
			Integer.class, Long.class, String.class, Short.class, Byte.class };
	public static final Class<?>[] CANDIDATE_DELEGATES_FOR_NUMBER = new Class<?>[] {
		Integer.class, Long.class, Short.class
	};
	public static final double PROBABILITY_OF_UNCLOSED_SUBTYPES = 0.1;
	public static final char PACKAGE_SEPARATOR = '.';
}
