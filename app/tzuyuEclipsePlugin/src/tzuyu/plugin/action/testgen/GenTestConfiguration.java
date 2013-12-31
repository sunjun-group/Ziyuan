/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.plugin.action.testgen;

import org.eclipse.debug.core.ILaunchConfiguration;

import tzuyu.plugin.core.dto.RunConfiguration;

/**
 * @author LLT
 * 
 */
public class GenTestConfiguration extends RunConfiguration {
	public static final String CONFIG_NAME = "Start GenTest";
	
	private ILaunchConfiguration config;
	
	public GenTestConfiguration(ILaunchConfiguration config) {
		this.config = config;
	}

	public ILaunchConfiguration getLaunchConfig() {
		return config;
	}
	
	
}
