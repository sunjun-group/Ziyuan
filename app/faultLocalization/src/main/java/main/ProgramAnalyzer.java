/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package main;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.JunitUtils;
import sav.strategies.IApplicationContext;
import sav.strategies.codecoverage.ICodeCoverage;
import sav.strategies.dto.BreakPoint;
import sav.strategies.slicing.ISlicer;
import faultLocalization.CoverageReport;
import faultLocalization.FaultLocalizationReport;
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

	public FaultLocalizationReport analyse(List<String> testingClasseNames,
			List<String> junitClassNames) throws Exception {
		return analyse(testingClasseNames, junitClassNames,
				SuspiciousnessCalculationAlgorithm.TARANTULA);
	}

	public FaultLocalizationReport analyse(List<String> testingClasses,
			List<String> junitClassNames, SuspiciousnessCalculationAlgorithm algorithm)
			throws Exception {
		FaultLocalizationReport report = new FaultLocalizationReport();
		CoverageReport result = new CoverageReport();
		codeCoverageTool.run(result, testingClasses, junitClassNames);
		report.setCoverageReport(result);
		if (useSlicer) {
			/* do slicing */
			List<BreakPoint> traces = result.getFailureTraces();
			slicer.setAnalyzedClasses(testingClasses);
			List<BreakPoint> causeTraces = slicer.slice(result.getFailureTraces(),
					JunitUtils.toClassMethodStrs(result.getFailTests()));
			for (BreakPoint bkp : causeTraces) {
				CollectionUtils.addIfNotNullNotExist(traces, bkp);
			}
			report.setLineCoverageInfos(result.tarantula(traces, algorithm));
		} else {
			report.setLineCoverageInfos(result.tarantula(new ArrayList<BreakPoint>(), algorithm));
		}
		return report;
	}

	public void setUseSlicer(boolean useSlicer) {
		this.useSlicer = useSlicer;
	}

}
