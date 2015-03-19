/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sav.commons.TestConfiguration;
import sav.commons.testdata.simplePrograms.DuplicateNumberTest;
import sav.commons.testdata.simplePrograms.FindInRotatedSortedArrayTest;
import sav.commons.testdata.simplePrograms.MatchStringTest;
import sav.commons.testdata.simplePrograms.Palindrome1Test;
import sav.commons.testdata.simplePrograms.Palindrome2Test;
import sav.commons.testdata.simplePrograms.ReverseWordTest;
import sav.commons.testdata.simplePrograms.SearchInSortingMatrix1Test;
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
				TestConfiguration.getTarget("slicer.javaslicer"));
		projectClasspath.add(config.getJunitLib());
	}
	
	@Test
	public void runSearchInSortingMatrix1() throws Exception {
		faultLocalization(SimplePrograms.class.getName(),
				SearchInSortingMatrix1Test.class.getName());
	}
	
	@Test
	public void runMatchString() throws Exception {
		faultLocalization(SimplePrograms.class.getName(),
				MatchStringTest.class.getName());
	}
	
	@Test
	public void runFindInRotatedSortedArray() throws Exception {
		faultLocalization(SimplePrograms.class.getName(),
				FindInRotatedSortedArrayTest.class.getName());
	}
	
	@Test
	public void runDuplicateNumber() throws Exception {
		faultLocalization(SimplePrograms.class.getName(),
				DuplicateNumberTest.class.getName());
	}
	
	@Test
	public void test2() throws Exception{
		TzuyuCore app = new TzuyuCore(testContext);
		List<String> testingClasses = new ArrayList<String>();
		testingClasses.add(SimplePrograms.class.getName());
		testingClasses.add(Integer.class.getName());
		List<String> junitClassNames = new ArrayList<String>();
//		junitClassNames.add(DuplicateNumberTest.class.getName());
		junitClassNames.add(ReverseWordTest.class.getName());
		app.faultLocalization(testingClasses, junitClassNames, false);
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
//		app.faultLocalization(testingClasses, junitClassNames, false);
		app.faultLocalization2(null, Arrays.asList("sav.commons.testdata.simplePrograms"), junitClassNames, false);
	}
	
	public void run() {
		
	}

}
