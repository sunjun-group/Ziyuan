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
import faultLocalization.dto.ClassCoverageInSingleTestcase;
import faultLocalization.dto.CoverageReport;

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
		
		
		JavaCoCo javacoco = new JavaCoCo();
		CoverageReport result = javacoco.run(testingClassNames, SampleProgramTest.class);
		
		result.LocalizeFault();
	}
}
