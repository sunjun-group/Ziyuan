/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocaliation;

import java.util.ArrayList;
import java.util.Map;

import javacocoWrapper.JavaCoCo;

import org.junit.Test;

import faultLocaliation.sample.SampleProgramTest;
import faultLocaliation.sample.SamplePrograms;
import faultLocalization.dto.LineCoverage;

/**
 * @author LLT
 *
 */
public class ProgramTest {
	
	@Test
	public void testLineCounter() throws Exception {
		String testingClassName1 = SamplePrograms.class.getName();
		
		ArrayList<String> testingClassNames = new ArrayList<String>();
		testingClassNames.add(testingClassName1);
		
		
		JavaCoCo javacoco = new JavaCoCo(System.out);
		Map<String, LineCoverage> result = javacoco.run(testingClassNames, SampleProgramTest.class);
		for (LineCoverage coverage : result.values()) {
			analyze(coverage);
		}
		System.out.println(result);
	}

	private void analyze(LineCoverage coverage) {
		int errorLine = -1; 
		float max = 0;
		float totalFailed = coverage.totalFail();
		float totalPasses = coverage.totalPass();
		for (int line : coverage.getPotentialLines()) {
			int failed = coverage.failed(line);
			int passed = coverage.passed(line);
			float suspicious = (failed / totalFailed)
					/ ((passed / totalPasses) + (failed / totalFailed));
			if (suspicious > max) {
				max = suspicious;
				errorLine = line;
			}
			System.out.println(String.format("line: %s, suspicious: %s", line, suspicious));
		}
		System.out.println(String.format("ErrorLine: %s:line %s",
				coverage.getClassResourcePath(), errorLine));
	}
}
