/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.util.List;

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
		params.setRankToExamine(3);
		params.setRunMutation(false);
	}
	
	@Override
	public List<String> prepare(TestPackage testPkg) throws Exception {
		List<String> result = super.prepare(testPkg);
		
		return result;
	}
	
	public void runFaultLocate(TestPackage testPkg) throws Exception {
		prepare(testPkg);
		params.setTestingClassNames(testingClassNames);
		params.setTestingPkgs(testingPackages);
		params.setJunitClassNames(junitClassNames);
		params.setUseSlicer(isUseSlicer());
		tzCore.faultLocate(params );
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
		params.setRankToExamine(7);
		setUseSlicer(false);
		runFaultLocate(testPkg);
	}
}
