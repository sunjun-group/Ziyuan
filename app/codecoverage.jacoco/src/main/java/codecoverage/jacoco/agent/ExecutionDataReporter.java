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
import org.jacoco.core.analysis.ICoverageVisitor;
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
	protected Logger log = LoggerFactory.getLogger(ExecutionDataReporter.class);
	protected ICoverageReport report;
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
			final List<String> testingClassNames) throws SavException {
		report(execFile, junitResultFile, getTestingClassAnalysis(testingClassNames));
	}

	protected void report(String execFile, String junitResultFile, IAnalysis analysis) throws SavException {
		StopTimer timer = new StopTimer("Collect coverage data");
		try {
			timer.newPoint("Read execFile");
			Map<String, List<ExecutionData>> execDataMap = read(execFile);
			JunitResult junitResult = JunitResult.readFrom(junitResultFile);
			timer.newPoint("Analyze data and count code coverage");
			report.setFailTests(junitResult.getFailTests());
			final CoverageBuilder coverageBuilder = new CoverageBuilder();
			ExecutionDataStore dataStore = new ExecutionDataStore();
			analysis.initAnalyzer(dataStore, coverageBuilder);
			int testcaseIdx = 0;
			for (String session : execDataMap.keySet()) {
				dataStore.reset();
				for (ExecutionData data : execDataMap.get(session)) {
					dataStore.put(data);
				}
				analysis.analyze(testcaseIdx);
				/* report data */
				boolean isPass = junitResult.getResult(testcaseIdx);
				// Let's dump some metrics and line coverage information:
				for (final IClassCoverage cc : coverageBuilder.getClasses()) {
					String coverageClassName = getClassName(cc.getName());
					if (analysis.accept(coverageClassName)) {
						for (int j = cc.getFirstLine(); j <= cc
								.getLastLine(); j++) {
							ILine lineInfo = cc.getLine(j);
							if (lineInfo.getStatus() != ICounter.EMPTY) {
								lineInfo.getInstructionCounter();
								
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
			throw new SavException(ModuleEnum.UNDEFINED, e);
		}
	}
	
	protected void report(String execFile, String junitResultFile,
			final String targetFolder) throws SavException {
		report(execFile, junitResultFile, getTargetFolderAnalysis(targetFolder));
	}

	private AbstractAnalysis getTargetFolderAnalysis(final String targetFolder) {
		return new AbstractAnalysis() {

			@Override
			public void analyze(int testcaseIdx) throws IOException {
				analyzer.analyzeAll(new File(targetFolder));
			}

			@Override
			public boolean accept(String clazz) {
				return true;
			}
		};
	}
	
	private AbstractAnalysis getTestingClassAnalysis(final List<String> testingClassNames) {
		return new AbstractAnalysis() {
			
			@Override
			public void analyze(int testcaseIdx) throws IOException {
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
		};
	}
	

	protected String getClassName(String name) {
		return name.replace(JACOCO_FILE_SEPARATOR, '.');
	}

	public InputStream getTargetClass(String className) throws IOException {
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
	
	protected Map<String, List<ExecutionData>> read(final String file)
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
	
	public interface IAnalysis {
		public void analyze(int testcaseIdx) throws IOException;
		public boolean accept(String coverageClassName);
		public void initAnalyzer(ExecutionDataStore dataStore, ICoverageVisitor coverageBuilder);
	}
	
	public abstract class AbstractAnalysis implements IAnalysis {
		protected Analyzer analyzer;

		@Override
		public void initAnalyzer(ExecutionDataStore dataStore, ICoverageVisitor coverageBuilder) {
			analyzer = new Analyzer(dataStore, coverageBuilder);
		}

	}

}
