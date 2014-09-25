/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package main;

import faultLocalization.dto.CoverageReport;
import faultLocalization.dto.LineCoverageInfo;
import icsetlv.common.dto.BreakPoint;
import icsetlv.iface.ISlicer;

import java.util.List;

/**
 * @author LLT
 *
 */
public class ProgramAnalyzer {
	private ISlicer slicer;
	private ICodeCoverage codeCoverageTool;
	
	public ProgramAnalyzer(IDataProvider dataProvider) {
		slicer = dataProvider.getSlicer();
		codeCoverageTool = dataProvider.getCodeCoverageTool();
	}
	
	public List<LineCoverageInfo> analyse(List<String> testingClasses,
			List<String> junitClassNames) throws Exception {
		CoverageReport result = codeCoverageTool.run(testingClasses,
				junitClassNames);

		/* do slicing */
		slicer.setAnalyzedClasses(testingClasses);
		List<BreakPoint> causeTraces = slicer.slice(result.getFailureTraces(),
				junitClassNames);

		return result.tarantula(causeTraces);
	}
	
}
