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

import org.jacoco.core.analysis.ICoverageVisitor;
import org.jacoco.core.data.ExecutionDataStore;

import cfgcoverage.jacoco.coverage.CfgCoverage;
import cfgcoverage.jacoco.extension.ExtAnalyzer;
import codecoverage.jacoco.agent.ExecutionDataReporter;
import sav.common.core.SavException;

/**
 * @author LLT
 *
 */
public class CfgExecutionDataReporter extends ExecutionDataReporter {
	private Analysis analysis = new Analysis();
	private CfgCoverageCollector cfgCoverageCollector;

	public CfgExecutionDataReporter(String... targetFolders) {
		super(targetFolders);
		cfgCoverageCollector = new CfgCoverageCollector();
	}
	
	public void setTestcases(List<String> testMethods) {
		cfgCoverageCollector.setTestcases(testMethods);
	}

	@Override
	public void report(String execFile, String junitResultFile, List<String> testingClassNames) throws SavException {
		analysis.setTestingClassNames(testingClassNames);
		report(execFile, junitResultFile, analysis);
		Map<String, CfgCoverage> cfgCoverages = cfgCoverageCollector.getMethodCfgMap();
		System.out.println(cfgCoverages);
	}

	private class Analysis implements IAnalysis {
		private List<String> testingClassNames;
		private ExtAnalyzer analyzer;
		
		public void setTestingClassNames(List<String> testingClassNames) {
			this.testingClassNames = testingClassNames;
		}

		@Override
		public void initAnalyzer(ExecutionDataStore dataStore, ICoverageVisitor coverageBuilder) {
			analyzer = new ExtAnalyzer(dataStore, coverageBuilder);
			analyzer.setAnalyzerListener(cfgCoverageCollector);
			analyzer.setInsnHandler(cfgCoverageCollector);
		}

		@Override
		public void analyze(int testcaseIdx) throws IOException {
			for (String testingClassName : testingClassNames) {
				cfgCoverageCollector.setTestcaseIdx(testcaseIdx);
				analyzer.analyzeClass(getTargetClass(testingClassName),
						testingClassName);
			}
		}
		
		@Override
		public boolean accept(String coverageClassName) {
			// only analyze testing class
			return testingClassNames.contains(coverageClassName);
		}
	}
}
