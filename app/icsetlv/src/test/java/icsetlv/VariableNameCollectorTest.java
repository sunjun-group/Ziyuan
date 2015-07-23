/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import icsetlv.common.exception.IcsetlvException;
import icsetlv.variable.VariableNameCollector;
import icsetlv.variable.VariableNameCollector.VarNameCollectionMode;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import sav.common.core.utils.CollectionUtils;
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
	
	private BreakPoint runSum(int lineNo) throws IcsetlvException {
		String className = VarNameCollectorTestData.class.getName();
		BreakPoint bkp = new BreakPoint(className, lineNo);
		List<BreakPoint> brkps = CollectionUtils.listOf(bkp);
		collector.updateVariables(brkps);
		printList(brkps);
		return bkp;
	}
	
	@Test
	public void testLine20() throws IcsetlvException {
		BreakPoint bkp = runSum(20);
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
		BreakPoint bkp = runSum(23);
		Assert.assertEquals(1, bkp.getVars().size());
		Variable var = bkp.getVars().get(0);
		Assert.assertEquals("innerClass", var.getParentName());
		Assert.assertEquals("innerClass.inner", var.getFullName());
		Assert.assertEquals(VarScope.UNDEFINED, var.getScope());
	}
	
	@Test
	public void testLine24() throws IcsetlvException {
		BreakPoint bkp = runSum(24);
		Assert.assertEquals(1, bkp.getVars().size());
		Variable var = bkp.getVars().get(0);
		Assert.assertEquals("innerClass", var.getParentName());
		Assert.assertEquals("innerClass.inner.b", var.getFullName());
	}
	
	@Test
	public void testLine28() throws IcsetlvException {
		BreakPoint bkp = runSum(28);
		Assert.assertEquals(2, bkp.getVars().size());
		Variable var = bkp.getVars().get(0);
		Assert.assertEquals("a", var.getFullName());
		var = bkp.getVars().get(1);
		Assert.assertEquals("innerClass", var.getParentName());
		Assert.assertEquals("innerClass.b", var.getFullName());
	}
	
}
