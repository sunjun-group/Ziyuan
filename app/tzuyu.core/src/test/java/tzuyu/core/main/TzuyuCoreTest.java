/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sav.commons.testdata.SimplePrograms;
import sav.commons.testdata.palindrome.PalindromeTest;

/**
 * @author LLT
 *
 */
public class TzuyuCoreTest extends AbstractTzTest {
	
	@Before
	public void setup() {
		List<String> projectClasspath = testContext.getProjectClasspath();
		projectClasspath.add(
				config.getTarget("slicer.javaslicer"));
		projectClasspath.add(config.getJunitLib());
	}
	
	@Test
	public void testFaultLocalization() throws Exception {
		TzuyuCore app = new TzuyuCore(testContext);
		List<String> testingClasses = new ArrayList<String>();
		testingClasses.add(SimplePrograms.class.getName());
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add(PalindromeTest.class.getName());
		app.faultLocalization(testingClasses, junitClassNames);
	}

}
