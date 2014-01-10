/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.reporter;

import tzuyu.plugin.TzuyuPlugin;
import tzuyu.plugin.core.exception.ErrorType;

/**
 * @author LLT
 * for logging in plugin.
 */
public class PluginLogger {
	
	public static void logEx(Exception e) {
		
	}

	public static void logEx(Exception e, String msg) {
		
	}

	public static void logEx(Exception e, ErrorType errorType) {
		System.out.println(TzuyuPlugin.getMessages().getMessage(errorType));
	}

}
