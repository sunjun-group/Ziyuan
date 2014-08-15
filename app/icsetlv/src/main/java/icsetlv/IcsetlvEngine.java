/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.iface.IBugExpert;
import icsetlv.iface.IManager;
import icsetlv.iface.ISlicer;
import icsetlv.iface.IVariableExtractor;
import icsetlv.slicer.SlicerInput;
import icsetlv.slicer.WalaSlicer;
import icsetlv.variable.VariablesExtractor;
import icsetlv.vm.VMConfiguration;

import java.util.List;

/**
 * @author LLT
 *
 */
public class IcsetlvEngine implements IManager {
	private VMConfiguration config;
	private SlicerInput sliceInput;
	private BugAnalyzer bugAnalyzer;
	
	public List<BreakPoint> run(IcsetlvInput input) throws IcsetlvException {
		config = input.getConfig();
		// do slicing
		sliceInput = new SlicerInput();
		sliceInput.setAppBinFolder(input.getAppOutput());
		sliceInput.setJre(config.getJavaHome());
		sliceInput.setAppSrcFolder(input.getSrcFolders());
		sliceInput.setClassEntryPoints(input.getTestMethods());
		return run(input.getPassTestcases(), input.getFailTestcases(),
				input.getBreakpoints());
	}

	private List<BreakPoint> run(List<String> passTestcases,
			List<String> failTestcases, List<BreakPoint> initBkps)
			throws IcsetlvException {
		bugAnalyzer = new BugAnalyzer(this, passTestcases, failTestcases);
		return bugAnalyzer.analyze(initBkps);
	}

	@Override
	public IVariableExtractor getVariableExtractor() {
		return new VariablesExtractor(config);
	}

	@Override
	public ISlicer getSlicer() throws IcsetlvException {
		return new WalaSlicer(sliceInput);
	}

	@Override
	public IBugExpert getBugExpert() {
		return new BugExpert();
	}
}
