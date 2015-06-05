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

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;

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
	protected static final String ICSETLV = "icsetlv";
	protected static TestConfiguration config = TestConfiguration.getInstance();
	
	@BeforeClass
	public static void init() throws Exception {
		ResourceBundle log4jtest = ResourceBundle.getBundle("test-log4j");
		Properties props = new Properties();
		for (String key : log4jtest.keySet()) {
			props.setProperty(key, log4jtest.getString(key));
		}
		PropertyConfigurator.configure(props);
	}
	
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
	
	protected List<String> getLibJars(String... libFolders) throws Exception {
		List<String> jars = new ArrayList<String>();
		for (String libFolder : libFolders) {
			Collection<?> files = FileUtils.listFiles(new File(libFolder),
					new String[] { "jar" }, true);
			for (Object obj : files) {
				File file = (File) obj;
				jars.add(file.getAbsolutePath());
			}
		}
		return jars;
	}
	
	protected void updateSystemClasspath(List<String> classpaths)
			throws Exception {
		for (String path : classpaths) {
			TestConfigUtils.addToSysClassLoader(new File(path));
		}
		
	}
}
