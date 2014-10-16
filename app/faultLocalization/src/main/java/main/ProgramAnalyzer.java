/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package main;

import java.util.List;

import sav.strategies.IApplicationContext;
import sav.strategies.codecoverage.ICodeCoverage;
import sav.strategies.dto.BreakPoint;
import sav.strategies.slicing.ISlicer;
import faultLocalization.CoverageReport;
import faultLocalization.LineCoverageInfo;
import faultLocalization.SuspiciousnessCalculator.SuspiciousnessCalculationAlgorithm;

/**
 * @author LLT
 * 
 */
public class ProgramAnalyzer {
	private ISlicer slicer;
	private ICodeCoverage codeCoverageTool;
	private boolean useSlicer = true; // Use slicer by default

	public ProgramAnalyzer(IApplicationContext appContext) {
		slicer = appContext.getSlicer();
		codeCoverageTool = appContext.getCodeCoverageTool();
	}

	public List<LineCoverageInfo> analyse(List<String> testingClasseNames,
			List<String> junitClassNames) throws Exception {
		return analyse(testingClasseNames, junitClassNames,
				SuspiciousnessCalculationAlgorithm.TARANTULA);
	}

	public List<LineCoverageInfo> analyse(List<String> testingClasses,
			List<String> junitClassNames, SuspiciousnessCalculationAlgorithm algorithm)
			throws Exception {
		CoverageReport result = new CoverageReport();
		codeCoverageTool.run(result, testingClasses, junitClassNames);
		/* do slicing */
		slicer.setAnalyzedClasses(testingClasses);
		List<BreakPoint> traces = result.getFailureTraces();
		if (useSlicer) {
			traces = slicer.slice(traces, junitClassNames);
		}
		return result.tarantula(traces, algorithm);

	}

	public void setUseSlicer(boolean useSlicer) {
		this.useSlicer = useSlicer;
	}

}
