/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.dto.VariablesExtractorResult;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.common.utils.CollectionBuilder;
import icsetlv.variable.AssertionDetector;
import icsetlv.variable.VariablesExtractor;
import icsetlv.vm.VMConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

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
			AbsentInformationException {
		IcsetlvInput input = initInput();
		List<BreakPoint> bkps = AssertionDetector.scan(input.getTestcasesSourcePaths());
		printBkps(bkps);
		VariablesExtractor extractor = new VariablesExtractor(
				input.getConfig(), input.getPassTestcases(),
				input.getFailTestcases(), bkps);
		VariablesExtractorResult result = extractor.execute();
		print(result);
	}

	private IcsetlvInput initInput() {
		IcsetlvInput input = new IcsetlvInput();
		VMConfiguration vmConfig = initVmConfig();
		input.setConfig(vmConfig);
		input.setTestcasesSourcePaths(getTestcasesSourcePaths());
		input.setPassTestcases(Arrays.asList(getPassTestcases()));
		input.setFailTestcases(Arrays.asList(getFailTestcases()));
		return input;
	}
	
	private String[] getPassTestcases() {
		return new String[] {
				"testdata.boundedStack.tzuyu.pass.BoundedStack0",
				"testdata.boundedStack.tzuyu.pass.BoundedStack1",
				"testdata.boundedStack.tzuyu.pass.BoundedStack2",
				"testdata.boundedStack.tzuyu.pass.BoundedStack3",
				"testdata.boundedStack.tzuyu.pass.BoundedStack4",
				"testdata.boundedStack.tzuyu.pass.BoundedStack5",
				"testdata.boundedStack.tzuyu.pass.BoundedStack6"
			};
	}
	
	private String[] getFailTestcases() {
		return new String[] {
				"testdata.boundedStack.tzuyu.fail.BoundedStack7",
			};
	}

	private List<String> getTestcasesSourcePaths() {
		return CollectionBuilder.init(new ArrayList<String>())
				.add(config.getSourcepath() + "/testdata/boundedStack/BoundedStack.java")
				.getResult();
	}
}
