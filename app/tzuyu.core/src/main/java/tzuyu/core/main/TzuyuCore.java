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
import mutation.mutator.Mutator;

import org.apache.commons.collections.CollectionUtils;

import sav.common.core.Logger;
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
	private static final Logger LOGGER = Logger.getDefaultLogger();
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
		mutanbug.setMutator(new Mutator());
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
		mutanbug.setMutator(new Mutator());
		mutanbug.mutateAndRunTests(report, junitClassNames);
		return report;
	}

	public FaultLocalizationReport doSpectrumAndMachineLearning(List<String> testingClassNames,
			List<String> testingPackages, List<String> junitClassNames, boolean useSlicer)
			throws Exception {
		final FaultLocalization analyzer = new FaultLocalization(appContext);
		analyzer.setUseSlicer(useSlicer);

		FaultLocalizationReport report;
		if (CollectionUtils.isEmpty(testingPackages)) {
			report = analyzer.analyse(testingClassNames, junitClassNames,
					appData.getSuspiciousCalculAlgo());
		} else {
			report = analyzer.analyseSlicingFirst(testingClassNames, testingPackages,
					junitClassNames, appData.getSuspiciousCalculAlgo());
		}

		List<ClassLocation> suspectLocations = report.getFirstRanksLocation(1);

		if (CollectionUtils.isEmpty(suspectLocations)) {
			LOGGER.warn("Did not find any place to add break point. SVM will not run.");
		} else {
			LearnInvariants learnInvariant = new LearnInvariants(appData.getVmConfig());
			learnInvariant.learn(suspectLocations, junitClassNames);
		}

		return report;
	}
}
