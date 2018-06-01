/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite.core;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import evosuite.core.EvosuiteRunner.EvosuiteResult;
import evosuite.core.commons.CoverageUtils;
import sav.common.core.Constants;
import sav.common.core.Pair;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.SignatureUtils;
import sav.common.core.utils.StringUtils;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.vm.JavaCompiler;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 *
 */
public class EvosuitEvaluation {
	private static Logger log = LoggerFactory.getLogger(EvosuitEvaluation.class);
	public static final String EVO_TEST = "/evosuite-tests";
	private AppJavaClassPath appClasspath;
	private JavaCompiler javaCompiler;
	private CoverageCounter coverageCounter;
	private VMConfiguration evosuiteConfig;
	
	public EvosuitEvaluation(VMConfiguration evosuiteConfig) {
		this.evosuiteConfig = evosuiteConfig;
	}

	public void run(AppJavaClassPath appClasspath, Configuration config){
		this.appClasspath = appClasspath;
		javaCompiler = new JavaCompiler(new VMConfiguration(appClasspath));
		coverageCounter = new CoverageCounter(appClasspath);
		List<String> methods = config.loadValidMethods();
		run(config, methods);
	}
	
	public void run(Configuration config, List<String> methods) {
		Map<String, TargetClass> targetClassMap = toTargetClass(methods);
		for (TargetClass targetClass : targetClassMap.values()) {
			EvoJavaFileAdaptor adaptor = null;
			try {
				adaptor = new EvoJavaFileAdaptor(appClasspath.getSrc(), targetClass);
				int line = 0;
				for (int i = 0; i < targetClass.getMethods().size(); i++) {
					try {
						/* clean up base dir */
						org.apache.commons.io.FileUtils.deleteDirectory(new File(config.getEvoBaseDir() + EVO_TEST));

						/* modify java file */
						line = targetClass.getMethodStartLines().get(i);
						if (!adaptor.enableMethod(line)) {
							continue;
						}
						log.debug("Run Evosuite for " + targetClass.getMethodFullName(i) + "." + targetClass.getMethodStartLines().get(i));
						javaCompiler.compile(appClasspath.getTarget(), adaptor.getSourceFile());
						
						/* run evosuite */
						EvosuitParams params = new EvosuitParams();
						params.setClasspath(appClasspath.getClasspathStr());
						params.setTargetClass(targetClass.getClassName());
						params.setMethod(targetClass.getMethods().get(i));
						params.setMethodPosition(adaptor.getStartLine(line), adaptor.getEndLine(line));
						params.setBaseDir(config.getEvoBaseDir());
						EvosuiteResult result = EvosuiteRunner.run(evosuiteConfig, params);
						if (result.targetMethod != null) {
							CfgCoverage coverage = coverageCounter.calculateCoverage(config, targetClass.generatePackage(i), result);
							result.branchCoverage = CoverageUtils.calculateCoverageByBranch(coverage);
							System.out.println("Coverage calculated by Ziyuan: " + result.branchCoverage);
							result.coverageInfo = CoverageUtils.getBranchCoverageDisplayTexts(coverage, -1);
							System.out.println(StringUtils.newLineJoin(result.coverageInfo));
						}
						config.updateResult(targetClass.getMethodFullName(i), line, result);
					} catch (Exception e) {
						revert(adaptor);
						log.debug(e.getMessage());
						System.out.println(e);
						config.logError(targetClass.getMethodFullName(i), line);
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				continue;
			} finally {
				revert(adaptor);
			}
		}
	}

	private void revert(EvoJavaFileAdaptor adaptor) {
		if (adaptor != null) {
			try {
				adaptor.revertAll();
				javaCompiler.compile(appClasspath.getTarget(), adaptor.getSourceFile());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static Map<String, TargetClass> toTargetClass(List<String> methods) {
		Map<String, TargetClass> map = new LinkedHashMap<String, TargetClass>();
		for (String name : methods) {
			try {
				int idx = name.lastIndexOf(Constants.DOT);
				if (idx > 0) {
					String classMethod = name.substring(0, idx);
					int line = Integer.valueOf(name.substring(idx + 1));
					Pair<String, String> pair = ClassUtils.splitClassMethod(classMethod);
					String className = pair.a;
					TargetClass targetClass = map.get(className);
					if (targetClass == null) {
						targetClass = new TargetClass();
						targetClass.setClassName(className);
						map.put(className, targetClass);
					}
					targetClass.addMethod(name, pair.b, line, className + "." + SignatureUtils.extractMethodName(pair.b));
				} else {
					throw new IllegalArgumentException();
				}
			} catch(Exception e) {
				System.out.println("Error format: " + name);
			}
		}
		return map;
	}
}
