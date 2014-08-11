/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.dto.BreakPoint.Variable;
import icsetlv.common.dto.VariablesExtractorResult;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.variable.VariablesExtractor;
import icsetlv.vm.VMConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import sav.common.core.utils.CollectionUtils;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;

/**
 * @author LLT
 *
 */
public class VariablesExtractorTest extends AbstractTest {
	
	@Test
	public void testExecute() throws IOException, InterruptedException, IncompatibleThreadStateException, AbsentInformationException, IcsetlvException {
		VMConfiguration vmConfig = initVmConfig();
	
		// breakpoints
		List<BreakPoint> breakpoints = new ArrayList<BreakPoint>();
		BreakPoint bkp1 = new BreakPoint("testdata.slice.FindMax", "findMax");
		bkp1.addVars(new Variable("max"));
		bkp1.addVars(new Variable("arr"));
		bkp1.setLineNo(11);
		breakpoints.add(bkp1);
		BreakPoint bkp2 = new BreakPoint("testdata.slice.FindMax", "findMax");
		bkp2.addVars(new Variable("max"));
		bkp2.addVars(new Variable("arr"));
		bkp2.setLineNo(14);
		breakpoints.add(bkp2);
		
		VariablesExtractor varExtr = new VariablesExtractor(vmConfig);
		VariablesExtractorResult extractedResult = varExtr.execute(CollectionUtils.listOf("testdata.slice.FindMaxCallerPassTest1"),
				CollectionUtils.listOf("testdata.slice.FindMaxCallerFailTest1"),
				breakpoints);
		print(extractedResult);
	}
}
