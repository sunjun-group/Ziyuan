/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core;

import java.util.List;

import org.jacop.core.Domain;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.utils.BreakpointDataUtils;
import jdart.model.TestInput;
import learntest.core.commons.utils.DomainUtils;
import learntest.core.gentest.GentestParams;
import learntest.core.gentest.GentestResult;
import learntest.core.jdart.JdartTestInputUtils;
import learntest.main.LearnTestParams;
import learntest.main.RunTimeInfo;
import sav.common.core.SavException;
import sav.settings.SAVTimer;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class JDartLearntest extends LearnTest {

	public JDartLearntest(AppJavaClassPath appClasspath) {
		super(appClasspath);
	}
	
	@Override
	protected void prepareInitTestcase(LearnTestParams params) throws SavException {
		List<TestInput> inputs = generateTestAndRunJDart(params);
		if (inputs == null) {
			return;
		}
		List<BreakpointValue> bkpVals = JdartTestInputUtils.toBreakpointValue(inputs,
				params.getTargetMethod().getMethodFullName());
		List<ExecVar> vars = BreakpointDataUtils.collectAllVars(bkpVals);
		List<Domain[]> solutions = DomainUtils.buildSolutions(bkpVals, vars);
		GentestResult testResult = genterateTestFromSolutions(vars, solutions, false);
		params.getInitialTests().addJunitClass(testResult, appClasspath.getClassLoader());
	}
	
	public RunTimeInfo jdart(LearnTestParams params) throws Exception {
		SAVTimer.startCount();
		prepareInitTestcase(params);
		CfgCoverage cfgCoverage = runCfgCoverage(params.getTargetMethod(), params.getInitialTests().getJunitClasses());
		return getRuntimeInfo(cfgCoverage);
	}

	public List<TestInput> generateTestAndRunJDart(LearnTestParams params) throws SavException {
		GentestParams gentestParams = params.initGentestParams(appClasspath);
		/* generate testcase and jdart entry */
		gentestParams.setGenerateMainClass(true);
		GentestResult testResult = generateTestcases(gentestParams);
		params.getInitialTests().addJunitClass(testResult, appClasspath.getClassLoader());
		JDartRunner jdartRunner = new JDartRunner(appClasspath);
		return jdartRunner.runJDart(params);
	}
}
