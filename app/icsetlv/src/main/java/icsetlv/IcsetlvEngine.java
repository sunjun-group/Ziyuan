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
import icsetlv.iface.ISlicer;
import icsetlv.slicer.SlicerInput;
import icsetlv.slicer.WalaSlicer;
import icsetlv.variable.AssertionDetector;
import icsetlv.variable.VariableNameCollector;
import icsetlv.variable.VariablesExtractor;
import icsetlv.vm.VMConfiguration;

import java.util.List;

/**
 * @author LLT
 *
 */
public class IcsetlvEngine {
	
	public List<BreakPoint> run(IcsetlvInput input) throws IcsetlvException {
		// scan all assertion statements and create breakpoints 
		List<BreakPoint> breakpoints = AssertionDetector.scan(input
				.getAssertionSourcePaths());
		
		VariablesExtractor extractor = new VariablesExtractor(
				input.getConfig(), input.getPassTestcases(),
				input.getFailTestcases(), breakpoints);
		VariablesExtractorResult result = extractor.execute();
		System.out.println(result);
		
		// do slicing
		SlicerInput sliceInput = new SlicerInput();
		VMConfiguration config = input.getConfig();
		sliceInput.setAppBinFolder(input.getAppOutput());
		sliceInput.setBreakpoints(breakpoints);
		sliceInput.setJre(config.getJavaHome());
		sliceInput.setClassEntryPoints(input.getTestMethods());
		sliceInput.setAppSrcFolder(input.getSrcFolders());
		ISlicer slicer = new WalaSlicer();
		List<BreakPoint> slicingResult = slicer.slice(sliceInput);
		new VariableNameCollector(input.getSrcFolders()).updateVariables(slicingResult);
		
		return slicingResult;
	}

}
