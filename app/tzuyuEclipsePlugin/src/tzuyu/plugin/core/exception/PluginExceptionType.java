/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.core.exception;

/**
 * @author LLT
 *
 */
public enum PluginExceptionType {
	SELECTION_MORE_THAN_ONE_PROJ_SELECTED;

	// TODO [LLT, to review)
	public static String getMsg(PluginExceptionType type) {
		return type.name();
	}
}
