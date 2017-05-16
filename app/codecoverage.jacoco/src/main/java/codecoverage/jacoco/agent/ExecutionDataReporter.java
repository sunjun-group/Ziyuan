/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package codecoverage.jacoco.agent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.utils.StopTimer;
import sav.strategies.codecoverage.ICoverageReport;
import sav.strategies.dto.BreakPoint;
import sav.strategies.junit.JunitResult;

/**
 * @author LLT
 *
 */
public class ExecutionDataReporter extends AbstractExecutionReporter implements IExecutionReporter {
	private ICoverageReport report;
	protected Logger log = LoggerFactory.getLogger(ExecutionDataReporter.class);
	
	public ExecutionDataReporter(String[] targetFolders) {
		super(targetFolders);
	}
	
	public void setReport(ICoverageReport report) {
		this.report = report;
	}
	
	public void report(String execFile, String junitResultFile,
			final List<String> testingClassNames) throws SavException {
		report(execFile, junitResultFile, new Analysis() {
			
			@Override
			public void analyze(Analyzer analyzer) throws IOException {
				for (String testingClassName : testingClassNames) {
					analyzer.analyzeClass(getTargetClass(testingClassName),
							testingClassName);
				}
			}
			
			@Override
			public boolean accept(String coverageClassName) {
				// do not display data for junit test file
				return testingClassNames.contains(coverageClassName);
			}
		});
	}
	
	protected void report(String execFile, String junitResultFile, Analysis analysis) throws SavException {
		StopTimer timer = new StopTimer("Collect coverage data");
		try {
			timer.newPoint("Read execFile");
			Map<String, List<ExecutionData>> execDataMap = read(execFile);
			JunitResult junitResult = JunitResult.readFrom(junitResultFile);
			timer.newPoint("Analyze data and count code coverage");
			report.setFailTests(junitResult.getFailTests());
			final CoverageBuilder coverageBuilder = new CoverageBuilder();
			ExecutionDataStore dataStore = new ExecutionDataStore();
			final Analyzer analyzer = new Analyzer(dataStore, coverageBuilder);
			int testcaseIdx = 0;
			for (String session : execDataMap.keySet()) {
				dataStore.reset();
				for (ExecutionData data : execDataMap.get(session)) {
					dataStore.put(data);
				}
				analysis.analyze(analyzer);
				/* report data */
				boolean isPass = junitResult.getResult(testcaseIdx);
				// Let's dump some metrics and line coverage information:
				for (final IClassCoverage cc : coverageBuilder.getClasses()) {
					String coverageClassName = JaCoCoUtils.getClassName(cc.getName());
					if (analysis.accept(coverageClassName)) {
						for (int j = cc.getFirstLine(); j <= cc
								.getLastLine(); j++) {
							ILine lineInfo = cc.getLine(j);
							if (lineInfo.getStatus() != ICounter.EMPTY) {
								boolean isCovered = lineInfo.getStatus() != ICounter.NOT_COVERED;
								report.addInfo(testcaseIdx, coverageClassName,
										j,
										isPass,
										isCovered);
							}

						}
					}
				}
				testcaseIdx++;
			}
			report.addFailureTrace(new ArrayList<BreakPoint>(junitResult
					.getFailureTraces()));
			timer.logResults(log);
		} catch (IOException e) {
			throw new SavException(ModuleEnum.SLICING, e);
		}
	}
	
	public void report(String execFile, String junitResultFile,
			final String targetFolder) throws SavException {
		report(execFile, junitResultFile, new Analysis() {

			@Override
			public void analyze(Analyzer analyzer) throws IOException {
				analyzer.analyzeAll(new File(targetFolder));
			}

			@Override
			public boolean accept(String clazz) {
				return true;
			}
		});
	}

	private static interface Analysis {
		void analyze(Analyzer analyzer) throws IOException;
		boolean accept(String coverageClassName);
	}
}
