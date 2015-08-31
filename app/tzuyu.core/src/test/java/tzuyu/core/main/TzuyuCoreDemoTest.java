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

import org.junit.Test;

import sav.commons.testdata.calculator.Calculator;
import sav.commons.testdata.calculator.CalculatorTest;
import sav.commons.testdata.calculator.Sum;
import sav.commons.testdata.calculator.SumTest;
import sav.commons.testdata.paper.TestClass;
import sav.commons.testdata.paper.Tests;
import sav.commons.testdata.paper.selectivesampling.StudentEvaluate;
import sav.commons.testdata.paper.selectivesampling.StudentEvaluateTest;
import sav.commons.testdata.paper.selectivesampling.StudentEvaluateTest2;
import sav.commons.testdata.timsort.TimSortTest;

/**
 * @author khanh
 *
 */
public class TzuyuCoreDemoTest extends TzuyuCoreTest{
	
	@Test
	public void testPaperExample() throws Exception {
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add(Tests.class.getName());
		FaultLocateParams params = initFaultLocateParams(TestClass.class.getName(), "calculate", null,
				null, junitClassNames, false);
		params.setRunMutation(false);
		app.faultLocate(params);
	}
	
	@Test
	public void testStudentEvaluate2() throws Exception {
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add(StudentEvaluateTest2.class.getName());
		FaultLocateParams params = initFaultLocateParams(StudentEvaluate.class.getName(), "lalala", null,
				null, junitClassNames, false);
		params.setRunMutation(false);
		params.setGenTest(false);
		params.setValueRetrieveLevel(4);
		app.faultLocate(params);
	}
	
	@Test
	public void testStudentEvaluate() throws Exception {
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add(StudentEvaluateTest.class.getName());
		FaultLocateParams params = initFaultLocateParams(StudentEvaluate.class.getName(), "evaluate", null,
				null, junitClassNames, false);
		params.setRunMutation(false);
		params.setGenTest(true);
		params.setValueRetrieveLevel(4);
		app.faultLocate(params);
	}
	
	@Test
	public void testTimSort() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add(TimSortTest.class.getName());
		FaultLocateParams params = initFaultLocateParams(sav.commons.testdata.timsort.TimSort.class.getName(), "mergeCollapse", null,
				null, junitClassNames, false);
		params.setRunMutation(false);
		params.setGenTest(false);
		params.setValueRetrieveLevel(4);
		app.faultLocate(params);
	}
	
	@Test
	public void testNoLoop() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add(CalculatorTest.class.getName());
		app.faultLocate(initFaultLocateParams(Calculator.class.getName(), "getSum", "validateGetSum",
				Arrays.asList("sav.commons.testdata.calculator"), junitClassNames, true));
	}

	@Test
	public void testNoLoop1() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.calculator.CalculatorTest1");
		app.faultLocate(initFaultLocateParams("sav.commons.testdata.calculator.Calculator", "getSum1", "validateGetSum",
				null, junitClassNames, false));
	}
	
	@Test
	public void testArray() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.calculator.GetSumArrayTest");
		app.faultLocate(initFaultLocateParams("sav.commons.testdata.calculator.Calculator", "getSumArray", "validateGetSumArray",
				null, junitClassNames, false));
	}
	
	@Test
	public void testSimpleForLoop() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.calculator.LoopInvariantTest");
		app.faultLocate(initFaultLocateParams("sav.commons.testdata.calculator.Calculator", "loopInvariant", "validateLoopInvariant",
				null, junitClassNames, false));
	}
	
	@Test
	public void testClass() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add(SumTest.class.getName());
		FaultLocateParams params = initFaultLocateParams(Sum.class.getName(), "getSum", "validateGetSum",
				null, junitClassNames, false);
		app.faultLocate(params);
	}
	
	@Test
	public void testExtractVariableNameIssue() throws Exception {
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.calculator.ExtractVariableNameIssueJunit");
		app.faultLocate(initFaultLocateParams(
				"sav.commons.testdata.calculator.ExtractVariableNameIssue",
				"getSum", "validateGetSum", null, junitClassNames, false));
	}
	
	@Test
	public void testPropertyAsObject() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.calculator.ClassATest");
		app.faultLocate(initFaultLocateParams("sav.commons.testdata.calculator.ClassA", "getSum", "validateGetSum",
				null, junitClassNames, false));
	}
	
	@Test
	public void testLoopInvariant() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.loopinvariant.LoopTest");
		app.faultLocate(initFaultLocateParams("sav.commons.testdata.loopinvariant.Loop", "testLoop", "validateTestLoop",
				null, junitClassNames, false));
	}
	
	@Test
	public void testWhileLoopWith2Bugs() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.search1.SearchIndexEqualValueTest");
		app.faultLocate(initFaultLocateParams("sav.commons.testdata.search1.SearchIndexEqualValue", "search", "validate",
				null, junitClassNames, false));
	}
}
