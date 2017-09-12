/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core;

import learntest.core.commons.LearntestConstants;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.gentest.GentestParams;
import sav.common.core.utils.SignatureUtils;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public class LearntestParamsUtils {
	private LearntestParamsUtils() {
	}
	
	public static String getTestPackage(LearnTestParams params, GenTestPackage phaseType) {
		return String.format(phaseType.format, params.getApproach().getName(),
				params.getTargetMethod().getTargetClazz().getClassSimpleName().toLowerCase(),
				params.getTargetMethod().getMethodName().toLowerCase());
	}

	public static GentestParams createGentestParams(AppJavaClassPath appClasspath, LearnTestParams learntestParams,
			GenTestPackage gentestPackage) {
		GentestParams params = new GentestParams();
		params.setMethodExecTimeout(LearntestConstants.GENTEST_METHOD_EXEC_TIMEOUT);
		TargetMethod targetMethod = learntestParams.getTargetMethod();
		params.setMethodSignature(
				SignatureUtils.createMethodNameSign(targetMethod.getMethodName(), targetMethod.getMethodSignature()));
		params.setTargetClassName(targetMethod.getClassName());
		params.setNumberOfTcs(1);
		params.setTestPerQuery(1);
		params.setTestSrcFolder(appClasspath.getTestSrc());
		params.setTestPkg(getTestPackage(learntestParams, gentestPackage));
		params.setTestClassPrefix(targetMethod.getTargetClazz().getClassSimpleName());
		params.setTestMethodPrefix("test");
		params.setExtractTestcaseSequenceMap(true);
		return params;
	}
	
	/* testdata.[approachName].{init/result}.[classSimpleName].[methodName]*/
	public static enum GenTestPackage {
		INIT("testdata.%s.init.%s.%s"), 
		RESULT("testdata.%s.result.%s.%s");
		
		private String format;
		private GenTestPackage(String format) {
			this.format = format;
		}
		
	}
}
