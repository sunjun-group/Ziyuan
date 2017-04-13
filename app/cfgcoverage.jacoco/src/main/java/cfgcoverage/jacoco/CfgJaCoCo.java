/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco;

import java.util.List;

import codecoverage.jacoco.agent.ExecutionDataReporter;
import codecoverage.jacoco.agent.JaCoCo;
import sav.common.core.Pair;
import sav.common.core.utils.JunitUtils;
import sav.strategies.codecoverage.ICoverageReport;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;

/**
 * @author LLT
 *
 */
public class CfgJaCoCo extends JaCoCo {
	private CfgExecutionDataReporter report;
	
	public CfgJaCoCo(AppJavaClassPath appClasspath) {
		super(appClasspath);
	}

	@Override
	protected ExecutionDataReporter initReport(AppJavaClassPath appClasspath) {
		report = new CfgExecutionDataReporter(new String[] {
				appClasspath.getTarget(), appClasspath.getTestTarget() });
		return report;
	}
	
	public void run(List<String> testingClassNames,
			List<String> junitClassNames) throws Exception {
		List<String> testMethods = JunitUtils.extractTestMethods(junitClassNames);
		report.setTestcases(testMethods);
		super.run(getEmptyReporter(), testingClassNames, junitClassNames);
	}
	
	@Deprecated
	@Override
	public void run(ICoverageReport reporter, List<String> testingClassNames, List<String> junitClassNames)
			throws Exception {
		throw new UnsupportedOperationException();
	}

	private ICoverageReport getEmptyReporter() {
		return new ICoverageReport() {
			
			@Override
			public void setTestingClassNames(List<String> testingClassNames) {
			}
			
			@Override
			public void setFailTests(List<Pair<String, String>> failTests) {
			}
			
			@Override
			public void addInfo(int testcaseIndex, String className, int lineIndex, boolean isPassed, boolean isCovered) {
			}
			
			@Override
			public void addFailureTrace(List<BreakPoint> traces) {
			}
		};
	}
}
