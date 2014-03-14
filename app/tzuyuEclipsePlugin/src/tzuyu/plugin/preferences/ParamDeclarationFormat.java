/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.preferences;

/**
 * @author LLT
 *
 */
public enum ParamDeclarationFormat {
	SHORT,
	LONG;
	
	public static ParamDeclarationFormat getTypeIf(boolean longFormat) {
		if (longFormat) {
			return LONG;
		}
		return SHORT;
	}
	
	public boolean isLongFormat() {
		return this == LONG;
	}
}
