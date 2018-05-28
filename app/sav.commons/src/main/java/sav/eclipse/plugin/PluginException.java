/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.eclipse.plugin;

/**
 * @author LLT
 *
 */
public class PluginException extends Exception {
	private static final long serialVersionUID = 1L;

	public PluginException(String message) {
		super(message);
	}
	
	public PluginException(Exception e) {
		super(e);
	}

	public static PluginException wrapEx(Exception e) {
		return new PluginException(e.getMessage());
	}

}
