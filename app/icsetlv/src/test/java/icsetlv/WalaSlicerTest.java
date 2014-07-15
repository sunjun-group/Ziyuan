/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.slicer.SlicerInput;
import icsetlv.slicer.WalaSlicer;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ibm.wala.util.collections.Pair;

/**
 * @author LLT
 *
 */
public class WalaSlicerTest extends AbstractTest {
	
	@Test
	public void testSlice() throws IcsetlvException {
		WalaSlicer slicer = new WalaSlicer();
		SlicerInput input = new SlicerInput();
		input.setAppBinFolder(config.getAppBinpath());
		input.setJre(config.getJavahome());
		// entry points
		List<Pair<String, String>> classEntryPoints = new ArrayList<Pair<String,String>>();
		classEntryPoints.add(Pair.make("Ltestdata/slice/FindMaxCallerFailTest1", "test2"));
		classEntryPoints.add(Pair.make("Ltestdata/slice/FindMaxCallerPassTest1", "test1"));
		input.setClassEntryPoints(classEntryPoints);
		// breakpoints
		List<BreakPoint> breakpoints = new ArrayList<BreakPoint>();
		BreakPoint bkp1 = new BreakPoint("testdata.slice.FindMaxCaller", "test1");
		bkp1.setLineNo(27);
		
		breakpoints.add(bkp1);
		input.setBreakpoints(breakpoints);
		List<BreakPoint> slicingResult = slicer.slice(input);
		printBkps(slicingResult);
	}
}
