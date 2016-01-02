/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.wala;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.commons.testdata.BoundedStack;
import sav.commons.testdata.Main;
import sav.strategies.dto.BreakPoint;


/**
 * @author LLT
 *
 */
public class WalaSlicerTest extends AbstractTest {
	
	@Test
	public void testSlice() throws Exception {
		SlicerInput input = new SlicerInput();
		input.setAppBinFolder(TestConfiguration.SAV_COMMONS_TEST_TARGET);
		input.setJre(TestConfiguration.getJavaHome());
		// entry points
		List<String[]> classEntryPoints = makeEntryPoints();
		input.setClassEntryPoints(classEntryPoints);
		// breakpoints
		List<BreakPoint> breakpoints = new ArrayList<BreakPoint>();
		addBreakpoints(breakpoints);
		WalaSlicer slicer = new WalaSlicer(input);
		List<BreakPoint> slicingResult = slicer.slice(null, breakpoints, new ArrayList<String>());
		printList(slicingResult);
	}

	private void addBreakpoints(List<BreakPoint> breakpoints) {
		breakpoints.add(new BreakPoint(BoundedStack.class.getName(), "push(Ljava/lang/Integer;)Z", 34));
//		breakpoints.add(new BreakPoint(Main.class.getName(), "main(Ljava/lang/Integer;)Z", 10));
	}

	private List<String[]> makeEntryPoints() {
		List<String[]> classEntryPoints = new ArrayList<String[]>();
		classEntryPoints.add(make("Lsav/commons/testdata/BoundedStack", "push(Ljava/lang/Integer;)Z"));
//		classEntryPoints.add(make("Lsav/commons/testdata/Main", "main(Ljava/lang/Integer;)Z"));
		return classEntryPoints;
	}
	
	private String[] make(String clazz, String method) {
		return new String[]{clazz, method};
	}
	
	protected SlicerInput initSlicerInput() {
		SlicerInput input = new SlicerInput();
		input.setAppBinFolder(TestConfiguration.SAV_COMMONS_TEST_TARGET);
		input.setJre(TestConfiguration.getJavaHome());
		return input;
	}
}
