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

import faultLocalization.SpectrumBasedSuspiciousnessCalculator.SpectrumAlgorithm;

import sav.common.core.Constants;
import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.commons.testdata.opensource.TestPackage;
import sav.commons.testdata.opensource.TestPackage.TestDataColumn;
import sav.strategies.IApplicationContext;
import tzuyu.core.inject.ApplicationData;
import tzuyu.core.main.context.AbstractApplicationContext;

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
		appData.setJavaHome("D:/_1_Projects/Tzuyu/tools/jdk1.6.0_26-64b");
		appData.addClasspath(TestConfiguration.getTarget("slicer.javaslicer"));
		appData.addClasspath(TestConfiguration.getTzAssembly(Constants.SAV_COMMONS_ASSEMBLY));
		appData.addClasspath(appData.getJavaHome() + "/bin");
		appData.setSuspiciousCalculAlgo(SpectrumAlgorithm.TARANTULA);
		appData.setTzuyuJacocoAssembly(TestConfiguration.getTzAssembly(Constants.TZUYU_JACOCO_ASSEMBLY));
		context.setAppData(appData);
		this.context = context;
	}
	
	public List<String> prepare(TestPackage testPkg) throws Exception {
		appData.setAppSrc(testPkg.getValue(TestDataColumn.SOURCE_FOLDER));
		appData.setAppTarget(testPkg.getValue(TestDataColumn.TARGET_FOLDER));
		appData.setAppTestTarget(testPkg.getValue(TestDataColumn.TEST_TARGET_FOLDER));
		appData.getAppClasspaths().addAll(testPkg.getClassPaths());
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
