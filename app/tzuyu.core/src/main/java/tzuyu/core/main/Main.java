/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.util.List;

import main.FaultLocalization;

import com.google.inject.Inject;

import faultLocalization.FaultLocalizationReport;

/**
 * @author LLT
 *
 */
public class Main {
	@Inject
	private FaultLocalization faultLocalization;
	@Inject 
	private ApplicationData appData;

	public FaultLocalizationReport faultLocalization(List<String> testingClassNames,
			List<String> junitClassNames) throws Exception {
		return faultLocalization(testingClassNames, junitClassNames, true);
	}
	
	public FaultLocalizationReport faultLocalization(List<String> testingClassNames,
			List<String> junitClassNames, boolean useSlicer) throws Exception {
		return faultLocalization.analyse(testingClassNames, junitClassNames,
				appData.getSuspiciousCalculAlgo());
	}
	
	public FaultLocalizationReport faultLocalization2(
			List<String> testingClassNames, List<String> testingPackages,
			List<String> junitClassNames, boolean useSlicer) throws Exception {
		return faultLocalization.analyseSlicingFirst(testingClassNames, testingPackages,
				junitClassNames,
				appData.getSuspiciousCalculAlgo());
	}

	public void setFaultLocalization(FaultLocalization faultLocalization) {
		this.faultLocalization = faultLocalization;
	}

	public void setAppData(ApplicationData appData) {
		this.appData = appData;
	}
}
