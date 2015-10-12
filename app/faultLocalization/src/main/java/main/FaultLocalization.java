/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.SavException;
import sav.common.core.SystemVariables;
import sav.common.core.utils.BreakpointUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.JunitUtils;
import sav.common.core.utils.StringUtils;
import sav.strategies.IApplicationContext;
import sav.strategies.codecoverage.ICodeCoverage;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.SystemPreferences;
import sav.strategies.junit.JunitResult;
import sav.strategies.junit.JunitRunner;
import sav.strategies.junit.JunitRunnerParameters;
import sav.strategies.slicing.ISlicer;
import faultLocalization.CoverageReport;
import faultLocalization.FaultLocalizationReport;
import faultLocalization.SpectrumBasedSuspiciousnessCalculator.SpectrumAlgorithm;

/**
 * @author LLT
 * 
 */
public class FaultLocalization {
	private static Logger log = LoggerFactory.getLogger(FaultLocalization.class);
	private ISlicer slicer;
	private ICodeCoverage codeCoverageTool;
	private AppJavaClassPath appClasspath;
	private boolean useSlicer;
	private SpectrumAlgorithm spectrumAlgorithm;

	public FaultLocalization(IApplicationContext appContext) {
		slicer = appContext.getSlicer();
		codeCoverageTool = appContext.getCodeCoverageTool();
		this.appClasspath = appContext.getAppData();
		setup(appClasspath.getPreferences());
	}

	private void setup(SystemPreferences preferences) {
		useSlicer = preferences
				.getBoolean(SystemVariables.FAULT_LOCATE_USE_SLICE);
		spectrumAlgorithm = SpectrumAlgorithm.valueOf(
				preferences.get(SystemVariables.FAULT_LOCATE_SPECTRUM_ALGORITHM));
	}
	
	public FaultLocalizationReport analyse(
			List<String> analyzedClasses, List<String> analyzedPackages,
			List<String> junitClassNames) throws Exception {
		setup(appClasspath.getPreferences());
		List<BreakPoint> filterPoints = Collections.emptyList();
		List<String> testingClasses = analyzedClasses;
		if (useSlicer) {
			Set<BreakPoint> traces = getSlicingTraces(analyzedClasses,
					analyzedPackages, junitClassNames);
			filterPoints = new ArrayList<BreakPoint>(traces);
			testingClasses = BreakpointUtils.extractClasses(traces);
		}
		// coverage
		FaultLocalizationReport report = new FaultLocalizationReport();
		CoverageReport result = new CoverageReport();
		if (testingClasses.isEmpty()) {
			return report;
		}
		if (log.isDebugEnabled()) {
			log.debug("Analyzing classes: ");
			log.debug(StringUtils.join(testingClasses, "\n"));
		}
		codeCoverageTool.run(result, testingClasses, junitClassNames);
		report.setCoverageReport(result);
		report.setLineCoverageInfos(result.computeSuspiciousness(filterPoints, spectrumAlgorithm));
		
		report.sort();
		return report;
	}

	private Set<BreakPoint> getSlicingTraces(List<String> analyzedClasses,
			List<String> analyzedPackages, List<String> junitClassNames)
			throws ClassNotFoundException, IOException, SavException, Exception {
		/*
		 * Run test cases first, and only slice the fail test cases
		 */
		JunitRunnerParameters params = new JunitRunnerParameters();
		params.setJunitClasses(junitClassNames);
		if(CollectionUtils.isEmpty(analyzedPackages)) {
			List<String> testingClasses = new ArrayList<String>(analyzedClasses);
			testingClasses.addAll(junitClassNames);
			params.setTestingClassNames(testingClasses );
		} else {
			params.setTestingPkgs(analyzedPackages);
			params.setTestingClassNames(analyzedClasses);
		}
		JunitResult jresult = JunitRunner.runTestcases(appClasspath, params);
		Set<BreakPoint> traces = jresult.getFailureTraces();
		/* do slicing */
		if (log.isDebugEnabled()) {
			log.debug("failureTraces=", BreakpointUtils.getPrintStr(traces));
		}
		slicer.setFiltering(analyzedClasses, analyzedPackages);
		List<BreakPoint> causeTraces = slicer.slice(appClasspath,
				new ArrayList<BreakPoint>(jresult.getFailureTraces()),
				JunitUtils.toClassMethodStrs(jresult.getFailTests()));
		traces.addAll(causeTraces);
		if (log.isDebugEnabled()) {
			log.debug("causeTraces=", BreakpointUtils.getPrintStr(traces));
		}
		return traces;
	}

}
