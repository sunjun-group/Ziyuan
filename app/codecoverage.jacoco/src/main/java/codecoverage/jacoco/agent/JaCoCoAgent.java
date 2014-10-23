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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import sav.common.core.ModuleEnum;
import sav.common.core.NullPrintStream;
import sav.common.core.SavException;
import sav.common.core.iface.IPrintStream;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;
import sav.strategies.codecoverage.ICodeCoverage;
import sav.strategies.codecoverage.ICoverageReport;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 *
 */
public class JaCoCoAgent implements ICodeCoverage {
	private static final String JUNIT_TEST_METHOD_PREFIX = "test";
	private static final String JUNIT_TEST_SUITE_PREFIX = "suite";
	private VMConfiguration vmConfig;
	private IPrintStream out = NullPrintStream.instance();
	private ICoverageReport report;
	private ExecutionDataReporter reporter;
	
	public JaCoCoAgent() {
		reporter = new ExecutionDataReporter();
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

	/**
	 * TODO: multithread!?!
	 */
	private void run(List<String> testingClassNames,
			List<String> junitClassNames) throws SavException, IOException,
			ClassNotFoundException {
		String destfile = File.createTempFile("tzJacoco", ".exec").getAbsolutePath();
		String junitResultFile = File.createTempFile("tzJunitRes", ".txt")
				.getAbsolutePath();
		JaCoCoVmRunner vmRunner = new JaCoCoVmRunner()
					.setDestfile(destfile)
					.setAppend(true);
		vmRunner.setOut(out);
		vmRunner.setAnalyzedClassNames(testingClassNames);
		vmConfig.setLaunchClass(JunitRunner.class.getName());
		reporter.setOut(out);
		reporter.setReport(report);
		List<String> testMethods = extractTestMethods(junitClassNames);
		@SuppressWarnings("unchecked")
		List<String> allClassNames = CollectionUtils.join(testingClassNames,
				junitClassNames);
		for (String testMethod : testMethods) {
			/* define arguments for JunitRunner */
			vmRunner.getProgramArgs().clear();
			vmRunner.addProgramArg(JunitRunnerParameters.DEST_FILE, junitResultFile);
			vmRunner.addProgramArg(JunitRunnerParameters.TESTING_CLASS_NAMES,
					allClassNames);
			vmRunner.addProgramArg(JunitRunnerParameters.CLASS_METHODS,
					testMethod);
			vmRunner.startAndWaitUntilStop(vmConfig);
		}
		
		reporter.report(destfile, junitResultFile, testingClassNames);
	}

	private List<String> extractTestMethods(List<String> junitClassNames)
			throws ClassNotFoundException {
		List<String> result = new ArrayList<String>();
		for (String className : junitClassNames) {
			Class<?> junitClass = Class.forName(className);
			Method[] methods = junitClass.getMethods();
			for (Method method : methods) {
				Test test = method.getAnnotation(Test.class);
				if (test != null
						|| (TestCase.class.isAssignableFrom(junitClass) && (method.getName()
								.startsWith(JUNIT_TEST_METHOD_PREFIX) || method.getName()
								.startsWith(JUNIT_TEST_SUITE_PREFIX)))) {
					result.add(StringUtils.dotJoin(className, method.getName()));
				}
			}
		}
		return result;
	}

	public void setVmConfig(VMConfiguration vmConfig) {
		this.vmConfig = vmConfig;
	}
	
	public ExecutionDataReporter getReporter() {
		return reporter;
	}
	
	public void setOut(IPrintStream out) {
		this.out = out;
	}
}
