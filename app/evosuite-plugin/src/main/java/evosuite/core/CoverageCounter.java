/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite.core;

import java.util.List;
import java.util.Map;

import cfgcoverage.jacoco.CfgJaCoCo;
import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import evosuite.core.EvosuiteRunner.EvosuiteResult;
import evosuite.core.EvosuiteTestcasesHandler.FilesInfo;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.core.commons.data.classinfo.ClassInfo;
import learntest.core.commons.data.classinfo.MethodInfo;
import microbat.instrumentation.cfgcoverage.CoverageOutput;
import sav.common.core.SavRtException;
import sav.common.core.utils.JunitUtils;
import sav.common.core.utils.SignatureUtils;
import sav.common.core.utils.TextFormatUtils;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public class CoverageCounter {
	private AppJavaClassPath appClasspath;
	
	public CoverageCounter(AppJavaClassPath appClassPath) {
		this.appClasspath = appClassPath;
	}
	
	public CfgCoverage calculateCoverage(EvosuiteResult result, FilesInfo junitFilesInfo) {
		try {
			CfgJaCoCo cfgJacoco = new CfgJaCoCo(appClasspath);
			cfgJacoco.setTimeout(5000l);
			Map<String, CfgCoverage> cfgCoverage = cfgJacoco.runBySimpleRunner(
					result.getTargetMethodAsList(),
					result.getTargetClassAsList(),
					junitFilesInfo.junitClasses);
			System.out.println(TextFormatUtils.printMap(cfgCoverage));
			return cfgCoverage.values().iterator().next();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SavRtException(e);
		}
	}
	
	public CoverageOutput calculateCfgCoverage(EvosuiteResult result, FilesInfo junitFilesInfo,
			LearntestSettings learntestSettings) throws Exception {
		try {
			learntest.activelearning.core.handler.CoverageCounter cvgCounter = new learntest.activelearning.core.handler.CoverageCounter(
					learntestSettings, false);
			MethodInfo targetMethod = new MethodInfo(new ClassInfo(result.targetClass));
			targetMethod.setMethodName(SignatureUtils.extractMethodName(result.targetMethod));
			targetMethod.setMethodSignature(SignatureUtils.extractSignature(result.targetMethod));
			List<String> testMethods = JunitUtils.extractTestMethods(junitFilesInfo.junitClasses,
					appClasspath.getClassLoader());
			return cvgCounter.runCoverage(targetMethod, testMethods, appClasspath, learntestSettings.getInputValueExtractLevel());
		} catch (Exception e) {
			e.printStackTrace();
			throw new SavRtException(e);
		}
	}
}
