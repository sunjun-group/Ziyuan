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
	private TzuyuCore2 tzCore;
	
	@Before
	public void setup() {
		super.setup();
		tzCore = new TzuyuCore2(context, appData);
		tzCore.setEnableGentest(false);
		tzCore.setRankToExamine(3);
	}
	
	public void runFaultLocate(TestPackage testPkg) throws Exception {
		tzCore.setEnableGentest(false);
		prepare(testPkg);
		tzCore.faultLocate(testingClassNames, 
				testingPackages, junitClassNames, isUseSlicer());
	}
	
	/**
	 * test part
	 */
	
	@Test
	public void testjavaparser46() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("javaparser", "46");
		setUseSlicer(true);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testjavaparser57() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("javaparser", "57");
		setUseSlicer(true);
		runFaultLocate(testPkg);
	}
	
	@Test
	public void testjodatime194() throws Exception {
		TestPackage testPkg = TestPackage.getPackage("joda-time", "194");
		setUseSlicer(false);
		tzCore.setRankToExamine(7);
		runFaultLocate(testPkg);
	}
}
