/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionData.ProbesType;
import org.jacoco.core.runtime.AgentOptions;
import org.jacoco.core.runtime.SavMock;
import org.jacoco.core.runtime.AgentOptions.OutputMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import codecoverage.jacoco.agent.IExecutionReporter;
import codecoverage.jacoco.agent.JaCoCoVmRunner;
import sav.common.core.SavException;
import sav.common.core.SystemVariables;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.JunitUtils;
import sav.common.core.utils.StopTimer;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.junit.SavJunitRunner;
import sav.strategies.junit.jacocoMock.JaCoCoMock;
import sav.strategies.junit.jacocoMock.JaCoCoMockJunitRunner;
import sav.strategies.junit.jacocoMock.JaCoCoMockJunitRunner.JParameterProgramArgBuilder;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 *
 */
public class CfgJaCoCo {
	private Logger log = LoggerFactory.getLogger(CfgJaCoCo.class);
	
	private static final ProbesType probesType = ProbesType.INTEGER;
	private AppJavaClassPath appClasspath;
	private ExecutionReporter reporter;
	private Map<String, CfgCoverage> cfgCoverageMap;
	private StopTimer timer = new StopTimer("Run cfg coverage");
	
	public CfgJaCoCo(AppJavaClassPath appClasspath) {
		this.appClasspath = appClasspath;
		// set up jacoco
		ExecutionData.setProbesType(probesType);
	}
	
	/**
	 * test method will be run by involking instead of start a junit request,
	 * this is used for simple testcases which helps to reduce running time consumption.
	 */
	public Map<String, CfgCoverage> runBySimpleRunner(List<String> targetMethods, List<String> testingClassNames,
			List<String> junitClassNames) throws SavException, IOException,
			ClassNotFoundException {
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
	 * @return
	 * @throws SavException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Map<String, CfgCoverage> runJunit(List<String> targetMethods, List<String> testingClassNames,
			List<String> junitClassNames) throws SavException, IOException,
			ClassNotFoundException {
		return run(targetMethods, testingClassNames, junitClassNames, false);
	}
	
	public Map<String, CfgCoverage> run(List<String> targetMethods, List<String> testingClassNames,
			List<String> junitClassNames, boolean usingSimpleRunner) throws SavException, IOException,
			ClassNotFoundException {
		timer.start();
		reporter = new ExecutionReporter(targetMethods, new String[] {
				appClasspath.getTarget(), appClasspath.getTestTarget() });
		if (cfgCoverageMap != null) {
			reporter.setCfgCoverageMap(cfgCoverageMap);
		}
		run(reporter, testingClassNames, junitClassNames, usingSimpleRunner);
		timer.stop();
		timer.logResults(log);
		return reporter.getMethodCfgCoverageMap();
	}
	
	/**
	 * call jacoco and run code coverage.
	 */
	private void run(IExecutionReporter reporter, List<String> testingClassNames,
			List<String> junitClassNames, boolean usingSimpleRunner) throws SavException, IOException,
			ClassNotFoundException {
		log.debug("RUNNING JACOCO..");
		timer.newPoint("run jacoco");
		if (CollectionUtils.isEmpty(testingClassNames)) {
			log.warn("TestingClassNames is empty!!");
		}
		testingClassNames = CollectionUtils.initIfEmpty(testingClassNames);
		String destfile = File.createTempFile("cfgJacoco", ".exec").getAbsolutePath();
		JaCoCoVmRunner vmRunner = new JaCoCoVmRunner()
					.setOutputMode(OutputMode.none)
					.setDestfile(destfile)
					.setAppend(true);
		vmRunner.addAgentParam(AgentOptions.PROBESTYPE, probesType.name());
		String mockClass = JaCoCoMock.class.getName();
		vmRunner.addAgentParam(AgentOptions.SAVMOCKCLASSNAME, mockClass);
		vmRunner.setAnalyzedClassNames(testingClassNames);
		VMConfiguration vmConfig = SavJunitRunner.createVmConfig(appClasspath);
		vmConfig.setLaunchClass(JaCoCoMockJunitRunner.class.getName());
		List<String> testMethods = JunitUtils.extractTestMethods(junitClassNames, getPrjClassLoader());
		reporter.setTestcases(testMethods);
		@SuppressWarnings("unchecked")
		List<String> allClassNames = CollectionUtils.join(testingClassNames,
				junitClassNames);
		
		vmRunner.getProgramArgs().clear();
		List<String> arguments = new JParameterProgramArgBuilder()
				.usingSimpleRunner(usingSimpleRunner)
				.mockAccessClassName(mockClass)
				.mockAccessFieldName(SavMock.accessFieldName)
				.methods(testMethods)
				.testClassNames(allClassNames).build();
		vmRunner.setProgramArgs(arguments);
		vmRunner.startAndWaitUntilStop(vmConfig);
		
		reporter.report(destfile, null, testingClassNames);
	}
	
	private ClassLoader getPrjClassLoader() {
		return appClasspath.getPreferences().get(SystemVariables.PROJECT_CLASSLOADER);
	}
	
	public void reset() {
		cfgCoverageMap = null;
	}
	
	/**
	 * @param cfgCoverageMap the map between methodIds (className.methodName) and theirs existing cfgcoverage
	 */
	public void setCfgCoverageMap(Map<String, CfgCoverage> cfgCoverageMap) {
		this.cfgCoverageMap = cfgCoverageMap;
	}
}
