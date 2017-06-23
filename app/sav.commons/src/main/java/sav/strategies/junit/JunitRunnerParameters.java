/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.junit;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import sav.common.core.SavRtException;
import sav.common.core.utils.JunitUtils;
import sav.strategies.vm.ProgramArgumentBuilder;

/**
 * @author LLT
 * 
 */
@SuppressWarnings("static-access")
public class JunitRunnerParameters extends Parameters {
	public static final Options opts;
	static final String CLASS_METHODS = "methods";
	static final String TESTING_CLASS_NAMES = "testingclass";
	static final String TESTING_PACKAGE_NAMES = "testingpkgs";
	static final String DEST_FILE = "destfile";
	static final String TESTCASE_TIME_OUT = "tc_timeout";
	static final String SINGLE_TEST_RESULT_DETAIL = "tc_result_detail";
	
	private List<String> classMethods;
	private List<String> testingClassNames;
	private List<String> testingPkgs;
	private String destfile;
	/* for each testcase, to make sure the testcase does not run forever */
	private long timeout; 
	private boolean storeTestResultDetail = false;
	
	static {
		opts = new Options();
		opts.addOption(classMethods());
		opts.addOption(testingClassNames());
		opts.addOption(testingPkgs());
		opts.addOption(destfile());
		opts.addOption(timeout());
		opts.addOption(singleTestResultDetail());
	}

	private static Option classMethods() {
		return OptionBuilder
				.withArgName(CLASS_METHODS)
				.withDescription(
						"methods with full name includes className and method name")
				.hasArgs()
				.isRequired()
				.create(CLASS_METHODS);
	}

	private static Option timeout() {
		return OptionBuilder
				.withArgName(TESTCASE_TIME_OUT)
				.withDescription("Timeout for each testcase to make sure it does not run forever")
				.hasArg()
				.isRequired(false)
				.create(TESTCASE_TIME_OUT);
	}

	private static Option testingPkgs() {
		return OptionBuilder
				.withArgName(TESTING_PACKAGE_NAMES)
				.withDescription(
						"Testing packages contain classes for extracting failure traces")
				.hasArgs()
				.isRequired(false)
				.create(TESTING_PACKAGE_NAMES);
	}

	private static Option destfile() {
		return OptionBuilder
				.withArgName(DEST_FILE)
				.withDescription("Name of file to save testing result")
				.hasArg()
				.isRequired(false)
				.create(DEST_FILE);
	}

	private static Option testingClassNames() {
		return OptionBuilder
				.withArgName(TESTING_CLASS_NAMES)
				.withDescription(
						"Testing class names for extracting failure traces")
				.hasArgs()
				.isRequired(false)
				.create(TESTING_CLASS_NAMES);
	}
	
	private static Option singleTestResultDetail() {
		return OptionBuilder
				.withArgName(SINGLE_TEST_RESULT_DETAIL)
				.withDescription("Store detail of test result (stacktrace)")
				.isRequired(false)
				.create(SINGLE_TEST_RESULT_DETAIL);
	}

	public static JunitRunnerParameters parse(String[] args) throws ParseException {
		CommandLine cmd = parse(opts, args);
		return createFrom(cmd);
	}

	public static JunitRunnerParameters createFrom(CommandLine cmd) {
		JunitRunnerParameters params = new JunitRunnerParameters();
		params.classMethods = getListStringOption(cmd, CLASS_METHODS);
		params.testingClassNames = getListStringOption(cmd, TESTING_CLASS_NAMES);
		params.testingPkgs = getListStringOption(cmd, TESTING_PACKAGE_NAMES);
		params.destfile = getOption(cmd, DEST_FILE);
		if (cmd.hasOption(TESTCASE_TIME_OUT)) {
			params.timeout = Long.valueOf(cmd.getOptionValue(TESTCASE_TIME_OUT));
		}
		params.storeTestResultDetail = getSingleOption(cmd, SINGLE_TEST_RESULT_DETAIL);
		return params;
	}
	
	public List<String> getClassMethods() {
		return classMethods;
	}

	public List<String> getTestingClassNames() {
		return testingClassNames;
	}
	
	public List<String> getTestingPkgs() {
		return testingPkgs;
	}
	
	public String getDestfile() {
		return destfile;
	}

	public void setClassMethods(List<String> classMethods) {
		this.classMethods = classMethods;
	}
	
	/**
	 * all test methods in the given junit classes will be detected automatically
	 * and add to the classMethods for the test.
	 */
	public void setJunitClasses(List<String> junitClassNames) {
		try {
			this.classMethods = JunitUtils.extractTestMethods(junitClassNames);
		} catch (ClassNotFoundException e) {
			throw new SavRtException(
					"cannot extract test methods from junit classes: ",
					e.getMessage());
		} 
	}

	public void setTestingClassNames(List<String> testingClassNames) {
		this.testingClassNames = testingClassNames;
	}
	
	public void setTestingPkgs(List<String> testingPkgs) {
		this.testingPkgs = testingPkgs;
	}

	public void setDestfile(String destfile) {
		this.destfile = destfile;
	}
	
	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout, TimeUnit unit) {
		this.timeout = unit.toMillis(timeout);
	}
	
	public boolean isStoreTestResultDetail() {
		return storeTestResultDetail;
	}

	public void setStoreTestResultDetail(boolean storeTestResultDetail) {
		this.storeTestResultDetail = storeTestResultDetail;
	}

	@Override
	public String toString() {
		return "JunitRunnerParameters [classMethods=" + classMethods
				+ ", testingClassNames=" + testingClassNames + ", testingPkgs="
				+ testingPkgs + ", destfile=" + destfile + ", timeout="
				+ timeout + ", storeTestResultDetail=" + storeTestResultDetail
				+ "]";
	}
	
	
	public static class JunitRunnerProgramArgBuilder extends ProgramArgumentBuilder {
		public JunitRunnerProgramArgBuilder methods(List<String> classMethods){
			addArgument(JunitRunnerParameters.CLASS_METHODS, classMethods);
			return this;
		}

		public JunitRunnerProgramArgBuilder method(String classMethod){
			addArgument(JunitRunnerParameters.CLASS_METHODS, classMethod);
			return this;
		}
		
		public JunitRunnerProgramArgBuilder destinationFile(String destFile){
			addArgument(JunitRunnerParameters.DEST_FILE, destFile);
			return this;
		}
		
		public JunitRunnerProgramArgBuilder testClassNames(List<String> testClassNames){
			addArgument(JunitRunnerParameters.TESTING_CLASS_NAMES, testClassNames);
			return this;
		}
		
		public JunitRunnerProgramArgBuilder testingPackages(List<String> testingPkgs) {
			addArgument(JunitRunnerParameters.TESTING_PACKAGE_NAMES, testingPkgs);
			return this;
		}
		
		public ProgramArgumentBuilder testcaseTimeout(long timeout) {
			if (timeout > 0) {
				addArgument(JunitRunnerParameters.TESTCASE_TIME_OUT, String.valueOf(timeout));
			}
			return this;
		}
		
		public JunitRunnerProgramArgBuilder testcaseTimeout(long timeout,
				TimeUnit unit) {
			testcaseTimeout(unit.toMillis(timeout));
			return this;
		}
		
		public JunitRunnerProgramArgBuilder storeSingleTestResultDetail() {
			addOptionArgument(JunitRunnerParameters.SINGLE_TEST_RESULT_DETAIL);
			return this;
		}
	}
}
