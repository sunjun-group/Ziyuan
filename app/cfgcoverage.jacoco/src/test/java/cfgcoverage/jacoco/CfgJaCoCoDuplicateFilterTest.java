/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.testdata.Program;
import cfgcoverage.jacoco.testdata.ProgramTest2;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StopTimer;
import sav.common.core.utils.TextFormatUtils;
import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.strategies.dto.AppJavaClassPath;
import testdata.type.paramtype.VariableClass;
import testdata.type.paramtype.VariableClassTest;

/**
 * @author LLT
 *
 */
public class CfgJaCoCoDuplicateFilterTest extends AbstractTest {

	public void run(List<String> targetMethods, List<String> testingClassNames, List<String> junitClassNames,
			String classesFolder) throws Exception {
		AppJavaClassPath appClasspath = initAppClasspath();
		appClasspath.addClasspath(classesFolder);
		appClasspath.getPreferences().set(CfgJaCoCoParams.DUPLICATE_FILTER, true);
		CfgJaCoCo jacoco = new CfgJaCoCo(appClasspath);
		Map<String, CfgCoverage> result = jacoco.runBySimpleRunner(targetMethods, testingClassNames, junitClassNames);
		System.out.println(TextFormatUtils.printMap(result));
	}
	
	@Test
	public void testSampleProgram() throws Exception {
		StopTimer timer = new StopTimer("test");
		timer.start();
		timer.newPoint("start");
		String targetClass = Program.class.getName();
		List<String> testingClassNames = Arrays.asList(targetClass);
		List<String> junitClassNames = Arrays.asList(ProgramTest2.class.getName());
//		List<String> junitClassNames = Arrays.asList(ProgramTest1.class.getName());
		List<String> targetMethods = CollectionUtils.listOf(ClassUtils.toClassMethodStr(targetClass, "Max"),
				ClassUtils.toClassMethodStr(targetClass, "ifInloop"));
		run(targetMethods, testingClassNames, junitClassNames, TestConfiguration.getTestTarget("cfgcoverage.jacoco"));
		timer.newPoint("stop");
		System.out.println(timer.getResults());
	}

	@Test
	public void testParameterizedClass() throws Exception {
		String targetClass = VariableClass.class.getName();
		List<String> testingClassNames = Arrays.asList(targetClass);
		List<String> junitClassNames = Arrays.asList(VariableClassTest.class.getName());
		Method method = ClassUtils.loockupMethod(VariableClass.class, "method");
//		String methodId = CfgJaCoCoUtils.createMethodId(targetClass, method.getName(), SignatureUtils.getSignature(method));
		String methodId = ClassUtils.toClassMethodStr(targetClass, method.getName());
		List<String> targetMethods = CollectionUtils.listOf(methodId);
		run(targetMethods, testingClassNames, junitClassNames, TestConfiguration.getTestTarget("cfgcoverage.jacoco"));
	}
}
