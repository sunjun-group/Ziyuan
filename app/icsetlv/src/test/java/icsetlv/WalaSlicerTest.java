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
	private TestData type = TestData.BOUNDED_STACK;
	
	@Test
	public void testSlice() throws IcsetlvException {
		WalaSlicer slicer = new WalaSlicer();
		SlicerInput input = new SlicerInput();
		input.setAppBinFolder(config.getAppBinpath());
		input.setJre(config.getJavahome());
		// entry points
		List<Pair<String, String>> classEntryPoints = makeEntryPoints();
		input.setClassEntryPoints(classEntryPoints);
		// breakpoints
		List<BreakPoint> breakpoints = new ArrayList<BreakPoint>();
		addBreakpoints(breakpoints);
		input.setBreakpoints(breakpoints);
		List<BreakPoint> slicingResult = slicer.slice(input);
		printBkps(slicingResult);
	}

	private void addBreakpoints(List<BreakPoint> breakpoints) {
		switch (type) {
		case BOUNDED_STACK:
			breakpoints.add(new BreakPoint("testdata.boundedStack.BoundedStack", "push", 30));
			breakpoints.add(new BreakPoint("testdata.boundedStack.BoundedStack", "pop", 43));
			break;
		case TEST_DEVIDER:
			breakpoints.add(new BreakPoint("testdata.testDevider.TestDevider", "stop", 9));
			breakpoints.add(new BreakPoint("testdata.testDevider.TestDevider", "testLong", 23));
			breakpoints.add(new BreakPoint("testdata.testDevider.TestDevider", "testInt", 31));
			breakpoints.add(new BreakPoint("testdata.testDevider.TestDevider", "testNegInt", 39));
			breakpoints.add(new BreakPoint("testdata.testDevider.TestDevider", "test2Params", 47));
			break;
		default:
			breakpoints.add(new BreakPoint("testdata.slice.FindMax", "findMax", 11));
		}
	}

	private List<Pair<String, String>> makeEntryPoints() {
		List<Pair<String, String>> classEntryPoints = new ArrayList<Pair<String,String>>();
		switch (type) {
		case BOUNDED_STACK:
			classEntryPoints.add(Pair.make("Ltestdata/boundedStack/tzuyu/fail/BoundedStack7", "test1"));
			classEntryPoints.add(Pair.make("Ltestdata/boundedStack/tzuyu/fail/BoundedStack7", "test2"));
			classEntryPoints.add(Pair.make("Ltestdata/boundedStack/tzuyu/fail/BoundedStack7", "test3"));
			classEntryPoints.add(Pair.make("Ltestdata/boundedStack/tzuyu/fail/BoundedStack7", "test4"));
			classEntryPoints.add(Pair.make("Ltestdata/boundedStack/tzuyu/fail/BoundedStack7", "test5"));
			classEntryPoints.add(Pair.make("Ltestdata/boundedStack/tzuyu/fail/BoundedStack7", "test6"));
			classEntryPoints.add(Pair.make("Ltestdata/boundedStack/tzuyu/fail/BoundedStack7", "test7"));
			break;
		case TEST_DEVIDER:
			classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider3", "test1"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider3", "test2"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider3", "test3"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider3", "test4"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider3", "test5"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider3", "test6"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider3", "test7"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider3", "test8"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider3", "test9"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider3", "test10"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider4", "test1"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider4", "test2"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider4", "test3"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider4", "test4"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider4", "test5"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider4", "test6"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider4", "test7"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider4", "test8"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider4", "test9"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider4", "test10"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider5", "test1"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider5", "test2"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider5", "test3"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider5", "test4"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider5", "test5"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider5", "test6"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider5", "test7"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider5", "test8"));
			 classEntryPoints.add(Pair.make("Ltestdata/testDevider/tzuyu/fail/TestDevider5", "test9"));
			break;
		default:
			classEntryPoints.add(Pair.make("Ltestdata/slice/FindMaxCallerFailTest1", "test2"));
			classEntryPoints.add(Pair.make("Ltestdata/slice/FindMaxCallerPassTest1", "test1"));
		}
		return classEntryPoints;
	}
	
	private enum TestData{
		BOUNDED_STACK,
		TEST_DEVIDER
	}
}
