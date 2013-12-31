/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.core.exception;

import org.eclipse.core.runtime.CoreException;

/**
 * @author LLT
 *
 */
public class PluginException extends Exception {
	private static final long serialVersionUID = 1L;
	private PluginExceptionType type;
	
	public PluginException() {}
	
	public PluginException(PluginExceptionType type) {
		super(PluginExceptionType.getMsg(type));
		this.type = type;
	}

	public PluginExceptionType getType() {
		return type;
	}

	public static void wrapEx(Exception e) throws PluginException {
		throw new PluginException();
	}
}
