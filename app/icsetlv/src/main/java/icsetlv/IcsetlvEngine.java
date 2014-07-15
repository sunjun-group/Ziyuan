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
import icsetlv.common.utils.ExecutionResultFileUtils;
import icsetlv.slicer.ISlicer;
import icsetlv.slicer.SlicerInput;
import icsetlv.slicer.WalaSlicer;
import icsetlv.variable.AssertionDetector;
import icsetlv.variable.VariableNameCollector;
import icsetlv.variable.VariablesExtractor;
import icsetlv.vm.VMConfiguration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sav.common.core.utils.CollectionUtils;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;

/**
 * @author LLT
 *
 */
public class IcsetlvEngine {
	
	public void analyze(IcsetlvInput input) throws IcsetlvException {
		// scan all assertion statements and create breakpoints 
		List<BreakPoint> breakpoints = AssertionDetector.scan(input
				.getTestcasesSourcePaths());
	}
	
	public void run(IcsetlvInput input) throws IcsetlvException {
		List<BreakPoint> breakpoints = AssertionDetector.scan(input
				.getTestcasesSourcePaths());
		if (CollectionUtils.isEmpty(breakpoints)) {
			return;
		}
		
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
		
		for (BreakPoint bkp : slicingResult) {
			System.out.println(String.format("%s.%s() line: %s vars: %s", 
					bkp.getClassCanonicalName(), bkp.getMethodName(), bkp.getLineNo(), bkp.getVars()));
		}
	
		List<String> allTests = new ArrayList<String>();
		allTests.add("slice.FindMaxCallerTest");
		VariablesExtractor varExtr = new VariablesExtractor(config, allTests,
				allTests, breakpoints);
//		config.setArgs(allTests.toArray(new String[]{}));
		try {
			VariablesExtractorResult extractedResult = varExtr.execute();

			String filePath = "brpResult.txt";
			ExecutionResultFileUtils.writeToFile(extractedResult, filePath);
		} catch (FileNotFoundException e) {
			IcsetlvException.rethrow(e);
		} catch (IOException e) {
			IcsetlvException.rethrow(e);
		} catch (InterruptedException e) {
			IcsetlvException.rethrow(e);
		} catch (IncompatibleThreadStateException e) {
			IcsetlvException.rethrow(e);
		} catch (AbsentInformationException e) {
			IcsetlvException.rethrow(e);
		}
	}

}
