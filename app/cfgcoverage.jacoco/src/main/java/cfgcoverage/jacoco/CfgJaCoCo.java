/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.runtime.AgentOptions;
import org.jacoco.core.runtime.AgentOptions.OutputMode;
import org.jacoco.core.runtime.SavMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import codecoverage.jacoco.agent.JaCoCoVmRunner;
import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.SystemVariables;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.FileUtils;
import sav.common.core.utils.SingleTimer;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.junit.JunitResult;
import sav.strategies.junit.SavJunitRunner;
import sav.strategies.junit.jacocoMock.JaCoCoMock;
import sav.strategies.junit.jacocoMock.JaCoCoMockJunitRunner;
import sav.strategies.junit.jacocoMock.JaCoCoMockJunitRunner.JParameterProgramArgBuilder;
import sav.strategies.vm.VMConfiguration;
import sav.strategies.vm.VMRunner;

/**
 * @author LLT
 * TODO LLT: make it clean when supporting cache component for multiple runs.
 */
public class CfgJaCoCo {
	private static final Logger log = LoggerFactory.getLogger(CfgJaCoCo.class);
	private AppJavaClassPath appClasspath;
	private ExecutionReporter reporter;
	private Map<String, CfgCoverage> cfgCoverageMap;
	
	public CfgJaCoCo(AppJavaClassPath appClasspath) {
		this.appClasspath = appClasspath;
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
	
	public Map<String, CfgCoverage> run(List<String> targetMethods, List<String> targetClassNames,
			List<String> junitClassNames, boolean usingSimpleRunner) throws SavException {
		CfgJaCoCoParams params = new CfgJaCoCoParams(appClasspath);
		params.setTargetMethods(targetMethods);
		params.setTargetClassNames(targetClassNames);
		params.setJunitClassNames(junitClassNames, appClasspath.getPreferences().<ClassLoader>get(SystemVariables.PROJECT_CLASSLOADER));
		params.setUsingSimpleRunner(usingSimpleRunner);
		return run(params);
	}
	
	public Map<String, CfgCoverage> run(CfgJaCoCoParams params) throws SavException {
		try {
			SingleTimer timer = SingleTimer.start("JaCoCo-execution");
			VMRunner vmRunner = startVm(params);;
			/* run */
			/* set up jacoco */
			ExecutionData.setProbesType(params.getProbesType());
			VMConfiguration vmConfig = SavJunitRunner.createVmConfig(appClasspath);
			vmConfig.setLaunchClass(JaCoCoMockJunitRunner.class.getName());
			vmRunner.startAndWaitUntilStop(vmConfig);
			timer.logResults(log);
			/* read coverage result from execution file and report */
			Map<String, CfgCoverage> result = report(params);
			/* update test result if needed */
			updateJunitResult(result, params);
			/* finish clean up */
			FileUtils.deleteFileByName(params.getJacocoExecFilePath());
			if (params.isCollectJunitResult()) {
				FileUtils.deleteFileByName(params.getJunitResultFile());
			}
			params.resetJacocoExecFilePath();
			return result;
		} catch (Exception e) {
			throw new SavException(e, ModuleEnum.UNSPECIFIED, e.getMessage());
		}
	}
	
	private void updateJunitResult(Map<String, CfgCoverage> coverages, CfgJaCoCoParams params) throws IOException {
		if (!params.isCollectJunitResult()) {
			return;
		}
		JunitResult result = JunitResult.readFrom(params.getJunitResultFile());
		for (CfgCoverage coverage : coverages.values()) {
			coverage.updateTestResult(params.getJunitMethods(), result.getTestResult());
		}
	}

	public Map<String, CfgCoverage> report(CfgJaCoCoParams params) throws SavException {
		try {
			ensureReporter(params);
			if (cfgCoverageMap != null) {
				reporter.setCfgCoverageMap(cfgCoverageMap);
			}
			reporter.setTestcases(params.getJunitMethods());
			reporter.report(params.getJacocoExecFilePath(), null, params.getTargetClassNames());
			return reporter.getMethodCfgCoverageMap();
		} catch (Exception e) {
			throw new SavException(e, ModuleEnum.UNSPECIFIED, e.getMessage());
		}
	}

	private void ensureReporter(CfgJaCoCoParams params) {
		if (reporter == null) {
			reporter = new ExecutionReporter(params.getTargetMethods(),
					new String[] { appClasspath.getTarget(), appClasspath.getTestTarget() });
			reporter.setDuplicateFilter(params.isDuplicateFilter());
		} else {
			reporter.reset(params.getTargetMethods(), new String[] { appClasspath.getTarget(), appClasspath.getTestTarget() });
		}
	}
	
	/**
	 * call jacoco and run code coverage.
	 */
	@SuppressWarnings("unchecked")
	public VMRunner startVm(CfgJaCoCoParams params) throws SavException, IOException,
			ClassNotFoundException {
		log.debug("RUNNING JACOCO..");
		if (CollectionUtils.isEmpty(params.getTargetClassNames())) {
			log.warn("TestingClassNames is empty!!");
		}
		List<String> targetClassNames = CollectionUtils.initIfEmpty(params.getTargetClassNames());
		JaCoCoVmRunner vmRunner = new JaCoCoVmRunner()
									.setOutputMode(OutputMode.none)
									.setDestfile(params.getJacocoExecFilePath())
									.setAppend(true);
		vmRunner.addAgentParam(AgentOptions.PROBESTYPE, params.getProbesType().name());
		String mockClass = JaCoCoMock.class.getName();
		vmRunner.addAgentParam(AgentOptions.SAVMOCKCLASSNAME, mockClass);
		if (params.getJacocoLogFile() != null) {
			vmRunner.addAgentParam(AgentOptions.SAVLOGFILE, params.getJacocoLogFile());
		}
		vmRunner.setAnalyzedClassNames(targetClassNames);
		List<String> allClassNames = CollectionUtils.<String>join(targetClassNames, params.getJunitClassNames());
		
		vmRunner.getProgramArgs().clear();
		List<String> arguments = new JParameterProgramArgBuilder()
										.usingSimpleRunner(params.isUsingSimpleRunner())
										.mockAccessClassName(mockClass)
										.mockAccessFieldName(SavMock.accessFieldName)
										.methods(params.getJunitMethods())
										.testClassNames(allClassNames)
										.destinationFile(params.getJunitResultFile())
										.testcaseTimeout(params.getTimeout())
										.build();
		vmRunner.setProgramArgs(arguments);
		return vmRunner;
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
