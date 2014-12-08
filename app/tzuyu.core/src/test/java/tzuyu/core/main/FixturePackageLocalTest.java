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
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.commons.testdata.opensource.TestPackage;
import sav.commons.testdata.opensource.TestPackage.TestDataColumn;
import sav.commons.utils.TestConfigUtils;

/**
 * @author LLT
 *
 */
public class FixturePackageLocalTest extends AbstractTest {
	private SingleSeededBugFixture fixture;
	
	@Before
	public void setup() throws FileNotFoundException {
		fixture = new SingleSeededBugFixture();
		fixture.useSlicer(true);
		fixture.javaHome(TestConfigUtils.getJavaHome());
		fixture.tracerJarPath(TestConfigUtils.getTracerLibPath());
		fixture.projectClassPath(TestConfiguration.getTarget("slicer.javaslicer"));
	}
	
	public void runTest(TestPackage testPkg) throws Exception {
		List<String> expectedBugLocations = prepare(testPkg);
		if (fixture.useSlicer) {
			fixture.analyze2(testPkg.getValues(TestDataColumn.ANALYZING_PACKAGES));
		} else {
			fixture.analyze();
		}
		Assert.assertTrue(expectedBugLocations.isEmpty() || fixture.bugWasFound());
	}
	
	public List<String> prepare(TestPackage testPkg) throws Exception {
		fixture.projectClassPaths(testPkg.getClassPaths());
		for (String libs : testPkg.getLibFolders()) {
			addLibs(libs);
		}
		if (!fixture.useSlicer) {
			for (String clazz : testPkg.getValues(TestDataColumn.ANALYZING_CLASSES)) {
				fixture.programClass(clazz);
			}
		}
		for (String clazz : testPkg.getValues(TestDataColumn.TEST_CLASSES)) {
			fixture.programTestClass(clazz);
		}		
		List<String> expectedBugLocations = testPkg.getValues(TestDataColumn.EXPECTED_BUG_LOCATION);
		if (!expectedBugLocations.isEmpty()) {
			fixture.expectedBugLine(expectedBugLocations.get(0));
		}
		updateSystemClasspath(fixture.getContext().getProjectClasspath());
		return expectedBugLocations;
	}
	
	private void addLibs(String... libFolders) throws Exception {
		for (String libFolder : libFolders) {
			Collection<?> files = FileUtils.listFiles(new File(libFolder),
					new String[] { "jar" }, true);
			for (Object obj : files) {
				File file = (File) obj;
				fixture.projectClassPath(file.getAbsolutePath());
			}
		}
	}
	
	//	Generated part
	
	@Test
	public void testjavaparser46() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("javaparser", "46");
		fixture.useSlicer(true);
		runTest(testPkg);
	}
	
	@Test
	public void testjavaparser57() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("javaparser", "57");
		fixture.useSlicer(true);
		runTest(testPkg);
	}
	
	@Test
	public void testjavaparser63() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("javaparser", "63");
		fixture.useSlicer(false);
		runTest(testPkg);
	}
	
	@Test
	public void testjodatime194() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("joda-time", "194");
		fixture.useSlicer(false);
		runTest(testPkg);
	}
	
	@Test
	public void testjodatime201() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("joda-time", "201");
		fixture.useSlicer(true);
		runTest(testPkg);
	}
	
	@Test
	public void testjodatime187() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("joda-time", "187");
		fixture.useSlicer(true);
		runTest(testPkg);
	}
	
	@Test
	public void testjavadiffutils18() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("java-diff-utils", "18");
		fixture.useSlicer(true);
		runTest(testPkg);
	}
	//	End generated part
}
