/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.CfgCoverageBuilder;
import cfgcoverage.jacoco.analysis.DuplicateFilterFreqProbesAnalyzer;
import cfgcoverage.jacoco.analysis.FreqProbesAnalyzer;
import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import codecoverage.jacoco.agent.AbstractExecutionReporter;
import codecoverage.jacoco.agent.ExecutionDataReporter;
import codecoverage.jacoco.agent.IExecutionReporter;
import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.utils.StopTimer;

/**
 * @author LLT
 *
 */
public class ExecutionReporter extends AbstractExecutionReporter implements IExecutionReporter {
	private Logger log = LoggerFactory.getLogger(ExecutionDataReporter.class);
	private CfgCoverageBuilder coverageBuilder;
	private boolean duplicateFilter;
	private Map<Integer, String> tcProbesUniqueHashcodes = new HashMap<Integer, String>();

	public ExecutionReporter(List<String> targetMethods, String... targetFolders) {
		super(targetFolders);
		coverageBuilder = new CfgCoverageBuilder(targetMethods);
	}
	
	public void reset(List<String> targetMethods, String... targetFolders) {
		super.reset(targetFolders);
		coverageBuilder.setTargetMethods(targetMethods);
	}
	
	public void report(String execFile, String junitResultFile, Collection<String> testingClassNames) throws SavException {
		StopTimer timer = new StopTimer("Collect coverage data");
		try {
			timer.newPoint("Read execFile");
			Map<String, List<ExecutionData>> execDataMap = read(execFile);
			timer.newPoint("Analyze data and count code coverage");
			
			ExecutionDataStore dataStore = new ExecutionDataStore();
			final FreqProbesAnalyzer analyzer = initAnalyzer(dataStore);
			coverageBuilder.testcases(testMethods);
			int testcaseIdx = 0;
			for (String session : execDataMap.keySet()) {
				dataStore.reset();
				coverageBuilder.testcase(testcaseIdx);
				for (ExecutionData data : execDataMap.get(session)) {
					dataStore.put(data);
				}
				for (String testingClassName : testingClassNames) {
					analyzer.analyzeClass(getTargetClass(testingClassName),
							testingClassName);
				}
				testcaseIdx++;
			}
			coverageBuilder.endAnalyzing();
			timer.logResults(log);
		} catch (IOException e) {
			throw new SavException(e, ModuleEnum.UNSPECIFIED, e.getMessage());
		}
	}

	private FreqProbesAnalyzer initAnalyzer(ExecutionDataStore dataStore) {
		if (duplicateFilter) {
			DuplicateFilterFreqProbesAnalyzer analyzer = new DuplicateFilterFreqProbesAnalyzer(dataStore, coverageBuilder);
			analyzer.setUniqueHashcodes(tcProbesUniqueHashcodes);
			return analyzer;
		}
		return new FreqProbesAnalyzer(dataStore, coverageBuilder);
	}
	
	public List<CfgCoverage> getCoverage() {
		return coverageBuilder.getCoverage();
	}
	
	public Map<String, CfgCoverage> getMethodCfgCoverageMap() {
		return coverageBuilder.getMethodCfgCoverageMap();
	}

	/**
	 * @param cfgCoverageMap  the map between methodIds (className.methodName) and theirs existing cfgcoverage
	 */
	public void setCfgCoverageMap(Map<String, CfgCoverage> cfgCoverageMap) {
		this.coverageBuilder.setMethodCfgCoverageMap(cfgCoverageMap);
	}

	public void setConfig(CfgJaCoCoConfigs config) {
		duplicateFilter = config.needToFilterDuplicate();
	}
}
