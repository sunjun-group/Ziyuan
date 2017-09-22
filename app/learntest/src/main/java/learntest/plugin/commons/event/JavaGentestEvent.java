/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.commons.event;

import learntest.plugin.commons.data.JavaModelRuntimeInfo;

/**
 * @author LLT
 *
 */
public class JavaGentestEvent implements ILearntestEvent {
	private JavaModelRuntimeInfo runtimeInfo;

	public JavaGentestEvent(JavaModelRuntimeInfo runtimeInfo) {
		this.runtimeInfo = runtimeInfo;
	}

	public JavaModelRuntimeInfo getRuntimeInfo() {
		return runtimeInfo;
	}
}
