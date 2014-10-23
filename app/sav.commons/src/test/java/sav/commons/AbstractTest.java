/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.commons;

import static sav.commons.TestConfiguration.JUNIT_CORE;
import static sav.commons.TestConfiguration.JUNIT_LIB;
import static sav.commons.TestConfiguration.SAV_COMMONS_TEST_TARGET;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import sav.common.core.utils.StringUtils;
import sav.commons.utils.TestConfigUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.vm.VMConfiguration;


/**
 * @author LLT
 * 
 */
public class AbstractTest {
	public static final String SAV_COMMONS = "sav.commons";
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
		vmConfig.setJavaHome(TestConfigUtils.getJavaHome());
		vmConfig.setDebug(true);
		vmConfig.setPort(findFreePort());
		vmConfig.setLaunchClass(JUNIT_CORE);
		vmConfig.addClasspath(config.getJavaBin());
		vmConfig.addClasspath(SAV_COMMONS_TEST_TARGET);
		vmConfig.addClasspath(JUNIT_LIB);
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
