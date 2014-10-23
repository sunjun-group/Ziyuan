/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import sav.commons.TestConfiguration;
import sav.commons.utils.TestConfigUtils;
import tzuyu.core.main.context.SystemConfiguredDataProvider;

/**
 * @author LLT
 *
 */
public class SingleSeededBugFixtureTest {
	private SingleSeededBugFixture fixture;
	
	@Before
	public void setup() {
		fixture = new SingleSeededBugFixture();
	}
	
	@Test
	public void test() throws Exception {
		fixture.javaHome(TestConfigUtils.getJavaHome());
		fixture.tracerJarPath(TestConfigUtils.getTracerLibPath());
		fixture.projectClassPath(TestConfigUtils.getConfig("jtopas.src"));
		fixture.projectClassPath(TestConfigUtils.getConfig("jtopas.test"));
		fixture.projectClassPath(TestConfiguration.getTarget(
				"slicer.javaslicer"));
		fixture.useSlicer(false);
		fixture.programClass("de.susebox.java.io.ExtIOException");
		fixture.programClass("de.susebox.java.lang.ExtIndexOutOfBoundsException");
		fixture.programClass("de.susebox.java.util.InputStreamTokenizer");
		fixture.programClass("de.susebox.java.util.AbstractTokenizer");
		fixture.programTestClass("de.susebox.java.util.TestTokenizerProperties");
		fixture.programTestClass("de.susebox.java.util.TestTokenProperties");
		fixture.programTestClass("de.susebox.java.util.TestInputStreamTokenizer");
		fixture.programTestClass("de.susebox.java.util.TestDifficultSituations");
		fixture.programTestClass("de.susebox.jtopas.TestPluginTokenizer");
		fixture.programTestClass("de.susebox.jtopas.TestTokenizerSpeed");
		fixture.programTestClass("de.susebox.jtopas.TestJavaTokenizing");
		SystemConfiguredDataProvider context = fixture.getContext();
		fixture.expectedBugLine("de.susebox.java.util.AbstractTokenizer:766");
		for (String path : context.getProjectClassPath()) {
			TestConfigUtils.addToSysClassLoader(new File(path));
		}
		fixture.analyze();
		Assert.assertTrue(fixture.bugWasFound());
	}
}
