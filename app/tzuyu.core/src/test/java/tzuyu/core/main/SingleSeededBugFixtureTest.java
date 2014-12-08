/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import sav.common.core.utils.StringUtils;
import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.commons.testdata.opensource.TestPackage;
import sav.commons.testdata.opensource.TestPackage.TestDataColumn;
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
	
	@Test
	public void testAll() {
		List<String> passTests = new ArrayList<String>();
		List<String> failTests = new ArrayList<String>();
		for (Entry<String, TestPackage> entry : TestPackage.getAllTestData().entrySet()) {
			try {
				runTest2(entry.getValue());
				passTests.add(entry.getKey());
			} catch (Throwable e) {
				e.printStackTrace();
				failTests.add(entry.getKey());
			}
		}
		System.out.println("pass tests: " + StringUtils.join(passTests, ", "));
		System.out.println("fail tests: " + StringUtils.join(failTests, ", "));
		Assert.assertTrue(failTests.isEmpty());
	}
	
	@Test
	public void testJavaParser46() throws Exception {
		runTest2(TestPackage.getPackage("javaparser", "46"));
	}
	
	public void runTest2(TestPackage testPkg) throws Exception {
		fixture.projectClassPaths(testPkg.getClassPaths());
		for (String libs : testPkg.getLibFolders()) {
			addLibs(libs);
		}
		for (String clazz : testPkg.getValues(TestDataColumn.ANALYZING_CLASSES)) {
			fixture.programClass(clazz);
		}
		for (String clazz : testPkg.getValues(TestDataColumn.TEST_CLASSES)) {
			fixture.programTestClass(clazz);
		}		
		List<String> expectedBugLocations = testPkg
				.getValues(TestDataColumn.EXPECTED_BUG_LOCATION);
		if (!expectedBugLocations.isEmpty()) {
			fixture.expectedBugLine(expectedBugLocations.get(0));
		}
		updateSystemClasspath(fixture.getContext().getProjectClasspath());
		fixture.analyze2(testPkg.getValues(TestDataColumn.ANALYZING_PACKAGES));
		Assert.assertTrue(fixture.bugWasFound());
	}
	
	@Test
	public void testCommonsLang() throws Exception {
		String prjFolder = TESTCASE_BASE + "/commons-lang";
		fixture.projectClassPath(prjFolder + "/trunk/target/classes");
		fixture.projectClassPath(prjFolder  + "/trunk/target/test-classes");
		addLibs(prjFolder + "/bin/libs");
		fixture.programClass("org.apache.commons.lang3.AnnotationUtils");
		fixture.programTestClass("org.apache.commons.lang3.AnnotationUtilsTest");
		fixture.expectedBugLine("org.apache.commons.lang3.AnnotationUtils:56");
		updateSystemClasspath(fixture.getContext().getProjectClasspath());
		fixture.analyze();
		Assert.assertTrue(fixture.bugWasFound());
	}

	@Test
	public void testApacheXmlSecurity() throws Exception {
		String prjFolder = TESTCASE_BASE + "/apache-xml-security";
		String libs = prjFolder + "/libs";
		fixture.projectClassPath(prjFolder + "/v1/s1/classes");
		addLibs(libs);
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
		updateSystemClasspath(fixture.getContext().getProjectClasspath());
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
		updateSystemClasspath(fixture.getContext().getProjectClasspath());
		fixture.analyze();
		Assert.assertTrue(fixture.bugWasFound());
	}

	private void addLibs(String... libFolders) throws Exception {
		for (String jar : getLibJars(libFolders)) {
			fixture.projectClassPath(jar);
		}
	}
}
