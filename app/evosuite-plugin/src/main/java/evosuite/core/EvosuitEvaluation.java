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
import evosuite.core.EvosuiteTestcasesHandler.FilesInfo;
import evosuite.core.commons.CoverageUtils;
import evosuite.core.commons.IProgressMonitor;
import learntest.activelearning.core.settings.LearntestSettings;
import microbat.instrumentation.cfgcoverage.CoverageOutput;
import microbat.instrumentation.cfgcoverage.InstrumentationUtils;
import microbat.instrumentation.cfgcoverage.graph.CFGInstance;
import microbat.instrumentation.cfgcoverage.graph.CFGUtility;
import sav.common.core.Constants;
import sav.common.core.Pair;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.SignatureUtils;
import sav.common.core.utils.SingleTimer;
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
	private EvosuiteTestcasesHandler evosuiteTcHandler;
	private VMConfiguration evosuiteConfig;
	private LearntestSettings learntestSettings;
	
	public EvosuitEvaluation(VMConfiguration evosuiteConfig, LearntestSettings learntestSettings) {
		this.evosuiteConfig = evosuiteConfig;
		this.learntestSettings = learntestSettings;
	}

	public void run(AppJavaClassPath appClasspath, Configuration config, IProgressMonitor monitor){
		this.appClasspath = appClasspath;
		javaCompiler = new JavaCompiler(new VMConfiguration(appClasspath));
		coverageCounter = new CoverageCounter(appClasspath);
		evosuiteTcHandler = new EvosuiteTestcasesHandler(appClasspath);
		List<String> methods = config.loadValidMethods();
		run(config, methods, monitor);
	}
	
	public void run(Configuration config, List<String> methods, IProgressMonitor monitor) {
		Map<String, TargetClass> targetClassMap = toTargetClass(methods);
		for (TargetClass targetClass : targetClassMap.values()) {
			EvoJavaFileAdaptor adaptor = null;
			if (monitor.isCanceled()) {
				return;
			}
			try {
				adaptor = new EvoJavaFileAdaptor(appClasspath.getSrc(), targetClass);
				int line = 0;
				for (int i = 0; i < targetClass.getMethods().size(); i++) {
					if (monitor.isCanceled()) {
						return;
					}
					try {
						/* clean up base dir */
						String evoTestFolder = config.getEvoBaseDir() + EVO_TEST;
						org.apache.commons.io.FileUtils.deleteDirectory(new File(evoTestFolder));

						/* modify java file */
						line = targetClass.getMethodStartLines().get(i);
						if (!adaptor.enableMethod(line)) {
							continue;
						}
						log.debug("Run Evosuite for method " + i + "th: " + targetClass.getMethodFullName(i) + "." + targetClass.getMethodStartLines().get(i));
						javaCompiler.compile(appClasspath.getTarget(), adaptor.getSourceFile());
						
						/* run evosuite */
						EvosuitParams params = new EvosuitParams();
						params.setClasspath(appClasspath.getClasspathStr());
						params.setTargetClass(targetClass.getClassName());
						params.setMethod(targetClass.getMethods().get(i));
						params.setMethodPosition(adaptor.getStartLine(line), adaptor.getEndLine(line));
						params.setBaseDir(config.getEvoBaseDir());
						EvosuiteResult result = EvosuiteRunner.run(evosuiteConfig, params);
						CoverageOutput graphCoverage = null;
						CFGInstance cfgInstance = null;
						System.out.println();
						if (result.targetMethod != null) {
							FilesInfo junitFilesInfo = evosuiteTcHandler.getEvosuiteTestcases(config, targetClass.generatePackage(i), result);
							SingleTimer timer = SingleTimer.start("ZiyuanCoverage");
							CfgCoverage coverage = coverageCounter.calculateCoverage(result, junitFilesInfo);
							result.branchCoverage = CoverageUtils.calculateCoverageByBranch(coverage);
							System.out.println("Coverage calculated by Ziyuan: " + result.branchCoverage);
							System.out.println(timer.getResult());
							result.coverageInfo = CoverageUtils.getBranchCoverageDisplayTexts(coverage, -1);
							System.out.println(StringUtils.newLineJoin(result.coverageInfo));
							graphCoverage = coverageCounter.calculateCfgCoverage(result, junitFilesInfo, learntestSettings);
							CFGUtility cfgUtility = new CFGUtility();
							cfgInstance = cfgUtility.buildProgramFlowGraph(appClasspath,
									InstrumentationUtils.getClassLocation(result.targetClass, result.targetMethod),
									learntestSettings.getCfgExtensionLayer());
							cfgUtility.breakCircle(cfgInstance);
						}
						
						config.updateResult(targetClass.getMethodFullName(i), line, result, graphCoverage, cfgInstance);
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
