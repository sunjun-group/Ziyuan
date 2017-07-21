/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cfgcoverage.jacoco.CfgJaCoCo;
import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.utils.CfgJaCoCoUtils;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.core.commons.exception.LearnTestException;
import sav.common.core.SavException;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public class CodeCoverage {
	
	public CfgCoverage generateCoverage(AppJavaClassPath appClasspath, LearnTestParams params) throws LearnTestException {
		try {
			/* collect coverage and build cfg */
			TargetMethod targetMethod = params.getTargetMethod();
			CfgCoverage cfgCoverage = runCfgCoverage(appClasspath, targetMethod, params.getInitialTests().getJunitClasses());
			targetMethod.updateCfgIfNotExist(cfgCoverage.getCfg());
			return cfgCoverage;
		} catch (Exception e) {
			throw new LearnTestException(e);
		}
	}
	
	private CfgCoverage runCfgCoverage(AppJavaClassPath appClasspath, TargetMethod targetMethod, List<String> junitClasses)
			throws SavException, IOException, ClassNotFoundException {
		CfgJaCoCo cfgCoverage = new CfgJaCoCo(appClasspath);
		List<String> targetMethods = CollectionUtils.listOf(ClassUtils.toClassMethodStr(targetMethod.getClassName(),
				targetMethod.getMethodName()));
		Map<String, CfgCoverage> coverage = cfgCoverage.runJunit(targetMethods, Arrays.asList(targetMethod.getClassName()),
				junitClasses);
		return coverage.get(CfgJaCoCoUtils.createMethodId(targetMethod.getClassName(), targetMethod.getMethodName(),
				targetMethod.getMethodSignature()));
	}
}
