/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.engine;


/**
 * @author LLT
 *
 */
public class TzConstants {
	private TzConstants() {}
	
	private static final int ARRAY_MAX_LENGTH = 5;
	private static final boolean DEBUG_CHECKS = false;
	private static final boolean FORBIT_NULL = true;
	private static final boolean LONG_FORMAT = true;
	private static final boolean PRETTY_PRINT = true;
	private static final int STRING_MAX_LENGTH = 10;
	private static final int TESTS_PER_QUERY = 1;
	private static final boolean OBJECT_TO_INTEGER = true;
	private static final boolean INHERITED_METHOD = false;
	private static final int CLASS_MAX_DEPTH = 5;
	
	public static enum TzParamType {
		arrayMaxLength(ARRAY_MAX_LENGTH),
		classMaxDepth(CLASS_MAX_DEPTH),
		debugChecks(DEBUG_CHECKS),
		forbitNull(FORBIT_NULL),
		longFormat(LONG_FORMAT),
		prettyPrint(PRETTY_PRINT),
		stringMaxLength(STRING_MAX_LENGTH),
		testsPerQuery(TESTS_PER_QUERY),
		objectToInteger(OBJECT_TO_INTEGER),
		inheritMethod(INHERITED_METHOD);
		
		private Object defaultVal; 
		private TzParamType(Object defaltVal) {
			this.defaultVal = defaltVal;
		}
		
		public Object defaultVal() {
			return defaultVal;
		}
	}
}
