/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.core.exception;

import tzuyu.plugin.TzuyuPlugin;

/**
 * @author LLT
 *
 */
public class PluginException extends Exception {
	private static final long serialVersionUID = 1L;
	private ErrorType type;
	
	public PluginException() {}
	
	public PluginException(ErrorType type) {
		super(TzuyuPlugin.getMessages().getMessage(type));
		this.type = type;
	}

	public ErrorType getType() {
		return type;
	}

	public static void wrapEx(Exception e) throws PluginException {
		throw new PluginException();
	}
}
