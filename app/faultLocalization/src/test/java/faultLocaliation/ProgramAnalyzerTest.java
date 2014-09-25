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

import main.ProgramAnalyzer;

import org.junit.Test;

import faultLocalization.dto.LineCoverageInfo;

/**
 * @author LLT
 *
 */
public class ProgramAnalyzerTest extends AbstractFLTest {
	
	@Test
	public void testAnalyse() throws Exception {
		ProgramAnalyzer analyzer = new ProgramAnalyzer(getDataProvider());
		List<String> testingClasses = Arrays.asList("faultLocaliation.sample.SamplePrograms");
		List<String> junitClassNames = Arrays.asList("faultLocaliation.sample.SampleProgramTestPass",
				"faultLocaliation.sample.SampleProgramTestFail");
//		List<String> junitClassNames = Arrays.asList("faultLocaliation.sample.SampleProgramTestFail");
		List<LineCoverageInfo> result = analyzer.analyse(testingClasses, junitClassNames);
//		for (LineCoverageInfo info : result) {
//			System.out.println(info.getLocId());
//		}
	}

	
}
