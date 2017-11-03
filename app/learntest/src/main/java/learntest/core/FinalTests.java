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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import gentest.core.data.Sequence;
import gentest.junit.FileCompilationUnitPrinter;
import gentest.junit.PrinterParams;
import gentest.junit.TestsPrinter;
import learntest.core.commons.data.LineCoverageResult;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.commons.utils.CoverageUtils;
import learntest.core.gentest.GentestResult;
import learntest.core.gentest.LearntestJWriter;
import sav.common.core.Pair;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.FileUtils;

/**
 * @author LLT
 *
 */
public class FinalTests {
	private Logger log = LoggerFactory.getLogger(FinalTests.class);
	private Map<String, Sequence> sequences;
	private List<String> files;
	private LineCoverageResult lineCoverageResult;
	
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
			Map<Integer, Set<Integer>> dupTcs = cfgCoverage.getDupTcs();
			if (CollectionUtils.isEmpty(dupTcs)) {
				continue;
			}
			for (Set<Integer> toRemoveTcs : dupTcs.values()) {
				for (Integer idx : toRemoveTcs) {
					sequences.remove(cfgCoverage.getTestcases().get(idx));
				}
			}

			List<String> lines = CoverageUtils.getBranchCoverageDisplayTexts(cfgCoverage, -1);
			for (String line : lines) {
				log.debug(line);
			}
		}
	}

	public List<File> commit(PrinterParams printerParams, CfgCoverage cfgCoverage, TargetMethod targetMethod) {
		/* build linecoverage and do filter the sequences again by covered line numbers */
		buildLineCoverageAndFilterSequences(cfgCoverage, targetMethod);
		/* print selected tests */
		TestsPrinter printer = new TestsPrinter(printerParams);
		LearntestJWriter jWriter = new LearntestJWriter(true);
		printer.setCuWriter(jWriter);
		List<Sequence> allSequences = new ArrayList<Sequence>(sequences.values());
		printer.printTests(Pair.of(allSequences, new ArrayList<Sequence>(0)));
		updateNewTestcaseNameInLineCoverageResult(jWriter.getTestcaseSequenceMap());
		return ((FileCompilationUnitPrinter) printer.getCuPrinter()).getGeneratedFiles();
	}

	private void buildLineCoverageAndFilterSequences(CfgCoverage cfgCoverage, TargetMethod targetMethod) {
		lineCoverageResult = LineCoverageResult.build(sequences.keySet(), cfgCoverage, targetMethod, true);
		Collection<String> filteredTestcases = lineCoverageResult.getCoveredTestcases();
		Iterator<Entry<String, Sequence>> it = sequences.entrySet().iterator();
		for (; it.hasNext(); ) {
			Entry<String, Sequence> entry = it.next();
			if (!filteredTestcases.contains(entry.getKey())) {
				it.remove();
			}
		}
	}

	private void updateNewTestcaseNameInLineCoverageResult(Map<String, Sequence> newTestcaseSequenceMap) {
		Map<Sequence, String> sequenceOldTcMap = CollectionUtils.revertMap(sequences);
		Map<String, String> oldToNewTestcaseMap;
		if (newTestcaseSequenceMap == null) {
			oldToNewTestcaseMap = new HashMap<String, String>(0);
			return;
		}
		oldToNewTestcaseMap = new HashMap<String, String>(newTestcaseSequenceMap.size());
		for (Entry<String, Sequence> entry : newTestcaseSequenceMap.entrySet()) {
			String newTc = entry.getKey();
			String oldTc = sequenceOldTcMap.get(entry.getValue());
			if (newTc == null || oldTc == null) {
				log.debug("one of testcase is missing in the map: newTc = {}, oldTc = {}", newTc, oldTc);
			}
			oldToNewTestcaseMap.put(oldTc, newTc);
		}
		lineCoverageResult.updateTestcase(oldToNewTestcaseMap);
	}

	public Map<String, Sequence> getSequences() {
		return sequences;
	}

	public List<String> getFiles() {
		return files;
	}

	public LineCoverageResult getLineCoverageResult() {
		if (lineCoverageResult == null) {
			log.warn("finalTest has not been committed yet!");
		}
		return lineCoverageResult;
	}
}
