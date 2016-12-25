/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer;


import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import sav.common.core.utils.JunitUtils;
import sav.commons.testdata.SampleProgramTest;
import sav.commons.testdata.SamplePrograms;
import sav.strategies.dto.BreakPoint;


/**
 * @author LLT
 *
 */
public class JavaSlicerTest extends AbstractJavaSlicerTest {

	@Test
	public void testSampleProgram() throws Exception {
		String targetClass = SamplePrograms.class.getName();
		String testClass = SampleProgramTest.class.getName();
		BreakPoint bkp2 = new BreakPoint(testClass, "test2", 26);
		List<BreakPoint> breakpoints = Arrays.asList(bkp2);
		analyzedClasses = Arrays.asList(targetClass);
		testClassMethods = JunitUtils.extractTestMethods(Arrays
				.asList(testClass));
		run(breakpoints);
	}
	
}
