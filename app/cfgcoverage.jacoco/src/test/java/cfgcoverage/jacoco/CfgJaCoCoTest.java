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
import java.util.Map;

import org.junit.Test;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.testdata.LoopSample;
import cfgcoverage.jacoco.testdata.LoopSampleTest;
import cfgcoverage.jacoco.testdata.SwitchSample;
import cfgcoverage.jacoco.testdata.SwitchSampleTest;
import sav.common.core.SystemVariables;
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
	private boolean runSimpleRunner = false;
	private long timeout = 5000;

	public Map<String, CfgCoverage> run(List<String> targetMethods, List<String> testingClassNames, List<String> junitClassNames, String classesFolder)
			throws Exception {
		AppJavaClassPath appClasspath = initAppClasspath();
		appClasspath.getPreferences().set(SystemVariables.TESTCASE_TIMEOUT, timeout);
		appClasspath.addClasspath(classesFolder);
		CfgJaCoCo jacoco = new CfgJaCoCo(appClasspath);
		if (runSimpleRunner) {
			return jacoco.runBySimpleRunner(targetMethods, testingClassNames, junitClassNames);
		}
		return jacoco.runJunit(targetMethods, testingClassNames, junitClassNames);
	}
	
	private void runTest(Class<?> targetClass, Class<?> junitClass, String targetMethod) throws Exception {
		StopTimer timer = new StopTimer("test");
		timer.start();
		timer.newPoint("start");
		List<String> testingClassNames = Arrays.asList(targetClass.getName());
		List<String> junitClassNames = Arrays.asList(junitClass.getName());
		List<String> targetMethods = CollectionUtils.listOf(ClassUtils.toClassMethodStr(targetClass.getName(), targetMethod));
		Map<String, CfgCoverage> result = run(targetMethods, testingClassNames, junitClassNames, TestConfiguration.getTestTarget("cfgcoverage.jacoco"));
		timer.newPoint("stop");
		System.out.println(result.values());
		System.out.println(timer.getResults());
	}

	@Test
	public void testSampleProgram() throws Exception {
		runTest(SamplePrograms.class, SampleProgramTest.class, "Max");
	}


	@Test
	public void testLoopProgram() throws Exception {
		runSimpleRunner = true;
		runTest(LoopSample.class, LoopSampleTest.class, "run");
		runSimpleRunner = false;
	}
	
	@Test
	public void testSwitch() throws Exception {
		runSimpleRunner = true;
		runTest(SwitchSample.class, SwitchSampleTest.class, "getName");
		runSimpleRunner = false;
	}
}
