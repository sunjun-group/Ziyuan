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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
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
	private ICoverageReport report;
	private ExecutionDataReporter reporter;
	private AppJavaClassPath appClasspath;
	
	public JaCoCo(AppJavaClassPath appClasspath) {
		reporter = initReport(appClasspath);
		this.appClasspath = appClasspath;
		report = null;
	}

	protected ExecutionDataReporter initReport(AppJavaClassPath appClasspath) {
		return new ExecutionDataReporter(new String[] {
				appClasspath.getTarget(), appClasspath.getTestTarget() });
	}
	
	@Override
	public void run(ICoverageReport reporter, List<String> testingClassNames,
			List<String> junitClassNames) throws Exception {
		try { 
			this.report = reporter;
			run(testingClassNames, junitClassNames);
		} catch (IOException e) {
			throw new SavException(ModuleEnum.JVM, e);
		}
	}

	private void run(List<String> testingClassNames,
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
		vmRunner.setAnalyzedClassNames(testingClassNames);
		VMConfiguration vmConfig = SavJunitRunner.createVmConfig(appClasspath);
		vmConfig.setLaunchClass(JunitRunner.class.getName());
		reporter.setReport(report);
		List<String> testMethods = JunitUtils.extractTestMethods(junitClassNames);
		@SuppressWarnings("unchecked")
		List<String> allClassNames = CollectionUtils.join(testingClassNames,
				junitClassNames);
		if (log.isDebugEnabled()) {
			log.debug("Start vmRunner..");
			log.debug("destfile=", destfile);
			log.debug("junitResultFile=", junitResultFile);
			log.debug("append=true");
			log.debug("testMethods=", testMethods);
			log.debug("allClassNames=", allClassNames);
			log.debug("junitClassNames=", junitClassNames);
			log.debug("testingClassNames=", testingClassNames);
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
	
	protected ExecutionDataReporter getReporter() {
		return reporter;
	}
	
	public void setExecutionDataReporter(ExecutionDataReporter reporter) {
		this.reporter = reporter;
	}
}
