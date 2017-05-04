/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jacop.core.Domain;

import cfgcoverage.jacoco.CfgJaCoCo;
import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import gentest.junit.TestsPrinter.PrintOption;
import learntest.calculator.OrCategoryCalculator;
import learntest.core.commons.data.DecisionNodeProbe;
import learntest.core.commons.data.DecisionProbes;
import learntest.core.commons.data.IDecisionNode;
import learntest.core.commons.data.testtarget.TargetMethod;
import learntest.core.commons.utils.DomainUtils;
import learntest.core.machinelearning.ITestCaseExecutor;
import learntest.core.machinelearning.JavailpSelectiveSampling;
import learntest.main.TestGenerator;
import learntest.main.TestGenerator.GentestResult;
import learntest.testcase.data.BranchType;
import learntest.testcase.data.BreakpointData;
import libsvm.core.Divider;
import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.formula.Eq;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StopTimer;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.vm.JavaCompiler;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 * this class holds all shared service and main data for learning process. 
 * The services should not care about how to run each other, and let the mediator take in to account that 
 * job.
 * 
 */
public class LearningMediator {
	/* services */
	private ITestCaseExecutor tcExecutor;
	private CfgJaCoCo cfgJacoco;
	private TestGenerator testGenerator;
	private JavaCompiler javaCompiler;
	private CfgJaCoCo cfgCoverage;
	private JavailpSelectiveSampling selectiveSampling;
	
	/* major data object */
	private DecisionProbes decisionProbes;
	private TargetMethod targetMethod;
	
	/* share utils and project configuration */
	private AppJavaClassPath appClassPath;
	private VMConfiguration vmConfig;
	private StopTimer timer;
	
	public LearningMediator(AppJavaClassPath appClassPath) {
		this.appClassPath = appClassPath;
		cfgCoverage = new CfgJaCoCo(appClassPath);
		selectiveSampling = new JavailpSelectiveSampling(this);
	}
	
	/**
	 * try to run testcases with new selected input for target method.
	 * @param originVars 
	 * @param list
	 * @return
	 * @throws SavException 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public Map<IDecisionNode, BreakpointData> runSamples(List<List<Eq<?>>> assignments, List<ExecVar> originVars)
			throws SavException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (List<Eq<?>> valSet : assignments) {
			if (!valSet.isEmpty()) {
				Map<String, Object> varMap = toInstrVarMap(valSet);
				list.add(varMap);
			}
		}
		logSample(list);
		List<Domain[]> domains = DomainUtils.buildSolutionsFromAssignments(assignments, originVars,
				decisionProbes.getTestInputs());
		try {
			GentestResult result = testGenerator.genTestAccordingToSolutions(domains, originVars, PrintOption.APPEND);
			getJavaCompiler().compile(appClassPath.getTestTarget(), result.getJunitfiles());
			/* run and update coverage */
			runCfgCoverage(result.getJunitClassNames());
		} catch (ClassNotFoundException e) {
			throw new SavException(ModuleEnum.TESTCASE_GENERATION, e, "");
		} catch (IOException e) {
			throw new SavException(ModuleEnum.TESTCASE_GENERATION, e, "");
		}
		return null;
	}
	
	public List<CfgCoverage> runCfgCoverage(List<String> junitClassNames)
			throws SavException, IOException, ClassNotFoundException {
		/* collect coverage and build cfg */
		timer.newPoint("start cfg coverage");
		List<String> targetMethods = CollectionUtils.listOf(targetMethod.getMethodFullName());
		List<CfgCoverage> coverage = cfgCoverage.run(targetMethods, Arrays.asList(targetMethod.getClassName()),
				junitClassNames);
		timer.newPoint("end cfg coverage");
		return coverage;
	}
	
	public JavaCompiler getJavaCompiler() {
		if (javaCompiler == null) {
			return new JavaCompiler(vmConfig);
		}
		return javaCompiler;
	}
	
	private void logSample(List<Map<String, Object>> list) {
		if(list != null){
			int size = list.size();
			System.out.println("Running " + size + " data points...");			
		}
	}
	
	private Map<String, Object> toInstrVarMap(List<Eq<?>> assignments) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Eq<?> asgt : assignments) {
			map.put(asgt.getVar().getLabel(), asgt.getValue());
		}
		return map;
	}

	/**
	 * after running selective sampling, decision coverages will be updated
	 * based on running new testcases which are generated based on new sample
	 * data.
	 * @return  updated node probe (with additional result of new tests generated from selective sampling).
	 */
	public DecisionNodeProbe selectiveSamplingForEmpty(DecisionNodeProbe target, List<ExecVar> originVars,
			OrCategoryCalculator precondition, List<Divider> current, BranchType missingBranch, boolean isLoop)
			throws SavException, SAVExecutionTimeOutException {
		selectiveSampling.selectDataForEmpty(target, originVars, precondition, current, missingBranch, isLoop);
		// TODO LLT: UPDATE THE NEW ONE.
		return target;
	}

	/**
	 * @return the decisionCoverages
	 */
	public DecisionProbes getDecisionProbes() {
		return decisionProbes;
	}
}
