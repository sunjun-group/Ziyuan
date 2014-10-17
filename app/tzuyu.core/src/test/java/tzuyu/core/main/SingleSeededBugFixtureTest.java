/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

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
		fixture.projectClassPath(TestConfigUtils.getConfig("jtopas.scr"));
		fixture.projectClassPath(TestConfigUtils.getConfig("jtopas.test"));
		fixture.projectClassPath(TestConfiguration.getInstance().getTarget(
				"slicer.javaslicer"));
		fixture.useSlicer(false);
		fixture.programClass("de.susebox.java.io.ExtIOException");
		fixture.programClass("de.susebox.java.lang.ExtIndexOutOfBoundsException");
		fixture.programClass("de.susebox.java.util.InputStreamTokenizer");
		fixture.programClass("de.susebox.jtopas.PluginTokenizer");
		fixture.programTestClass("de.susebox.java.util.TestTokenizerProperties");
		fixture.programTestClass("de.susebox.java.util.TestTokenProperties");
		fixture.programTestClass("de.susebox.java.util.TestInputStreamTokenizer");
		fixture.programTestClass("de.susebox.java.util.TestDifficultSituations");
		fixture.programTestClass("de.susebox.jtopas.TestPluginTokenizer");
		fixture.programTestClass("de.susebox.jtopas.TestTokenizerSpeed");
		fixture.programTestClass("de.susebox.jtopas.TestJavaTokenizing");
		
		SystemConfiguredDataProvider context = fixture.getContext();
		for (String path : context.getProjectClassPath()) {
			addSoftwareLibrary(new File(path));
		}
		fixture.analyze();
	}
	
	private static void addSoftwareLibrary(File file) throws Exception {
	    Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
	    method.setAccessible(true);
	    method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
	}
}
