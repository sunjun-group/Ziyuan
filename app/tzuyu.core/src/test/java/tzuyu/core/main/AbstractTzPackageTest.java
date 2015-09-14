/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;

import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.commons.testdata.opensource.TestPackage;
import sav.commons.testdata.opensource.TestPackage.TestDataColumn;
import sav.strategies.IApplicationContext;
import tzuyu.core.inject.ApplicationData;
import tzuyu.core.main.context.AbstractApplicationContext;
import faultLocalization.SpectrumBasedSuspiciousnessCalculator.SpectrumAlgorithm;

/**
 * @author LLT
 *
 */
public class AbstractTzPackageTest extends AbstractTest {
	protected IApplicationContext context;
	protected ApplicationData appData;
	protected boolean useSlicer = false;
	protected List<String> testingClassNames;
	protected List<String> testingPackages;
	protected List<String> junitClassNames;
	
	@Before
	public void setup() {
		AbstractApplicationContext context = new AbstractApplicationContext() {
		};
		appData = new ApplicationData();
		appData.setJavaHome(TestConfiguration.JAVA_HOME);
		appData.setSuspiciousCalculAlgo(SpectrumAlgorithm.TARANTULA);
		context.setAppData(appData);
		this.context = context;
	}
	
	public List<String> prepare(TestPackage testPkg) throws Exception {
		appData.setAppSrc(testPkg.getValue(TestDataColumn.SOURCE_FOLDER));
		appData.setAppTarget(testPkg.getValue(TestDataColumn.TARGET_FOLDER));
		appData.setAppTestTarget(testPkg.getValue(TestDataColumn.TEST_TARGET_FOLDER));
		appData.addClasspaths(testPkg.getClassPaths());
		for (String libs : testPkg.getLibFolders()) {
			addLibs(libs);
		}
		testingClassNames = testPkg.getValues(TestDataColumn.ANALYZING_CLASSES);
		testingPackages = testPkg.getValues(TestDataColumn.ANALYZING_PACKAGES);
		junitClassNames = testPkg.getValues(TestDataColumn.TEST_CLASSES);
		
		List<String> expectedBugLocations = testPkg.getValues(TestDataColumn.EXPECTED_BUG_LOCATION);
		
		updateSystemClasspath(appData.getAppClasspaths());
		return expectedBugLocations;
	}
	
	private void addLibs(String... libFolders) throws Exception {
		for (String libFolder : libFolders) {
			Collection<?> files = FileUtils.listFiles(new File(libFolder),
					new String[] { "jar" }, true);
			for (Object obj : files) {
				File file = (File) obj;
				appData.addClasspath(file.getAbsolutePath());
			}
		}
	}
	
	public void setUseSlicer(boolean useSlicer) {
		this.useSlicer = useSlicer;
	}
	
	public boolean isUseSlicer() {
		return useSlicer;
	}
}
