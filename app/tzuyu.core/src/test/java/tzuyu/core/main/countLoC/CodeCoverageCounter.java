/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main.countLoC;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import codecoverage.jacoco.agent.ExecutionDataReporter;
import codecoverage.jacoco.agent.JaCoCo;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.strategies.IApplicationContext;
import sav.strategies.codecoverage.ICoverageReport;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import tzuyu.core.main.FaultLocateParams;
import tzuyu.core.main.TzuyuCore;

/**
 * @author LLT
 * 
 */
public class CodeCoverageCounter extends TzuyuCore {

	public CodeCoverageCounter(IApplicationContext appContext,
			AppJavaClassPath appData) {
		super(appContext);
	}

	public int count(FaultLocateParams params) throws Exception {
		CoverageCountReport countReport = new CoverageCountReport();
		JaCoCo codeCoverageTool = (JaCoCo) appContext.getCodeCoverageTool();
		ExecutionDataReporter reporter = getReporter();
		reporter.setReport(countReport);
		codeCoverageTool.run(reporter, params.getTestingClassNames(),
				params.getJunitClassNames());
		logBkps(countReport.bpks);
		return countReport.bpks.size();
	}
	
	private ExecutionDataReporter getReporter() {
		final AppJavaClassPath appClasspath = appContext.getAppData();
		return new ExecutionDataReporter(new String[]{appClasspath.getTarget()}) {
			@Override
			public void report(String execFile, String junitResultFile,
					Collection<String> testingClassNames) throws SavException {
				if (testingClassNames.isEmpty()) {
					super.report(execFile, junitResultFile, appClasspath.getTarget());
				} else {
					super.report(execFile, junitResultFile, testingClassNames);
				}
			}
		};
	}

	private void logBkps(List<BreakPoint> bkps) {
		for (BreakPoint bkp : bkps) {
			log.debug(bkp.getId());
		}
		log.debug("total lines = " + bkps.size());
	}

	private static class CoverageCountReport implements ICoverageReport {
		private List<BreakPoint> bpks = new ArrayList<BreakPoint>();

		@Override
		public void addFailureTrace(List<BreakPoint> traces) {
			// do nothing
		}

		@Override
		public void addInfo(int testcaseIndex, String className, int lineIndex,
				boolean isPassed, boolean isCovered) {
			if (isCovered) {
				bpks.add(new BreakPoint(className, lineIndex));
			}
		}

		@Override
		public void setTestingClassNames(List<String> testingClassNames) {
			// do nothing
		}

		@Override
		public void setFailTests(List<Pair<String, String>> failTests) {
			// do nothing
		}
		
	}
}
