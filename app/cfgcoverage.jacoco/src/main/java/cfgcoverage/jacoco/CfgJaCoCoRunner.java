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
import org.jacoco.core.runtime.AgentOptions.OutputMode;
import org.jacoco.core.runtime.SavMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import codecoverage.jacoco.agent.JaCoCoVmRunner;
import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.SavRtException;
import sav.common.core.SystemVariables;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.FileUtils;
import sav.common.core.utils.JunitUtils;
import sav.common.core.utils.SingleTimer;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.junit.SavJunitRunner;
import sav.strategies.junit.jacocoMock.JaCoCoMock;
import sav.strategies.junit.jacocoMock.JaCoCoMockJunitRunner;
import sav.strategies.junit.jacocoMock.JaCoCoMockJunitRunner.JParameterProgramArgBuilder;
import sav.strategies.vm.VMConfiguration;
import sav.strategies.vm.VMRunner;

/**
 * @author LLT
 *
 */
public class CfgJaCoCoRunner {
	private Logger log = LoggerFactory.getLogger(CfgJaCoCoRunner.class);
	
	private static final ProbesType probesType = ProbesType.INTEGER;
	private AppJavaClassPath appClasspath;
	private ExecutionReporter reporter;
	private Map<String, CfgCoverage> cfgCoverageMap;
	
	/* running data */
	private boolean usingSimpleRunner;
	private List<String> targetMethods;
	private List<String> targetClassNames;
	private List<String> junitClassNames;
	private JaCoCoVmRunner vmRunner;
	private VMConfiguration vmConfig;
	private String destfile;
	private List<String> junitMethods;

	private String jacocoLogFile;
	private CfgJaCoCoConfigs config;
	
	public VMConfiguration appClasspath(AppJavaClassPath appClasspath) {
		this.appClasspath = appClasspath;
		// set up jacoco
		ExecutionData.setProbesType(probesType);
		vmConfig = SavJunitRunner.createVmConfig(appClasspath);
		vmConfig.setLaunchClass(JaCoCoMockJunitRunner.class.getName());
		config = new CfgJaCoCoConfigs(appClasspath);
		return vmConfig;
	}
	
	public CfgJaCoCoRunner reset() {
		usingSimpleRunner = false;
		targetMethods = null;
		targetClassNames = null;
		junitClassNames = null;
		vmRunner = null;
		vmConfig = null;
		destfile = null;
		cfgCoverageMap = null;
		return this;
	}
	
	public CfgJaCoCoRunner usingSimpleRunner(boolean usingSimpleRunner) {
		this.usingSimpleRunner = usingSimpleRunner;
		return this;
	}
	
	public CfgJaCoCoRunner targetClassNames(List<String> testingClassNames) {
		this.targetClassNames = testingClassNames;
		return this;
	}

	public CfgJaCoCoRunner targetMethods(List<String> targetMethods) {
		this.targetMethods = targetMethods;
		return this;
	}
	
	public CfgJaCoCoRunner junitClassNames(List<String> junitClassNames) {
		this.junitClassNames = junitClassNames;
		try {
			junitMethods = JunitUtils.extractTestMethods(junitClassNames, getPrjClassLoader());
		} catch (ClassNotFoundException e) {
			throw new SavRtException(e);
		}
		return this;
	}
	
	public CfgJaCoCoRunner existingCoverage(Map<String, CfgCoverage> cfgCoverageMap) {
		this.cfgCoverageMap = cfgCoverageMap;
		return this;
	}
	
	public Map<String, CfgCoverage> runAll() throws SavException {
		try {
			SingleTimer timer = SingleTimer.start("JaCoCo-execution");
			startVm();
			/* run */
			vmRunner.startAndWaitUntilStop(vmConfig);
			timer.logResults(log);
			/* report */
			Map<String, CfgCoverage> result = report();
			onFinish();
			return result;
		} catch (Exception e) {
			throw new SavException(e, ModuleEnum.UNSPECIFIED, e.getMessage());
		}
	}
	
	private void onFinish() {
		// clean up
		FileUtils.deleteFileByName(destfile);
		destfile = null;
	}

	public Map<String, CfgCoverage> report() throws SavException {
		try {
			ensureReporter();
			if (cfgCoverageMap != null) {
				reporter.setCfgCoverageMap(cfgCoverageMap);
			}
			reporter.setTestcases(junitMethods);
			reporter.report(destfile, null, targetClassNames);
			return reporter.getMethodCfgCoverageMap();
		} catch (Exception e) {
			throw new SavException(e, ModuleEnum.UNSPECIFIED, e.getMessage());
		}
	}

	private void ensureReporter() {
		if (reporter == null) {
			reporter = new ExecutionReporter(targetMethods,
					new String[] { appClasspath.getTarget(), appClasspath.getTestTarget() });
			reporter.setConfig(config);
		} else {
			reporter.reset(targetMethods, new String[] { appClasspath.getTarget(), appClasspath.getTestTarget()});
		}
	}
	
	/**
	 * call jacoco and run code coverage.
	 */
	public VMRunner startVm() throws SavException, IOException,
			ClassNotFoundException {
		log.debug("RUNNING JACOCO..");
		if (CollectionUtils.isEmpty(targetClassNames)) {
			log.warn("TestingClassNames is empty!!");
		}
		targetClassNames = CollectionUtils.initIfEmpty(targetClassNames);
		destfile = File.createTempFile("cfgJacoco", ".exec").getAbsolutePath();
		vmRunner = new JaCoCoVmRunner()
					.setOutputMode(OutputMode.none)
					.setDestfile(destfile)
					.setAppend(true);
		vmRunner.addAgentParam(AgentOptions.PROBESTYPE, probesType.name());
		String mockClass = JaCoCoMock.class.getName();
		vmRunner.addAgentParam(AgentOptions.SAVMOCKCLASSNAME, mockClass);
		if (jacocoLogFile != null) {
			vmRunner.addAgentParam(AgentOptions.SAVLOGFILE, jacocoLogFile);
		}
		vmRunner.setAnalyzedClassNames(targetClassNames);
		@SuppressWarnings("unchecked")
		List<String> allClassNames = CollectionUtils.join(targetClassNames,
				junitClassNames);
		
		vmRunner.getProgramArgs().clear();
		List<String> arguments = new JParameterProgramArgBuilder()
				.usingSimpleRunner(usingSimpleRunner)
				.mockAccessClassName(mockClass)
				.mockAccessFieldName(SavMock.accessFieldName)
				.methods(junitMethods)
				.testClassNames(allClassNames)
				.testcaseTimeout(config.getTimeout()).build();
		vmRunner.setProgramArgs(arguments);
		return vmRunner;
	}
	
	public VMConfiguration getVmConfig() {
		return vmConfig;
	}
	
	private ClassLoader getPrjClassLoader() {
		return appClasspath.getPreferences().get(SystemVariables.PROJECT_CLASSLOADER);
	}

	public void logFile(String logFile) {
		this.jacocoLogFile = logFile;
	}
}
