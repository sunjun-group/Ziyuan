/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocaliation;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import main.FaultLocalization;
import sav.commons.AbstractTest;
import sav.commons.testdata.SampleProgramTestFail;
import sav.commons.testdata.SampleProgramTestPass;
import sav.commons.testdata.SamplePrograms;
import sav.strategies.IApplicationContext;
import sav.strategies.codecoverage.ICodeCoverage;
import sav.strategies.codecoverage.ICoverageReport;
import sav.strategies.dto.BreakPoint;
import sav.strategies.mutanbug.IMutator;
import sav.strategies.slicing.ISlicer;
import sav.strategies.vm.VMConfiguration;
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
					public List<BreakPoint> slice(List<BreakPoint> breakpoints,
							List<String> junitClassNames) throws Exception {
						return breakpoints;
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
			public VMConfiguration getVmConfig() {
				return initVmConfig();
			}
		});
		List<String> testingClasses = Arrays.asList(SamplePrograms.class.getName());
		List<String> junitClassNames = Arrays.asList(
				SampleProgramTestPass.class.getName(),
				SampleProgramTestFail.class.getName());
		List<LineCoverageInfo> result = analyzer.analyseSlicingFirst(
				testingClasses, Arrays.asList(""), junitClassNames,
				SpectrumAlgorithm.JACCARD).getLineCoverageInfos();
		System.out.println(result);
	}
	
}
