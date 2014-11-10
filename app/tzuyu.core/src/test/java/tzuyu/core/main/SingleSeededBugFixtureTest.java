/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.commons.testdata.opensource.TestPackage;
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
		fixture.useSlicer(false);
		fixture.javaHome(TestConfigUtils.getJavaHome());
		fixture.tracerJarPath(TestConfigUtils.getTracerLibPath());
		fixture.projectClassPath(TestConfiguration.getTarget("slicer.javaslicer"));
	}
	
	@Test
	public void testJavaParser() throws Exception {
		runTest(TestPackage.JAVA_PARSER);
	}
	
	public void runTest(TestPackage testPkg) throws Exception {
		fixture.projectClassPaths(testPkg.classPaths);
		if (testPkg.libsPath != null) {
			addLibs(testPkg.libsPath);
		}
		for (String clazz : testPkg.analyzingClasses) {
			fixture.programClass(clazz);
		}
		for (String clazz : testPkg.testClasses) {
			fixture.programTestClass(clazz);
		}		
		fixture.expectedBugLine("japa.parser.ASTParser:1810");
		fixture.updateSysClassLoader();
		fixture.analyze();
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
		fixture.updateSysClassLoader();
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

	private void addLibs(String libFolder) throws Exception {
		Collection<?> files = FileUtils.listFiles(new File(libFolder), new String[] { "jar" }, true);
		for (Object obj : files) {
			File file = (File) obj;
			fixture.projectClassPath(file.getAbsolutePath());
		}
	}
}
