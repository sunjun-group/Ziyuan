/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author khanh
 *
 */
public class TzuyuCoreDemoTest extends TzuyuCoreTest{

	@Test
	public void testNoLoop() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.calculator.CalculatorTest");
		app.faultLocate("sav.commons.testdata.calculator.Calculator", "getSum", "validateGetSum",
				null, junitClassNames, false);
	}

	@Test
	public void testNoLoop1() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.calculator.CalculatorTest1");
		app.faultLocate("sav.commons.testdata.calculator.Calculator", "getSum1", "validateGetSum",
				null, junitClassNames, false);
	}
	
	@Test
	public void testArray() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.calculator.GetSumArrayTest");
		app.faultLocate("sav.commons.testdata.calculator.Calculator", "getSumArray", "validateGetSumArray",
				null, junitClassNames, false);
	}
	
	@Test
	public void testSimpleForLoop() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.calculator.LoopInvariantTest");
		app.faultLocate("sav.commons.testdata.calculator.Calculator", "loopInvariant", "validateLoopInvariant",
				null, junitClassNames, false);
	}
	
	@Test
	public void testClass() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.calculator.SumTest");
		app.faultLocate("sav.commons.testdata.calculator.Sum", "getSum", "validateGetSum",
				null, junitClassNames, false);
	}
	
	@Test
	public void testExtractVariableNameIssue() throws Exception {
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.calculator.ExtractVariableNameIssueJunit");
		app.faultLocate(
				"sav.commons.testdata.calculator.ExtractVariableNameIssue",
				"getSum", "validateGetSum", null, junitClassNames, false);
	}
	
	@Test
	public void testPropertyAsObject() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.calculator.ClassATest");
		app.faultLocate("sav.commons.testdata.calculator.ClassA", "getSum", "validateGetSum",
				null, junitClassNames, false);
	}
	
	@Test
	public void testLoopInvariant() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.loopinvariant.LoopTest");
		app.faultLocate("sav.commons.testdata.loopinvariant.Loop", "testLoop", "validateTestLoop",
				null, junitClassNames, false);
	}
	
	@Test
	public void testWhileLoopWith2Bugs() throws Exception{
		List<String> junitClassNames = new ArrayList<String>();
		junitClassNames.add("sav.commons.testdata.search1.SearchIndexEqualValueTest");
		app.faultLocate("sav.commons.testdata.search1.SearchIndexEqualValue", "search", "validate",
				null, junitClassNames, false);
	}
}
