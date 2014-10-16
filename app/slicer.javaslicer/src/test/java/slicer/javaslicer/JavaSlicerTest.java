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

import sav.common.core.SavPrintStream;
import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.commons.testdata.SampleProgramTest;
import sav.commons.testdata.SamplePrograms;
import sav.commons.utils.TestConfigUtils;
import sav.strategies.dto.BreakPoint;
import sav.strategies.vm.VMConfiguration;


/**
 * @author LLT
 *
 */
public class JavaSlicerTest extends AbstractTest {
	private JavaSlicer slicer;
	
	@Before
	public void setup() {
		slicer = new JavaSlicer();
		VMConfiguration vmConfig = initVmConfig();
		vmConfig.addClasspath(TestConfiguration.getInstance().getTarget(
				"slicer.javaslicer"));
		slicer.setVmRunnerPrintStream(new SavPrintStream(System.out));
		slicer.setTracerJarPath(TestConfigUtils.getTracerLibPath());
		slicer.setVmConfig(vmConfig);
	}

	@Test
	public void testSlice() throws Exception {
		String sampleProgramsClassName = SamplePrograms.class.getName();
		String sampleProgramTestClassName = SampleProgramTest.class.getName();
		slicer.setAnalyzedClasses(Arrays.asList(sampleProgramsClassName));
		BreakPoint bkp1 = new BreakPoint(sampleProgramTestClassName, "test5", 53);
		BreakPoint bkp2 = new BreakPoint(sampleProgramsClassName, "Max", 10);
		List<BreakPoint> breakpoints = Arrays.asList(bkp2);
		List<BreakPoint> result = slicer.slice(breakpoints,
				Arrays.asList(sampleProgramTestClassName));
		for (BreakPoint bkp : result) {
			System.out.println(bkp);
		}
	}
	
//	@Test
	public void testInnerSlice() throws InterruptedException {
		List<BreakPoint> breakpoints = Arrays.asList(new BreakPoint(
				"faultLocaliation.sample.SampleProgramTest", "test5", 53),

				new BreakPoint("faultLocaliation.sample.SampleProgram", 
						"Max", 26));
		slicer.slice("/tmp/javaSlicer.trace", breakpoints);
	}
}
