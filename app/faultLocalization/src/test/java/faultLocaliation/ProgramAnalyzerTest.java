/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocaliation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.FaultLocalization;

import org.junit.Test;

import sav.common.core.SystemVariables;
import sav.commons.AbstractTest;
import sav.commons.testdata.SampleProgramTestFail;
import sav.commons.testdata.SampleProgramTestPass;
import sav.commons.testdata.SamplePrograms;
import sav.strategies.IApplicationContext;
import sav.strategies.codecoverage.ICodeCoverage;
import sav.strategies.codecoverage.ICoverageReport;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import sav.strategies.dto.SystemPreferences;
import sav.strategies.mutanbug.IMutator;
import sav.strategies.slicing.ISlicer;
import faultLocalization.LineCoverageInfo;
import faultLocalization.SpectrumBasedSuspiciousnessCalculator.SpectrumAlgorithm;

/**
 * @author LLT
 *
 */
public class ProgramAnalyzerTest extends AbstractTest {
	
	@Test
	public void testAnalyse() throws Exception {
		FaultLocalization analyzer = new FaultLocalization(new IApplicationContext() {
			
			@Override
			public ISlicer getSlicer() {
				return new ISlicer() {
					
					@Override
					public List<BreakPoint> slice(
							AppJavaClassPath appClassPath,
							List<BreakPoint> entryPoints,
							List<String> junitClassNames) throws Exception {
						return entryPoints;
					}
					
					@Override
					public void setFiltering(List<String> analyzedClasses,
							List<String> analyzedPackages) {
						
					}
				};
			}
			
			@Override
			public ICodeCoverage getCodeCoverageTool() {
				return new ICodeCoverage() {
					
					@Override
					public void run(ICoverageReport reporter, List<String> testingClassNames,
							List<String> junitClassNames) throws Exception {
						
					}
				};
			}

			@Override
			public IMutator getMutator() {
				return null;
			}

			@Override
			public AppJavaClassPath getAppData() {
				AppJavaClassPath appClasspath = initAppClasspath();
				config(appClasspath.getPreferences());
				return appClasspath;
			}
		});
		List<String> testingClasses = Arrays.asList(SamplePrograms.class.getName());
		List<String> junitClassNames = Arrays.asList(
				SampleProgramTestPass.class.getName(),
				SampleProgramTestFail.class.getName());
		List<LineCoverageInfo> result = analyzer.analyse(testingClasses,
				new ArrayList<String>(), junitClassNames)
				.getLineCoverageInfos();
		System.out.println(result);
	}

	protected void config(SystemPreferences preferences) {
		preferences.put(SystemVariables.FAULT_LOCATE_SPECTRUM_ALGORITHM,
				SpectrumAlgorithm.JACCARD.name());
		preferences.putBoolean(SystemVariables.FAULT_LOCATE_USE_SLICE, false);
	}
	
	
}
