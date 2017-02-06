/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import icsetlv.common.exception.IcsetlvException;
import icsetlv.variable.VarNameVisitor.VarNameCollectionMode;
import icsetlv.variable.VariableNameCollector;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.dto.BreakPoint.Variable.VarScope;
import testdata.VarNameCollectorTestData;

/**
 * @author LLT
 *
 */
public class VariableNameCollectorTest extends AbstractTest {
	private VariableNameCollector collector;
	
	@Before
	public void setup() {
		collector = new VariableNameCollector(
				VarNameCollectionMode.FULL_NAME,
				TestConfiguration.getTestScrPath("icsetlv"));
	}
	
	private List<BreakPoint> runUpdateVars(int... lineNos) throws IcsetlvException {
		String className = VarNameCollectorTestData.class.getName();
		List<BreakPoint> brkps = new ArrayList<BreakPoint>();
		for (int lineNo : lineNos) {
			BreakPoint bkp = new BreakPoint(className, lineNo);
			brkps.add(bkp);
		}
		collector.updateVariables(brkps);
		printList(brkps);
		return brkps;
	}
	
	@Test
	public void testLine20() throws IcsetlvException {
		BreakPoint bkp = runUpdateVars(20).get(0);
		Assert.assertEquals(2, bkp.getVars().size());
		Variable var = bkp.getVars().get(0);
		Assert.assertEquals("a", var.getParentName());
		Assert.assertEquals(VarScope.THIS, var.getScope());
		var = bkp.getVars().get(1);
		Assert.assertEquals("a", var.getParentName());
		Assert.assertEquals(VarScope.UNDEFINED, var.getScope());
	}
	
	@Test
	public void testLine23() throws IcsetlvException {
		BreakPoint bkp = runUpdateVars(23).get(0);
		Assert.assertEquals(1, bkp.getVars().size());
		Variable var = bkp.getVars().get(0);
		Assert.assertEquals("innerClass", var.getParentName());
		Assert.assertEquals("innerClass.inner", var.getFullName());
		Assert.assertEquals(VarScope.UNDEFINED, var.getScope());
	}
	
	@Test
	public void testLine24() throws IcsetlvException {
		BreakPoint bkp = runUpdateVars(24).get(0);
		Assert.assertEquals(1, bkp.getVars().size());
		Variable var = bkp.getVars().get(0);
		Assert.assertEquals("innerClass", var.getParentName());
		Assert.assertEquals("innerClass.inner.b", var.getFullName());
	}
	
	@Test
	public void testLine28() throws IcsetlvException {
		BreakPoint bkp = runUpdateVars(28).get(0);
		Assert.assertEquals(2, bkp.getVars().size());
		Variable var = bkp.getVars().get(0);
		Assert.assertEquals("a", var.getFullName());
		var = bkp.getVars().get(1);
		Assert.assertEquals("innerClass", var.getParentName());
		Assert.assertEquals("innerClass.b", var.getFullName());
	}
	
	@Test
	public void testLine46() throws IcsetlvException {
		BreakPoint bkp = runUpdateVars(45).get(0);
		Assert.assertEquals(3, bkp.getVars().size());
	}
	
	@Test
	public void collectAppendingMode() throws Exception {
		collector.setAppendPrevLineVars(true);
		runUpdateVars(21, 22, 23, 25);
	}
}
