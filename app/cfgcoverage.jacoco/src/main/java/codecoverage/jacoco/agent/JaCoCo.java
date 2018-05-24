/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package codecoverage.jacoco.agent;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.SystemVariables;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.JunitUtils;
import sav.strategies.codecoverage.ICodeCoverage;
import sav.strategies.codecoverage.ICoverageReport;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.junit.JunitRunner;
import sav.strategies.junit.JunitRunner.JunitRunnerProgramArgBuilder;
import sav.strategies.junit.SavJunitRunner;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 *
 */
public class JaCoCo implements ICodeCoverage {
	private Logger log = LoggerFactory.getLogger(JaCoCo.class);
	private AppJavaClassPath appClasspath;
	private  Map<String, String> extraAgentParams; 
	
	public JaCoCo(AppJavaClassPath appClasspath) {
		this.appClasspath = appClasspath;
	}
	
	public void run(ICoverageReport report, List<String> testingClassNames,
			List<String> junitClassNames) throws Exception {
		try { 
			ExecutionDataReporter reporter = new ExecutionDataReporter(new String[] {
					appClasspath.getTarget(), appClasspath.getTestTarget() });
			reporter.setReport(report);
			run(reporter, testingClassNames, junitClassNames);
		} catch (IOException e) {
			throw new SavException(ModuleEnum.JVM, e);
		}
	}
	
	public void run(IExecutionReporter reporter, List<String> testingClassNames,
			List<String> junitClassNames) throws SavException, IOException,
			ClassNotFoundException {
		log.debug("RUNNING JACOCO..");
		if (CollectionUtils.isEmpty(testingClassNames)) {
			log.warn("TestingClassNames is empty!!");
		}
		testingClassNames = CollectionUtils.initIfEmpty(testingClassNames);
		String destfile = File.createTempFile("tzJacoco", ".exec").getAbsolutePath();
		String junitResultFile = File.createTempFile("tzJunitRes", ".txt")
				.getAbsolutePath();
		JaCoCoVmRunner vmRunner = new JaCoCoVmRunner()
					.setDestfile(destfile)
					.setAppend(true);
		addExtraAgentParams(vmRunner);
		vmRunner.setAnalyzedClassNames(testingClassNames);
		
		VMConfiguration vmConfig = SavJunitRunner.createVmConfig(appClasspath);
		vmConfig.setLaunchClass(JunitRunner.class.getName());
		List<String> testMethods = JunitUtils.extractTestMethods(junitClassNames, getPrjClassLoader());
		reporter.setTestcases(testMethods);
		@SuppressWarnings("unchecked")
		List<String> allClassNames = CollectionUtils.join(testingClassNames,
				junitClassNames);
		if (log.isDebugEnabled()) {
			log.debug("Start vmRunner..");
			log.debug("destfile={}", destfile);
			log.debug("junitResultFile={}", junitResultFile);
			log.debug("append=true");
			log.debug("testMethods={}", testMethods);
			log.debug("allClassNames={}", allClassNames);
			log.debug("junitClassNames={}", junitClassNames);
			log.debug("testingClassNames={}", testingClassNames);
		}
		for (String testMethod : testMethods) {
			/* define arguments for JunitRunner */
			vmRunner.getProgramArgs().clear();
		
			List<String> arguments = new JunitRunnerProgramArgBuilder()
					.method(testMethod).destinationFile(junitResultFile)
					.testClassNames(allClassNames).build();
			vmRunner.setProgramArgs(arguments);
			vmRunner.startAndWaitUntilStop(vmConfig);
		}
		
		reporter.report(destfile, junitResultFile, testingClassNames);
	}
	
	private ClassLoader getPrjClassLoader() {
		return appClasspath.getPreferences().get(SystemVariables.PROJECT_CLASSLOADER);
	}

	private void addExtraAgentParams(JaCoCoVmRunner vmRunner) {
		if (extraAgentParams == null) {
			return;
		}
		for (Entry<String, String> entry : extraAgentParams.entrySet()) {
			vmRunner.addAgentParam(entry.getKey(), entry.getValue());
		}
	}

	public void setMoreAgentParams(Map<String, String> extraAgentParams) {
		this.extraAgentParams = extraAgentParams;
	}
}
