/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.machinelearning;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gentest.junit.TestsPrinter.PrintOption;
import learntest.core.AbstractLearningComponent;
import learntest.core.LearningMediator;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.data.sampling.SamplingResult;
import learntest.core.commons.utils.VarSolutionUtils;
import learntest.core.gentest.GentestResult;
import learntest.core.machinelearning.iface.ISampleExecutor;
import sav.common.core.Constants;
import sav.common.core.SavException;
import sav.common.core.formula.Eq;
import sav.common.core.utils.FileUtils;
import sav.common.core.utils.StopTimer;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class SampleExecutor extends AbstractLearningComponent implements ISampleExecutor<SamplingResult> {
	private static Logger log = LoggerFactory.getLogger(SampleExecutor.class);
	/* data object in context */
	private DecisionProbes decisionProbes;
	private Set<String> prevSolutions;
	
	public SampleExecutor(LearningMediator mediator, DecisionProbes decisionProbes) {
		super(mediator);
		this.decisionProbes = decisionProbes;
		prevSolutions = new HashSet<String>();
	} 
	
	/**
	 * try to run testcases with new selected input for target method.
	 */
	public SamplingResult runSamples(List<List<Eq<?>>> assignments, List<ExecVar> originVars)
			throws SavException {
		StopTimer timer = new StopTimer("runSample");
		timer.start();
		SamplingResult samples = new SamplingResult(decisionProbes);
		List<double[]> domains = VarSolutionUtils.buildSolutionsFromAssignments(assignments, originVars,
				decisionProbes.getTestInputs());
		
		removeDuplicates(domains);
		log.debug("Number of samples: {}", domains.size());
		GentestResult result = null;
		try {
			timer.newPoint("gentest");
			log.debug("gentest..");
			result = getTestGenerator().genTestAccordingToSolutions(domains, originVars, PrintOption.APPEND);
			timer.newPoint("compile");
			log.debug("compile..");
			mediator.compile(result.getJunitfiles());
			samples.setNewInputData(result.getTestInputs());
			/* run and update coverage */
			timer.newPoint("coverage");
			log.debug("run coverage..");
			log.debug("new tcs: " + FileUtils.getFileNames(result.getJunitfiles()));
			runCfgCoverage(samples, result.getJunitClassNames());
			samples.updateNewData();
			timer.logResults(log);
		} catch (Exception e) {
			// LOG
			log.warn("sample execution fail: " + e.getMessage());
			if (result != null) {
				FileUtils.copyFilesSilently(result.getJunitfiles(), mediator.getAppClassPath().getTestSrc()
						+ "/compilationErrorBak");
				FileUtils.deleteFiles(result.getJunitfiles());
			}
			return null;
		}
		return samples;  
	}

	private void removeDuplicates(List<double[]> domains) {
		Iterator<double[]> it = domains.iterator();
		/* 
		 * domains 10: duplicate prev: 5, duplicate: 2 (3)
		 * add: 10 - 5 - 2 = 3
		 * remains: = add
		 */
		int count = 0;
		while(it.hasNext()) {
			double[] domain = it.next();
			StringBuilder sb = new StringBuilder();
			for (double val : domain) {
				sb.append(val).append(Constants.LOW_LINE);
			}
			String key = sb.toString();
			if (prevSolutions.contains(key)) {
				it.remove();
				count++;
			} else {
				prevSolutions.add(key);
			}
		}
		log.debug("prevSolutions.size = {}, duplicate: {}", prevSolutions.size(), count);
	}

	/**
	 * after running coverage, samples will be updated, the original
	 * decisionProbes will be modified as well. 
	 */
	public void runCfgCoverage(SamplingResult samples, List<String> junitClassNames)
			throws SavException, IOException, ClassNotFoundException {
		/* collect coverage and build cfg */
		StopTimer timer = new StopTimer("cfgCoverage");
		timer.newPoint("start cfg coverage");
		mediator.runCoverageForGeneratedTests(samples.getCfgCoverageMap(), junitClassNames);
		timer.newPoint("end cfg coverage");
	}
	
}
