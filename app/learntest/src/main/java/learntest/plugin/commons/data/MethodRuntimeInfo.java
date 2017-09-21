/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.commons.data;

import learntest.core.RunTimeInfo;

/**
 * @author LLT
 *
 */
public class MethodRuntimeInfo implements IModelRuntimeInfo {
	private RunTimeInfo runtimeInfo;
	
	public MethodRuntimeInfo(RunTimeInfo runtimeInfo) {
		this.runtimeInfo = runtimeInfo;
	}
}
