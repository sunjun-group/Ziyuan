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
	
	public static enum TzParamType {
		arrayMaxLength(5),
		classMaxDepth(5),
		stringMaxLength(10),
		longFormat(true),
		objectToInteger(true),
		testsPerQuery(1),
		debugChecks(false),
		inheritMethod(false),
		forbitNull(true),
		prettyPrint(true),
		printFailTests(true),
		printPassTests(true);
		
		private Object defaultVal; 
		private TzParamType(Object defaltVal) {
			this.defaultVal = defaltVal;
		}
		
		public Object defaultVal() {
			return defaultVal;
		}
	}
}
