/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocaliation;

import icsetlv.TestConfiguration;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import faultLocalization.dto.LineCoverageInfo;

import main.IDataProvider;
import main.ProgramAnalyzer;

/**
 * @author LLT
 *
 */
public class ProgramAnalyzerTest {
	
	@Test
	public void testAnalyse() throws Exception {
		ProgramAnalyzer analyzer = new ProgramAnalyzer(getDataProvider());
		List<String> testingClasses = Arrays.asList("faultLocaliation.sample.SamplePrograms");
		List<String> junitClassNames = Arrays.asList("faultLocaliation.sample.SampleProgramTestPass",
				"faultLocaliation.sample.SampleProgramTestFail");
		List<LineCoverageInfo> result = analyzer.analyse(testingClasses, junitClassNames);
		for (LineCoverageInfo info : result) {
			System.out.println(info.getLocId());
		}
	}

	private IDataProvider getDataProvider() {
		return new MockDataProvider() {

			@Override
			protected String getTracerJarPath() {
				return "D:/_1_Projects/Tzuyu/tools/java-slicer/tracer.jar";
			}
			
			@Override
			protected List<String> getProjectClasspath() {
				TestConfiguration testConfig = TestConfiguration.getInstance();
				return Arrays.asList(
						testConfig.getTarget(
								TestConfiguration.FALTLOCALISATION),
						testConfig.getTestTarget(
								TestConfiguration.FALTLOCALISATION),
						testConfig.getJunitLib()
						);
			}
		};
	}
}
