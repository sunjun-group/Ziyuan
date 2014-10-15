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

import sav.commons.testdata.simplePrograms.DuplicateNumberTest;
import sav.commons.testdata.simplePrograms.Palindrome1Test;
import sav.commons.testdata.simplePrograms.Palindrome2Test;
import sav.commons.testdata.simplePrograms.SimplePrograms;

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
	public void runDuplicateNumber() throws Exception {
		faultLocalization(SimplePrograms.class.getName(),
				DuplicateNumberTest.class.getName());
	}
	
	@Test
	public void runPalindrome2() throws Exception {
		faultLocalization(SimplePrograms.class.getName(),
				Palindrome2Test.class.getName());
	}
	
	@Test
	public void runPalindrome1() throws Exception {
		faultLocalization(SimplePrograms.class.getName(),
				Palindrome1Test.class.getName());
	}
	
	public void faultLocalization(String program, String junit) throws Exception {
		TzuyuCore app = new TzuyuCore(testContext);
		List<String> testingClasses = new ArrayList<String>();
		testingClasses.add(program);
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add(junit);
		app.faultLocalization(testingClasses, junitClassNames);
	}

}
