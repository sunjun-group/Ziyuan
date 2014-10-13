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

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import sav.commons.AbstractTest;
import sav.commons.TzuyuTestCase;
import sav.strategies.dto.BreakPoint;


/**
 * @author LLT
 *
 */
public class JavaSlicerTest extends AbstractTest {
	private JavaSlicer slicer;
	
	@Before
	public void setup() {
		slicer = new JavaSlicer();
	}

	@Test
	@Category(TzuyuTestCase.class)
	public void testSlice() throws Exception {
		slicer.setAnalyzedClasses(Arrays.asList("faultLocaliation.sample.SamplePrograms"));
		List<BreakPoint> breakpoints = Arrays.asList(new BreakPoint(
				"faultLocaliation.sample.SampleProgramTest", "test5", 53),
				new BreakPoint("faultLocaliation.sample.SamplePrograms", 
						"Max", 26));
		List<BreakPoint> result = slicer.slice(breakpoints,
				Arrays.asList("faultLocaliation.sample.SampleProgramTest"));
		for (BreakPoint bkp : result) {
			System.out.println(bkp);
		}
	}
	
	@Test
	@Category(TzuyuTestCase.class)
	public void testInnerSlice() throws InterruptedException {
		List<BreakPoint> breakpoints = Arrays.asList(new BreakPoint(
				"faultLocaliation.sample.SampleProgramTest", "test5", 53),

				new BreakPoint("faultLocaliation.sample.SampleProgram", 
						"Max", 26));
		slicer.slice("/tmp/javaSlicer.trace", breakpoints);
	}
}
