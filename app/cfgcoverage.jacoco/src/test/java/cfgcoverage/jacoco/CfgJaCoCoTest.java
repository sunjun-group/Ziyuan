/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StopTimer;
import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.commons.testdata.SampleProgramTest;
import sav.commons.testdata.SamplePrograms;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public class CfgJaCoCoTest extends AbstractTest {

	public void run(List<String> targetMethods, List<String> testingClassNames, List<String> junitClassNames, String classesFolder)
			throws Exception {
		AppJavaClassPath appClasspath = initAppClasspath();
		appClasspath.addClasspath(classesFolder);
		CfgJaCoCo jacoco = new CfgJaCoCo(appClasspath);
		jacoco.runJunit(targetMethods, testingClassNames, junitClassNames);
	}
	
	@Test
	public void testSampleProgram() throws Exception {
		StopTimer timer = new StopTimer("test");
		timer.start();
		timer.newPoint("start");
		String targetClass = SamplePrograms.class.getName();
		List<String> testingClassNames = Arrays.asList(targetClass);
		List<String> junitClassNames = Arrays.asList(SampleProgramTest.class.getName());
//		List<String> junitClassNames = Arrays.asList(SamplePrograms1.class.getName());
		List<String> targetMethods = CollectionUtils.listOf(ClassUtils.toClassMethodStr(targetClass, "Max"),
				ClassUtils.toClassMethodStr(targetClass, "ifInloop"));
		run(targetMethods, testingClassNames, junitClassNames, TestConfiguration.SAV_COMMONS_TEST_TARGET);
		timer.newPoint("stop");
		System.out.println(timer.getResults());
	}

	
}
