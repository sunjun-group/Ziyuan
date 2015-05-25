/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import icsetlv.common.dto.TcExecResult;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.variable.AssertionDetector;
import icsetlv.variable.TestcasesExecutor;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import libsvm.core.KernelType;
import libsvm.core.Machine;
import libsvm.core.MachineType;
import libsvm.core.Parameter;

import org.junit.Before;
import org.junit.Test;

import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.commons.AbstractTest;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.BreakPoint.Variable;
import sav.strategies.vm.VMConfiguration;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;

/**
 * @author LLT
 *
 */
public class IcsetlvEngineTest extends AbstractTest {
	private IcsetlvEngine engine;
	
	@Before
	public void beforeTest() {
		engine = new IcsetlvEngine();
	}

	@Test
	public void testAnalyze() throws IcsetlvException, IOException,
			InterruptedException, IncompatibleThreadStateException,
			AbsentInformationException, SavException {
		IcsetlvInput input = initInput();
		List<BreakPoint> bkps = AssertionDetector.scan(input.getAssertionSourcePaths());
		
		BreakPoint bkp4 = new BreakPoint("sav.commons.testdata.findMax.FindMax", "findMax", 10);
		bkp4.addVars(new Variable("max"));
		bkp4.addVars(new Variable("i"));
		bkps.add(bkp4);	
		
		BreakPoint bkp3 = new BreakPoint("sav.commons.testdata.findMax.FindMax", "findMax", 14); 
		bkp3.addVars(new Variable("max"));
		bkps.add(bkp3);
		
		printBkps(bkps);
		TestcasesExecutor extractor = new TestcasesExecutor(input.getConfig(), 4);
		TcExecResult result = extractor.execute(CollectionUtils.join(input.getPassTestcases(), input.getFailTestcases()),
				bkps);
		print(result);
		Machine machine = setupMachine(new Machine(), 2);
		for (BreakPoint bkp : bkps) {
			BugExpert.addDataPoints(machine, result.getPassValues(bkp), result.getFailValues(bkp));
			machine.train();
			System.out.println(machine.getLearnedLogic());
			System.out.println(machine.getModelAccuracy());
		}
	}

	protected Machine setupMachine(Machine defaultMachine, int numberOfFeatures) {
		return defaultMachine.setNumberOfFeatures(numberOfFeatures).setParameter(
				new Parameter().setMachineType(MachineType.C_SVC).setKernelType(KernelType.LINEAR)
						.setEps(1.0).setUseShrinking(false).setPredictProbability(false)
						.setC(Double.MAX_VALUE));
	}

	private IcsetlvInput initInput() {
		IcsetlvInput input = new IcsetlvInput();
		VMConfiguration vmConfig = initVmConfig();
		input.setConfig(vmConfig);
		input.setAssertionSourcePaths(getTestcasesSourcePaths());
		input.setPassTestcases(Arrays.asList(getPassTestcases()));
		input.setFailTestcases(Arrays.asList(getFailTestcases()));
		return input;
	}
	
	private String[] getPassTestcases() {
		return new String[] {
				"testdata.slice.FindMaxCallerPassTest1"
			};
	}
	
	private String[] getFailTestcases() {
		return new String[] {
				"testdata.slice.FindMaxCallerFailTest1"
			};
	}
	
	private Map<String, List<String>> getTestcasesSourcePaths() {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		result.put(config.SAV_COMMONS_TEST_TARGET + "/testdata/slice/FindMax.java",
//		result.put(TestConfiguration.getTestScrPath("sav.commons") + "/sav/commons/testdata/findMax/FindMax.java",
				//"/testdata/boundedStack/BoundedStack.java", 
				null);
		return result;
	}
}
