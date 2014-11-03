/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.commons.utils.TestConfigUtils;

/**
 * @author LLT
 *
 */
public class SingleSeededBugFixtureTest extends AbstractTest {
	private static final String TESTCASE_BASE = TestConfiguration.TESTCASE_BASE;
	private SingleSeededBugFixture fixture;
	
	@Before
	public void setup() throws FileNotFoundException {
		fixture = new SingleSeededBugFixture();
		fixture.useSlicer(true);
		fixture.javaHome(TestConfigUtils.getJavaHome());
		fixture.tracerJarPath(TestConfigUtils.getTracerLibPath());
		fixture.projectClassPath(TestConfiguration.getTarget("slicer.javaslicer"));
	}

//	@Test
	public void testApacheXmlSecurity() throws Exception {
		String prjFolder = TESTCASE_BASE + "/apache-xml-security";
		String libs = prjFolder + "/libs";
		fixture.projectClassPath(prjFolder + "/v1/s1/classes");
//		fixture.projectClassPath(prjFolder + "/libs/*");
		fixture.projectClassPath(libs + "/bc-jce-jdk13-114.jar");
		fixture.projectClassPath(libs + "/commons-logging-api.jar");
		fixture.projectClassPath(libs + "/commons-logging.jar");
		fixture.projectClassPath(libs + "/junit-4.8.2.jar");
		fixture.projectClassPath(libs + "/junit3.8.1.jar");
		fixture.projectClassPath(libs + "/junitSIR.jar");
		fixture.projectClassPath(libs + "/log4j-1.2.8.jar");
		fixture.projectClassPath(libs + "/style-apachexml.jar");
		fixture.projectClassPath(libs + "/stylebook-1.0-b3_xalan-2.jar");
		fixture.projectClassPath(libs + "/xalan.jar");
		fixture.projectClassPath(libs + "/xercesImpl.jar");
		fixture.projectClassPath(libs + "/xml-apis.jar");
		fixture.projectClassPath(libs + "/xmlParserAPIs.jar");
		fixture.programClass("org.apache.xml.security.c14n.implementations.Canonicalizer20010315Excl");
		fixture.programClass("org.apache.xml.security.c14n.implementations.Canonicalizer20010315ExclOmitComments");
		fixture.programClass("org.apache.xml.security.transforms.params.InclusiveNamespaces");
		fixture.programClass("org.apache.xml.security.utils.IdResolver");
		fixture.programClass("org.apache.xml.security.utils.XMLUtils");
		fixture.programClass("org.apache.xml.security.c14n.helper.C14nHelper");
		fixture.programClass("org.apache.xml.security.c14n.implementations.Canonicalizer20010315");
		fixture.programClass("org.apache.xml.security.c14n.Canonicalizer");
		fixture.programClass("org.apache.xml.security.signature.SignedInfo");
		fixture.programClass("org.apache.xml.security.signature.XMLSignatureInput");
		fixture.programTestClass("org.apache.xml.security.test.AllTests");
		fixture.expectedBugLine("org.apache.xml.security.c14n.implementations.Canonicalizer20010315Excl:96");
		fixture.updateSysClassLoader();
		fixture.analyze();
	}
	
	@Test
	public void test() throws Exception {
		fixture.projectClassPath(TestConfigUtils.getConfig("jtopas.src"));
		fixture.projectClassPath(TestConfigUtils.getConfig("jtopas.test"));
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
		fixture.expectedBugLine("de.susebox.java.util.AbstractTokenizer:766");
		fixture.updateSysClassLoader();
		fixture.analyze();
		Assert.assertTrue(fixture.bugWasFound());
	}
}
