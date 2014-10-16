/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package main;

import java.util.List;

import sav.common.core.utils.CollectionUtils;
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
	
	public ProgramAnalyzer(IApplicationContext appContext) {
		slicer = appContext.getSlicer();
		codeCoverageTool = appContext.getCodeCoverageTool();
	}
	
	public List<LineCoverageInfo> analyse(List<String> testingClasseNames,
			List<String> junitClassNames) throws Exception {
		return analyse(testingClasseNames, junitClassNames, SuspiciousnessCalculationAlgorithm.TARANTULA);
	}
	
	public List<LineCoverageInfo> analyse(List<String> testingClasses,
			List<String> junitClassNames, SuspiciousnessCalculationAlgorithm algorithm) throws Exception {
		CoverageReport result = new CoverageReport();
		codeCoverageTool.run(result, testingClasses, junitClassNames);
		/* do slicing */
		slicer.setAnalyzedClasses(testingClasses);
		List<BreakPoint> causeTraces = slicer.slice(result.getFailureTraces(),
				junitClassNames);
		for (BreakPoint bkp : result.getFailureTraces()) {
			CollectionUtils.addIfNotNullNotExist(causeTraces, bkp);
		}
		return result.tarantula(causeTraces, algorithm);
	}
	
}
