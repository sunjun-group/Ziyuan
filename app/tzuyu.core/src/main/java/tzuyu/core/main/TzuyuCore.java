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
import mutanbug.main.MutatorMain;
import sav.strategies.dto.ClassLocation;
import tzuyu.core.inject.ApplicationData;
import tzuyu.core.machinelearning.LearnInvariants;
import tzuyu.core.main.context.AbstractApplicationContext;
import tzuyu.core.mutantbug.MutanBug;
import faultLocalization.FaultLocalizationReport;


/**
 * @author LLT
 *
 */
public class TzuyuCore {
	private AbstractApplicationContext appContext;
	private ApplicationData appData;
	
	public TzuyuCore(AbstractApplicationContext appContext) {
		this.appContext = appContext;
		this.appData = appContext.getAppData();
	}

	public FaultLocalizationReport faultLocalization(List<String> testingClassNames,
			List<String> junitClassNames) throws Exception {
		return faultLocalization(testingClassNames, junitClassNames, true);
	}
	
	public FaultLocalizationReport faultLocalization(List<String> testingClassNames,
			List<String> junitClassNames, boolean useSlicer) throws Exception {
		FaultLocalization analyzer = new FaultLocalization(appContext);
		analyzer.setUseSlicer(useSlicer);
		FaultLocalizationReport report = analyzer.analyse(testingClassNames, junitClassNames,
				appData.getSuspiciousCalculAlgo());
		MutanBug mutanbug = new MutanBug();
		mutanbug.setAppData(appData);
		mutanbug.setMutator(new MutatorMain(appData.getAppSrc()));
		mutanbug.mutateAndRunTests(report, junitClassNames);
		return report;
	}
	
	public FaultLocalizationReport faultLocalization2(
			List<String> testingClassNames, List<String> testingPackages,
			List<String> junitClassNames, boolean useSlicer) throws Exception {
		FaultLocalization analyzer = new FaultLocalization(appContext);
		analyzer.setUseSlicer(useSlicer);
		FaultLocalizationReport report = analyzer.analyseSlicingFirst(testingClassNames, testingPackages,
				junitClassNames,
				appData.getSuspiciousCalculAlgo());
		MutanBug mutanbug = new MutanBug();
		mutanbug.setAppData(appData);
		mutanbug.setMutator(new MutatorMain(appData.getAppSrc()));
		mutanbug.mutateAndRunTests(report, junitClassNames);
		return report;
	}
	
	public FaultLocalizationReport SpectrumAndMachineLearning(List<String> testingClassNames,
			List<String> junitClassNames, boolean useSlicer) throws Exception {
		FaultLocalization analyzer = new FaultLocalization(appContext);
		analyzer.setUseSlicer(useSlicer);
		FaultLocalizationReport report = analyzer.analyse(testingClassNames, junitClassNames,
				appData.getSuspiciousCalculAlgo());
		
		List<ClassLocation> suspectLocations = report.getFirstRanksLocation(1);
		
		LearnInvariants learnInvariant = new LearnInvariants();
		learnInvariant.learn(suspectLocations, junitClassNames);
		
		return report;
	}
}
