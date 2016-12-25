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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import sav.commons.AbstractTest;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.dto.BreakPoint.Variable.VarScope;
import sav.strategies.dto.DebugLine;

/**
 * @author LLT
 *
 */
public class DebugLinePreProcessorTest extends AbstractTest {
	private DebugLinePreProcessor processor;
	
	@Before
	public void setup() {
		processor = new DebugLinePreProcessor();
	}
	
	@Test
	public void Duplicate2Line() {
		TestData data = new TestData();
		Variable var1 = new Variable("var1", "var1.field1", VarScope.THIS);
		Variable var2 = new Variable("var2", "var2", VarScope.UNDEFINED);
		data.addDebugLine("class1", 8, 10, var1, var2);
		Variable var21 = new Variable("var2", "var2", VarScope.UNDEFINED);
		Variable var22 = new Variable("var1", "var1.field1", VarScope.THIS);
		data.addDebugLine("class1", 5, 10, var21, var22);
		List<DebugLine> debugLines = processor.preProcess(data.debugLines);
		printDebugLine(debugLines);
		Assert.assertEquals(1, debugLines.size());
		List<Integer> orgLines = debugLines.get(0).getOrgLineNos();
		Assert.assertTrue(orgLines.contains(5));
		Assert.assertTrue(orgLines.contains(8));
	}
	
	@Test
	public void Duplicate4LineInTheMiddle() {
		TestData data = new TestData();
		/* line 8 (11) */
		Variable var1 = new Variable("var3", "var3.field1", VarScope.THIS);
		Variable var2 = new Variable("var4", "var4.field2", VarScope.UNDEFINED);
		data.addDebugLine("class1", 8, 12, var1, var2);
		/* line 11 (12) */
		data.addDebugLine("class1", 11, 12, new Variable("var5"));
		/* line 12 (13) */
		data.addDebugLine("class1", 12, 15, new Variable("var7"));
		/* line 5 (10) */
		Variable var21 = new Variable("var2", "var2", VarScope.UNDEFINED);
		Variable var22 = new Variable("var1", "var1.field1", VarScope.THIS);
		data.addDebugLine("class1", 5, 12, var21, var22);
		/* line 3 (9) */
		Variable var31 = new Variable("var2", "var2", VarScope.UNDEFINED);
		Variable var32 = new Variable("var1", "var1.field1", VarScope.THIS);
		data.addDebugLine("class1", 3, 12, var31, var32);
		List<DebugLine> debugLines = processor.preProcess(data.debugLines);
		printDebugLine(debugLines);
		Assert.assertEquals(2, debugLines.size());
		List<Integer> orgLines = debugLines.get(0).getOrgLineNos();
		Assert.assertTrue(orgLines.contains(5));
		Assert.assertTrue(orgLines.contains(8));
		Assert.assertTrue(orgLines.contains(3));
		Assert.assertEquals(5, debugLines.get(0).getVars().size());
	}
	
	@Test
	public void noDuplicateLines() {
		TestData data = new TestData();
		Variable var1 = new Variable("var1", "var1.field2", VarScope.THIS);
		Variable var2 = new Variable("var2", "var2", VarScope.UNDEFINED);
		data.addDebugLine("class1", 8, 12, var1, var2);
		Variable var21 = new Variable("var2", "var2", VarScope.UNDEFINED);
		Variable var22 = new Variable("var1", "var1.field1", VarScope.THIS);
		data.addDebugLine("class1", 5, 10, var21, var22);
		List<DebugLine> debugLines = processor.preProcess(data.debugLines);
		printDebugLine(debugLines);
		Assert.assertEquals(2, debugLines.size());
	}
	
	public void printDebugLine(List<DebugLine> list) {
		for (DebugLine ele : list) {
			System.out.println(ele);
		}
	}
	
	private static class TestData {
		List<DebugLine> debugLines = new ArrayList<DebugLine>();
		
		void addDebugLine(String className, int orgLine, int debugLine, Variable... vars) {
			BreakPoint orgBkp = new BreakPoint(className, orgLine, vars);
			DebugLine line = new DebugLine(orgBkp, debugLine);
			debugLines.add(line);
		}
	}
}
