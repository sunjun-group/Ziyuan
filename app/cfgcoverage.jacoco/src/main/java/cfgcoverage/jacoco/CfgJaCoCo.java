/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco;

import java.util.List;
import java.util.Map;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import sav.common.core.SavException;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
public class CfgJaCoCo {
	public static final String JACOCO_LOG_FILE = "jacocoLogFile";
	private CfgJaCoCoRunner runner;
	
	public CfgJaCoCo(AppJavaClassPath appClasspath) {
		runner = new CfgJaCoCoRunner();
		runner.appClasspath(appClasspath);
		runner.logFile(appClasspath.getPreferences().<String>get(JACOCO_LOG_FILE));
	}
	
	/**
	 * test method will be run by involking instead of start a junit request,
	 * this is used for simple testcases which helps to reduce running time consumption.
	 */
	public Map<String, CfgCoverage> runBySimpleRunner(List<String> targetMethods, List<String> testingClassNames,
			List<String> junitClassNames) throws SavException {
		return run(targetMethods, testingClassNames, junitClassNames, true);
	}
	
	/**
	 * 
	 * @param targetMethods 
	 * 				methods that we need to collect coverage
	 * 				format: className.methodName (ex: sav.commons.testdata.SamplePrograms.Max)
	 * @param testingClassNames
	 * 				classNames of classes we need to test (ex: sav.commons.testdata.SamplePrograms)
	 * @param junitClassNames
	 * 				junit tests (ex: sav.commons.testdata.SampleProgramTest)
	 */
	public Map<String, CfgCoverage> runJunit(List<String> targetMethods, List<String> testingClassNames,
			List<String> junitClassNames) throws SavException {
		return run(targetMethods, testingClassNames, junitClassNames, false);
	}
	
	public Map<String, CfgCoverage> run(List<String> targetMethods, List<String> testingClassNames,
			List<String> junitClassNames, boolean usingSimpleRunner) throws SavException {
		return runner.targetMethods(targetMethods)
				.targetClassNames(testingClassNames)
				.junitClassNames(junitClassNames)
				.usingSimpleRunner(usingSimpleRunner)
				.runAll();
	}
	
	public void reset() {
		runner.existingCoverage(null);
	}
	
	/**
	 * @param cfgCoverageMap the map between methodIds (className.methodName) and theirs existing cfgcoverage
	 */
	public void setCfgCoverageMap(Map<String, CfgCoverage> cfgCoverageMap) {
		runner.existingCoverage(cfgCoverageMap);
	}
}
