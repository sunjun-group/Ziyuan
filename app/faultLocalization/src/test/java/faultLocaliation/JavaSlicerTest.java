/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocaliation;

import java.util.Arrays;
import java.util.List;

import javaslicer.JavaSlicer;

import org.junit.Test;

import de.unisb.cs.st.javaslicer.slicing.Slicer;

import icsetlv.common.dto.BreakPoint;
import icsetlv.iface.ISlicer;
import main.IDataProvider;

/**
 * @author LLT
 *
 */
public class JavaSlicerTest extends AbstractFLTest {

	@Test
	public void testSlice() throws Exception {
		IDataProvider dataProvider = getDataProvider();
		ISlicer slicer = dataProvider.getSlicer();
		List<BreakPoint> breakpoints = Arrays.asList(new BreakPoint(
				"faultLocaliation.sample.SampleProgramTest", "test5", 53),
				new BreakPoint("faultLocaliation.sample.SamplePrograms", 
						"Max", 26));
		List<BreakPoint> result = slicer.slice(breakpoints,
				Arrays.asList("faultLocaliation.sample.SampleProgramTest"));
		System.out.println(result);
	}
	
	@Test
	public void testInnerSlice() throws InterruptedException {
		IDataProvider dataProvider = getDataProvider();
		JavaSlicer slicer = (JavaSlicer)dataProvider.getSlicer();
		List<BreakPoint> breakpoints = Arrays.asList(new BreakPoint(
				"faultLocaliation.sample.SampleProgramTest", "test5", 53),
				new BreakPoint("faultLocaliation.sample.SamplePrograms", 
						"Max", 26));
		slicer.slice("/tmp/javaSlicer.trace", breakpoints);
	}
	
	@Test
	public void test() throws InterruptedException {
		Slicer.main(new String[] {
				"-p",
				"/tmp/javaSlicer.trace",
				"faulLocalisation.SamplePrograms.Max:26:*"
		// "faulLocalisation.Main.main:64"
		// "faulLocalisation.FindMax.findMax:14:*"
		});
	}
}
