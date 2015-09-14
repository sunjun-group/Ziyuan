/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main.countLoC;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.strategies.IApplicationContext;
import sav.strategies.codecoverage.ICoverageReport;
import sav.strategies.dto.BreakPoint;
import tzuyu.core.inject.ApplicationData;
import tzuyu.core.main.FaultLocateParams;
import tzuyu.core.main.TzuyuCore;
import codecoverage.jacoco.agent.ExecutionDataReporter;
import codecoverage.jacoco.agent.JaCoCoAgent;

/**
 * @author LLT
 * 
 */
public class CodeCoverageCounter extends TzuyuCore {

	public CodeCoverageCounter(IApplicationContext appContext,
			ApplicationData appData) {
		super(appContext, appData);
	}

	public int count(FaultLocateParams params) throws Exception {
		log.info("Running " + appData.getSuspiciousCalculAlgo());
		CoverageCountReport reporter = new CoverageCountReport();
		JaCoCoAgent codeCoverageTool = (JaCoCoAgent) appContext.getCodeCoverageTool();
		codeCoverageTool.setReporter(getReporter()); 
	
		codeCoverageTool.run(reporter, params.getTestingClassNames(),
				params.getJunitClassNames());
		logBkps(reporter.bpks);
		return reporter.bpks.size();
	}
	
	private ExecutionDataReporter getReporter() {
		return new ExecutionDataReporter(appData.getAppTarget()) {
			@Override
			public void report(String execFile, String junitResultFile,
					List<String> testingClassNames) throws SavException {
				if (testingClassNames.isEmpty()) {
					super.report(execFile, junitResultFile, appData.getAppTarget());
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
