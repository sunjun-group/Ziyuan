/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package codecoverage.jacoco.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.data.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StopTimer;
import sav.strategies.codecoverage.ICoverageReport;
import sav.strategies.dto.BreakPoint;
import sav.strategies.junit.JunitResult;

/**
 * @author LLT
 *
 */
public class ExecutionDataReporter {
	private Logger log = LoggerFactory.getLogger(ExecutionDataReporter.class);
	private ICoverageReport report;
	private static final char JACOCO_FILE_SEPARATOR = '/';
	/* target folder of testing project */
	private List<String> targetFolders;
	
	public ExecutionDataReporter(String... targetFolders) {
		this.targetFolders = new ArrayList<String>();
		CollectionUtils.addIfNotNullNotExist(this.targetFolders, targetFolders);
	}

	public void setReport(ICoverageReport report) {
		this.report = report;
	}
	
	public void report(String execFile, String junitResultFile,
			List<String> testingClassNames) throws SavException {
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

				for (String testingClassName : testingClassNames) {
					analyzer.analyzeClass(getTargetClass(testingClassName),
							testingClassName);
				}
				/* report data */
				boolean isPass = junitResult.getResult(testcaseIdx);
				// Let's dump some metrics and line coverage information:
				for (final IClassCoverage cc : coverageBuilder.getClasses()) {
					// do not display data for junit test file
					String coverageClassName = getClassName(cc.getName());
					for (String className : testingClassNames) {
						if (coverageClassName.equals(className)) {
							for (int j = cc.getFirstLine(); j <= cc
									.getLastLine(); j++) {
								ILine lineInfo = cc.getLine(j);
								if (lineInfo.getStatus() != ICounter.EMPTY) {
									boolean isCovered = lineInfo.getStatus() != ICounter.NOT_COVERED;
									report.addInfo(testcaseIdx, className,
											j,
											isPass,
											isCovered);
								}

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
	
	/**
	 * TODO LLT: maybe refactor? this is duplicate with the other report function. 
	 */
	public void report(String execFile, String junitResultFile, String targetFolder) throws SavException {
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
				analyzer.analyzeAll(new File(targetFolder));
				/* report data */
				boolean isPass = junitResult.getResult(testcaseIdx);
				// Let's dump some metrics and line coverage information:
				for (final IClassCoverage cc : coverageBuilder.getClasses()) {
					// do not display data for junit test file
					String coverageClassName = getClassName(cc.getName());
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
				testcaseIdx++;
			}
			report.addFailureTrace(new ArrayList<BreakPoint>(junitResult
					.getFailureTraces()));
			timer.logResults(log);
		} catch (IOException e) {
			throw new SavException(ModuleEnum.SLICING, e);
		}
	}

	private String getClassName(String name) {
		return name.replace(JACOCO_FILE_SEPARATOR, '.');
	}

	private InputStream getTargetClass(String className) throws IOException {
		if (!targetFolders.isEmpty()) {
			for (String target : targetFolders) {
				File file = new File(ClassUtils.getClassFilePath(target, className));
				if (file.exists()) {
					return new FileInputStream(file);
				}
			}
		} else {
			String resource = JACOCO_FILE_SEPARATOR
					+ className.replace('.', JACOCO_FILE_SEPARATOR) + ".class";
			return getClass().getResourceAsStream(resource);
		}
		throw new SavRtException("Cannot find .class file for class " + className);
	}
	
	private Map<String, List<ExecutionData>> read(final String file)
			throws IOException {
		if (log.isDebugEnabled()) {
			log.debug("read exec file ", file);
		}
		final ExecutionDataIntStore dataStore = new ExecutionDataIntStore();
		FileInputStream in = new FileInputStream(file);
		ExecutionDataReader reader = new ExecutionDataReader(in);
		reader.setSessionInfoVisitor(new ISessionInfoVisitor() {
			public void visitSessionInfo(SessionInfo info) {
				dataStore.newSession(info.getId());
			}
		});
		reader.setExecutionDataVisitor(new IExecutionDataVisitor() {
			public void visitClassExecution(ExecutionData data) {
				dataStore.curData.add(data);
			}
		});
		reader.read();
		in.close();
		/*
		 * TODO LLT: log here looks useless.
		 * allow to configure the module to show log.
		 *  
		 */
//		if (log.isDebug()) {
//			for (String sessionId : dataStore.execDataMap.keySet()) {
//				log.debug("Session", sessionId);
//				for (ExecutionData data : dataStore.execDataMap.get(sessionId)) {
//					log.debug(data);
//				}
//			}
//		}
		return dataStore.execDataMap;
	}
	
	private static class ExecutionDataIntStore {
		List<ExecutionData> curData;
		Map<String, List<ExecutionData>> execDataMap;
		
		public ExecutionDataIntStore() {
			execDataMap = new LinkedHashMap<String, List<ExecutionData>>();
		}
		
		public void newSession(String sessionId) {
			curData = new ArrayList<ExecutionData>();
			execDataMap.put(sessionId, curData);
		}
	}
}
