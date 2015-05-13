/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import sav.common.core.Constants;
import sav.common.core.SavRtException;
import sav.commons.TestConfiguration;
import sav.commons.utils.TestConfigUtils;
import tzuyu.core.inject.ApplicationData;
import tzuyu.core.main.context.AbstractApplicationContext;
import de.unisb.cs.st.javaslicer.tracer.Tracer;
import faultLocalization.SpectrumBasedSuspiciousnessCalculator.SpectrumAlgorithm;


/**
 * @author LLT
 *
 */
public class TestApplicationContext extends AbstractApplicationContext {
	private SpectrumAlgorithm suspiciousnessCalcul;
	protected List<String> projectClasspath;

	public TestApplicationContext() {
		ApplicationData appData = new ApplicationData();
		projectClasspath = new ArrayList<String>();
		projectClasspath.add(TestConfiguration.SAV_COMMONS_TEST_TARGET);
		appData.setClasspaths(projectClasspath);
		appData.setTracerJarPath(getTracerJarPath());
		appData.setJavaHome(TestConfigUtils.getJavaHome());
		appData.setSuspiciousCalculAlgo(suspiciousnessCalcul);
		appData.setTzuyuJacocoAssembly(TestConfiguration.getTzAssembly(Constants.TZUYU_JACOCO_ASSEMBLY));
		appData.setAppSrc(TestConfiguration.getTestScrPath("sav.commons"));
		appData.setAppTarget(TestConfiguration.getTestTarget("sav.commons"));
		appData.setAppTestTarget(appData.getAppTarget());
		setAppData(appData);
	}
	
	protected String getTracerJarPath() {
		String path = Tracer.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		System.out.println(path);
		try {
			return URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new SavRtException("cannot get path of Tracer.jar");
		}
//		return TestConfigUtils.getTracerLibPath();
	}
}
