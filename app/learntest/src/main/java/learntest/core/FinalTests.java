/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import gentest.core.data.Sequence;
import gentest.junit.FileCompilationUnitPrinter;
import gentest.junit.PrinterParams;
import gentest.junit.TestsPrinter;
import learntest.core.gentest.GentestResult;
import sav.common.core.Pair;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.FileUtils;

/**
 * @author LLT
 *
 */
public class FinalTests {
	private Map<String, Sequence> sequences;
	private List<String> files;
	
	public FinalTests() {
		files = new ArrayList<String>();
		sequences = new HashMap<String, Sequence>();
	}

	public void log(GentestResult result) {
		files.addAll(FileUtils.getFilePaths(result.getAllFiles()));
		CollectionUtils.addAllIfNotNull(sequences, result.getTestcaseSequenceMap());
	}

	public void filterByCoverageResult(Map<String, CfgCoverage> coverageMap) {
		for (CfgCoverage cfgCoverage : coverageMap.values()) {
			Map<Integer, List<Integer>> dupTcs = cfgCoverage.getDupTcs();
			if (CollectionUtils.isEmpty(dupTcs)) {
				continue;
			}
			for (List<Integer> toRemoveTcs : dupTcs.values()) {
				for (Integer idx : toRemoveTcs) {
					sequences.remove(cfgCoverage.getTestcases().get(idx));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<File> commit(PrinterParams printerParams) {
		/* print selected tests */
		TestsPrinter printer = new TestsPrinter(printerParams);
		List<Sequence> allSequences = new ArrayList<Sequence>(sequences.values());
		printer.printTests(Pair.of(allSequences, Collections.EMPTY_LIST));
		return ((FileCompilationUnitPrinter) printer.getCuPrinter()).getGeneratedFiles();
	}

	public Map<String, Sequence> getSequences() {
		return sequences;
	}

	public List<String> getFiles() {
		return files;
	}
	
}
