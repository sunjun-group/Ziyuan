/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.commons;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import sav.common.core.utils.StringUtils;
import sav.commons.utils.ConfigUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.vm.VMConfiguration;


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
		vmConfig.setJavaHome(ConfigUtils.getJavaHome());
		vmConfig.setDebug(true);
		vmConfig.setPort(findFreePort());
		vmConfig.setLaunchClass(config.getJunitcore());
		vmConfig.addClasspath(config.getJavaBin());
		vmConfig.addClasspath(config.testTarget);
		vmConfig.addClasspath(config.getJunitLib());
		return vmConfig;
	}
	
	public static int findFreePort() {
		ServerSocket socket= null;
		try {
			socket= new ServerSocket(0);
			return socket.getLocalPort();
		} catch (IOException e) { 
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
		return -1;		
	}	
}
