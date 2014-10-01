/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocaliation;

import icsetlv.common.dto.BreakPoint;

import java.util.Arrays;
import java.util.List;

import javaslicer.JavaSlicer;
import main.IDataProvider;

import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author LLT
 *
 */
public class JavaSlicerTest extends AbstractFLTest {

	@Test
	@Category(sg.edu.sutd.test.core.TzuyuTestCase.class)
	public void testSlice() throws Exception {
		IDataProvider dataProvider = getDataProvider();
		JavaSlicer slicer = (JavaSlicer)dataProvider.getSlicer();
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
	@Category(sg.edu.sutd.test.core.TzuyuTestCase.class)
	public void testInnerSlice() throws InterruptedException {
		IDataProvider dataProvider = getDataProvider();
		JavaSlicer slicer = (JavaSlicer)dataProvider.getSlicer();
		List<BreakPoint> breakpoints = Arrays.asList(new BreakPoint(
				"faultLocaliation.sample.SampleProgramTest", "test5", 53),

				new BreakPoint("faultLocaliation.sample.SampleProgram", 
						"Max", 26));
		slicer.slice("/tmp/javaSlicer.trace", breakpoints);
	}
}
