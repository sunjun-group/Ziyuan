/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.util.List;

import main.ProgramAnalyzer;
import tzuyu.core.main.context.AbstractApplicationContext;
import faultLocalization.FaultLocalizationReport;


/**
 * @author LLT
 *
 */
public class TzuyuCore {
	private AbstractApplicationContext appContext;
	
	public TzuyuCore(AbstractApplicationContext appContext) {
		this.appContext = appContext;
	}

	public FaultLocalizationReport faultLocalization(List<String> testingClassNames,
			List<String> junitClassNames) throws Exception {
		return faultLocalization(testingClassNames, junitClassNames, true);
	}
	
	public FaultLocalizationReport faultLocalization(List<String> testingClassNames,
			List<String> junitClassNames, boolean useSlicer) throws Exception {
		ProgramAnalyzer analyzer = new ProgramAnalyzer(appContext);
		analyzer.setUseSlicer(useSlicer);
		return analyzer.analyse(testingClassNames, junitClassNames,
				appContext.getSuspiciousnessCalculationAlgorithm());
	}
	
	public FaultLocalizationReport faultLocalization2(
			List<String> testingClassNames, List<String> testingPackages,
			List<String> junitClassNames, boolean useSlicer) throws Exception {
		ProgramAnalyzer analyzer = new ProgramAnalyzer(appContext);
		analyzer.setUseSlicer(useSlicer);
		return analyzer.analyseSlicingFirst(testingClassNames, testingPackages,
				junitClassNames,
				appContext.getSuspiciousnessCalculationAlgorithm());
	}
}
