/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import icsetlv.common.dto.BreakpointData;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.variable.DebugValueInstExtractor;
import icsetlv.variable.TestcasesExecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import sav.common.core.Constants;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.JunitUtils;
import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.dto.BreakPoint.Variable.VarScope;
import sav.strategies.vm.VMConfiguration;
import testdata.testcasesexecutor.test1.TcExSum;
import testdata.testcasesexecutor.test1.TcExSumTest;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;

/**
 * @author LLT
 *
 */
public class InstrTestcasesExecutorTest extends AbstractTest {
	private VMConfiguration vmConfig;
	private TestcasesExecutor varExtr;
	
	@Before
	public void setup() {
		vmConfig = initVmConfig();
		vmConfig.addClasspath(TestConfiguration.getTestTarget(ICSETLV));
		vmConfig.addClasspath(TestConfiguration.getTzAssembly(Constants.SAV_COMMONS_ASSEMBLY));
		varExtr = new TestcasesExecutor(3);
	}

	@Test
	public void testExecute() throws IOException, InterruptedException,
			IncompatibleThreadStateException, AbsentInformationException,
			IcsetlvException, SavException, ClassNotFoundException {
		// breakpoints
		List<BreakPoint> breakpoints = new ArrayList<BreakPoint>();
		String clazz = TcExSum.class.getName();
		BreakPoint bkp1 = new BreakPoint(clazz, null, 32);
		bkp1.addVars(new Variable("a"));
		bkp1.addVars(new Variable("a", "a", VarScope.THIS));
		bkp1.addVars(new Variable("innerClass", "innerClass.b"));
		bkp1.addVars(new Variable("innerClass", "innerClass.a"));
		breakpoints.add(bkp1);
		List<String> tests = JunitUtils.extractTestMethods(CollectionUtils
				.listOf(TcExSumTest.class.getName()));
		Map<String, Object> instVals = new HashMap<String, Object>();
		instVals.put("a", 1000);
		varExtr.setup(vmConfig, tests);
		varExtr.setValueExtractor(new DebugValueInstExtractor(instVals), true);
		varExtr.run(breakpoints);
		List<BreakpointData> result = varExtr.getResult();
		System.out.println(result);
	}
}
