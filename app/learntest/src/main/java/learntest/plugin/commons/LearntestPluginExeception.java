/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.commons;

import learntest.core.commons.exception.LearnTestException;

/**
 * @author LLT
 *
 */
public class LearntestPluginExeception extends LearnTestException {
	private static final long serialVersionUID = 1L;

	public LearntestPluginExeception(String message) {
		super(message);
	}
	
	public LearntestPluginExeception(Exception e) {
		super(e);
	}

}
