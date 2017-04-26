/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.CfgCoverageBuilder;
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

	public ExecutionReporter(String... targetFolders) {
		super(targetFolders);
	}
	
	public void report(String execFile, String junitResultFile, List<String> testingClassNames) throws SavException {
		StopTimer timer = new StopTimer("Collect coverage data");
		try {
			coverageBuilder = new CfgCoverageBuilder();
			timer.newPoint("Read execFile");
			Map<String, List<ExecutionData>> execDataMap = read(execFile);
			timer.newPoint("Analyze data and count code coverage");
			
			
			ExecutionDataStore dataStore = new ExecutionDataStore();
			final FreqProbesAnalyzer analyzer = new FreqProbesAnalyzer(dataStore, coverageBuilder);
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
			timer.logResults(log);
		} catch (IOException e) {
			throw new SavException(ModuleEnum.UNDEFINED, e);
		}
		List<CfgCoverage> coverage = coverageBuilder.getCoverage();
		System.out.println(coverage);
	}
	
	public List<CfgCoverage> getCoverage() {
		return coverageBuilder.getCoverage();
	}
}
