/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import icsetlv.common.dto.BreakPoint;
import icsetlv.vm.VMConfiguration;

import java.util.List;

import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 * 
 */
public class AbstractTest {
	protected static TestConfiguration config = TestConfiguration.getInstance();
	
	public void print(Object... objs) {
		System.out.println(StringUtils.spaceJoin(objs));
	}

	public void printBkps(List<BreakPoint> breakpoints) {
		for (BreakPoint bkp : breakpoints) {
			printBreakpoint(bkp);
		}
	}

	public void printBreakpoint(BreakPoint bkp) {
		System.out.println(bkp);
	}

	protected VMConfiguration initVmConfig() {
		VMConfiguration vmConfig = new VMConfiguration();
		vmConfig.setJavaHome(config.getJavahome());
		vmConfig.setDebug(true);
		vmConfig.setPort(config.getVmDefaultPort());
		vmConfig.setClazzName(config.getJunitcore());
		vmConfig.addClasspath(config.getJavaBin());
		vmConfig.addClasspath(config.getAppBinpath());
		vmConfig.addClasspath(config.getJunitLib());
		return vmConfig;
	}
}
