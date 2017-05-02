/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import cfgcoverage.jacoco.CfgJaCoCo;
import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.commons.testdata.SampleProgramTest;
import sav.commons.testdata.SamplePrograms;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public class CfgCoverageTest extends AbstractTest {

	public void run(List<String> testingClassNames, List<String> junitClassNames, String classesFolder)
			throws Exception {
		AppJavaClassPath appClasspath = initAppClasspath();
		appClasspath.addClasspath(classesFolder);
		CfgJaCoCo jacoco = new CfgJaCoCo(appClasspath);
//		jacoco.run(testingClassNames, junitClassNames);
	}

	@Test
	public void testSampleProgram() throws Exception {
		List<String> testingClassNames = Arrays.asList(SamplePrograms.class.getName());
		List<String> junitClassNames = Arrays.asList(SampleProgramTest.class.getName());
		run(testingClassNames, junitClassNames, TestConfiguration.SAV_COMMONS_TEST_TARGET);
	}
}
