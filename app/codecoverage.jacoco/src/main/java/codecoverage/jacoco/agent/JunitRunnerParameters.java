/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package codecoverage.jacoco.agent;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author LLT
 * 
 */
@SuppressWarnings("static-access")
public class JunitRunnerParameters {
	private static final Options opts;
	public static final String CLASS_METHODS = "methods";
	public static final String TESTING_CLASS_NAMES = "testingclass";
	public static final String DEST_FILE = "destfile";
	
	private List<String> classMethods;
	private List<String> testingClassNames;
	private String destfile;

	static {
		opts = new Options();
		opts.addOption(classMethods());
		opts.addOption(testingClassNames());
		opts.addOption(destfile());
	}

	private static Option classMethods() {
		return OptionBuilder
				.withArgName(CLASS_METHODS)
				.withDescription(
						"methods with full name includes className and method name")
				.hasArgs().isRequired()
				.create(CLASS_METHODS);
	}

	private static Option destfile() {
		return OptionBuilder
				.withArgName(DEST_FILE)
				.withDescription("Name of file to save testing result")
				.hasArg()
				.isRequired()
				.create(DEST_FILE);
	}

	private static Option testingClassNames() {
		return OptionBuilder
				.withArgName(TESTING_CLASS_NAMES)
				.withDescription(
						"Testing class names for extracting failure traces")
				.hasArgs()
				.isRequired()
				.create(TESTING_CLASS_NAMES);
	}

	public static JunitRunnerParameters parse(String[] args) throws ParseException {
		CommandLineParser parser = new GnuParser();
		CommandLine cmd = parser.parse(opts, args);
		if (cmd.getOptions().length == 0) {
			throw new ParseException("No specified option");
		}
		JunitRunnerParameters params = new JunitRunnerParameters();
		if (cmd.hasOption(CLASS_METHODS)) {
			params.classMethods = Arrays.asList(cmd.getOptionValues(CLASS_METHODS));
		}
		if (cmd.hasOption(TESTING_CLASS_NAMES)) {
			params.testingClassNames = Arrays.asList(cmd.getOptionValues(TESTING_CLASS_NAMES));
		}
		if (cmd.hasOption(DEST_FILE)) {
			params.destfile = cmd.getOptionValue(DEST_FILE);
		}
		return params;
	}

	public List<String> getClassMethods() {
		return classMethods;
	}

	public List<String> getTestingClassNames() {
		return testingClassNames;
	}
	
	public String getDestfile() {
		return destfile;
	}

	public void setClassMethods(List<String> classMethods) {
		this.classMethods = classMethods;
	}

	public void setTestingClassNames(List<String> testingClassNames) {
		this.testingClassNames = testingClassNames;
	}

	public void setDestfile(String destfile) {
		this.destfile = destfile;
	}
}
