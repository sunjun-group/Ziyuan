/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco;

import java.util.List;

import org.junit.Test;

import codecoverage.jacoco.JaCoCoAgentTest;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public class CfgJaCoCoTest extends JaCoCoAgentTest {

	@Override
	public void run(List<String> testingClassNames, List<String> junitClassNames, String classesFolder)
			throws Exception {
		AppJavaClassPath appClasspath = initAppClasspath();
		appClasspath.addClasspath(classesFolder);
		CfgJaCoCo jacoco = new CfgJaCoCo(appClasspath);
		jacoco.run(testingClassNames, junitClassNames);
	}

	@Test
	@Override
	public void testSampleProgram() throws Exception {
		super.testSampleProgram();
	}
}
