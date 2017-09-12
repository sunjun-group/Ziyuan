/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.machinelearning;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gentest.junit.TestsPrinter.PrintOption;
import learntest.core.AbstractLearningComponent;
import learntest.core.LearningMediator;
import learntest.core.commons.data.decision.DecisionProbes;
import learntest.core.commons.data.sampling.SamplingResult;
import learntest.core.gentest.GentestResult;
import learntest.core.machinelearning.iface.ISampleExecutor;
import sav.common.core.SavException;
import sav.common.core.SavExceptionType;
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
	
	public SampleExecutor(LearningMediator mediator, DecisionProbes decisionProbes) {
		super(mediator);
		this.decisionProbes = decisionProbes;
	} 
	
	/**
	 * try to run testcases with new selected input for target method.
	 */
	public SamplingResult runSamples(List<double[]> domains, List<ExecVar> originVars) throws SavException {
		StopTimer timer = new StopTimer("runSample");
		timer.start();
		SamplingResult samples = new SamplingResult(decisionProbes);
		log.debug("Executing {} samples...", domains.size());
		log.info("Executing sample data points : ");
		logGeneratedInputs(domains, originVars);
		GentestResult result = null;
		try {
			timer.newPoint("gentest & compile");
			result = mediator.genTestAndCompile(domains, originVars, PrintOption.APPEND);
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
			log.warn("sample execution fail:[{}] {} ", e, e.getMessage());
			if (result != null) {
				/* backup to analyze compilation error */
				if (SavException.isExceptionOfType(e, SavExceptionType.COMPILATION_ERROR)) {
					FileUtils.copyFilesSilently(result.getJunitfiles(), mediator.getAppClassPath().getTestSrc()
							+ "/compilationErrorBak");
				}
				FileUtils.deleteFiles(result.getJunitfiles());
			}
			timer.logResults(log);
			return null;
		}
		return samples;  
	}

	private void logGeneratedInputs(List<double[]> domains, List<ExecVar> originVars) {
		log.debug("vars: {}", originVars);
		StringBuilder builder = new StringBuilder();
		for (double[] value : domains) {
			builder.append(Arrays.toString(value));
			builder.append("\n");
		}
		log.debug(builder.toString());
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
