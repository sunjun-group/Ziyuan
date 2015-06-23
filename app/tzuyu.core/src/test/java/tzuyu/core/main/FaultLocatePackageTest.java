/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import org.junit.Before;
import org.junit.Test;

import sav.commons.testdata.opensource.TestPackage;

/**
 * @author LLT
 *
 */
public class FaultLocatePackageTest extends AbstractTzPackageTest {
	private TzuyuCore tzCore;
	private FaultLocateParams params;
	
	@Before
	public void setup() {
		super.setup();
		tzCore = new TzuyuCore(context, appData);
		params = new FaultLocateParams();
		params.setMachineLearningEnable(true);
		params.setRankToExamine(3);
		params.setRunMutation(false);
		params.setUseSlicer(true);
	}
	
	public void runFaultLocate(TestPackage testPkg) throws Exception {
		prepare(testPkg);
		params.setTestingClassNames(testingClassNames);
		params.setTestingPkgs(testingPackages);
		params.setJunitClassNames(junitClassNames);
		tzCore.faultLocate(params);
	}
	
	/**
	 * test part
	 */
	
	/**
	 * ASTParserTokenManager: 2220
	 * & ASTParserTokenManager: 69
	 */
	@Test
	public void testjavaparser46() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("javaparser", "46");
		params.setUseSlicer(true);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testjavaparser57() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("javaparser", "57");
		params.setRankToExamine(2);
		params.setRunMutation(false);
		params.setUseSlicer(true);
		params.setMachineLearningEnable(true);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testjavaparser69() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("javaparser", "69");
		params.setUseSlicer(true);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testjodatime194() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("joda-time", "194");
		params.setRankToExamine(7);
		runFaultLocate(testPkg);
	}
	
	/**
	 * 
	 */
	@Test
	public void testDiffUtils18() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("java-diff-utils", "18");
		params.setRankToExamine(3);
		params.setUseSlicer(true);
		params.setRunMutation(false);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testDiffUtils10() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("java-diff-utils", "10");
		params.setRankToExamine(3);
		params.setUseSlicer(true);
		params.setRunMutation(false);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testDiffUtils12() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("java-diff-utils", "12");
		params.setRankToExamine(3);
		params.setUseSlicer(true);
		params.setRunMutation(false);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testDiffUtils20() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("java-diff-utils", "20");
		params.setRankToExamine(3);
		params.setUseSlicer(true);
		params.setRunMutation(false);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testCommonsMath1196() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("apache-commons-math", "1196");
		params.setRankToExamine(3);
		params.setUseSlicer(true);
		params.setRunMutation(false);
		runFaultLocate(testPkg);
	}
}
