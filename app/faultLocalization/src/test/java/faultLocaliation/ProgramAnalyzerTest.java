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

import main.ProgramAnalyzer;
import sav.commons.AbstractTest;
import sav.commons.testdata.SampleProgramTestFail;
import sav.commons.testdata.SamplePrograms;
import sav.strategies.IApplicationContext;
import sav.strategies.codecoverage.ICodeCoverage;
import sav.strategies.codecoverage.ICoverageReport;
import sav.strategies.dto.BreakPoint;
import sav.strategies.slicing.ISlicer;
import faultLocalization.LineCoverageInfo;

/**
 * @author LLT
 *
 */
public class ProgramAnalyzerTest extends AbstractTest {
	
	public void testAnalyse() throws Exception {
		ProgramAnalyzer analyzer = new ProgramAnalyzer(new IApplicationContext() {
			
			@Override
			public ISlicer getSlicer() {
				return new ISlicer() {
					
					@Override
					public List<BreakPoint> slice(List<BreakPoint> breakpoints,
							List<String> junitClassNames) throws Exception {
						return breakpoints;
					}
					
					@Override
					public void setAnalyzedClasses(List<String> analyzedClasses) {
						// TODO Auto-generated method stub
						
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
		});
		List<String> testingClasses = Arrays.asList(SamplePrograms.class.getName());
		List<String> junitClassNames = Arrays.asList(
				SampleProgramTestFail.class.getName(),
				SampleProgramTestFail.class.getName());
		List<LineCoverageInfo> result = analyzer.analyse(testingClasses, junitClassNames).getLineCoverageInfos();
	}
	
}
