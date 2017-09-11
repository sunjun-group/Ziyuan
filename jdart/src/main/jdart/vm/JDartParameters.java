/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package jdart.vm;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import jdart.core.JDartParams;
import sav.strategies.junit.Parameters;
import sav.strategies.vm.ProgramArgumentBuilder;

/**
 * @author LLT
 *
 */
public class JDartParameters extends Parameters {
	public static final Options opts;
	static final String CLASSPATH_STR = "classpathStr";
	static final String MAIN_ENTRY = "mainEntry";
	static final String TARGET_CLASS_NAME = "targetClassName";
	static final String TARGET_METHOD_NAME = "targetMethodName";
	static final String TARGET_METHOD_PARAM_STR = "targetMethodParamStr";
	static final String JDART_APP_PROPERTIES = "jdartAppProperties";
	static final String JDART_SITE_PROPERTIES = "jdartSiteProperties";
	static final String TIME_LIMIT = "timelimit";
	static final String MIN_MEMORY_FREE = "memoryFree";
	static final String LIMIT_RESULT = "limitResult";
	static final String RESULT_FILE_PATH = "jdartResultFilePath";
	
	static {
		opts = new Options();
		opts.addOption(CLASSPATH_STR, true, "target application classpath");
		opts.addOption(MAIN_ENTRY, true, "main entry");
		opts.addOption(TARGET_CLASS_NAME, true, "target class name to learn");
		opts.addOption(TARGET_METHOD_NAME, true, "target method name to learn");
		opts.addOption(TARGET_METHOD_PARAM_STR, true, "target method arguments");
		opts.addOption(JDART_APP_PROPERTIES, true, "jdart app properties file path");
		opts.addOption(JDART_SITE_PROPERTIES, true, "jdart site properties file path");
		opts.addOption(TIME_LIMIT, true, "time limit");
		opts.addOption(MIN_MEMORY_FREE, true, "min memory");
		opts.addOption(LIMIT_RESULT, true, "maximum number of test input result");
		opts.addOption(RESULT_FILE_PATH, true, "jdart result file");
	}
	
	private String resultFile;
	private JDartParams jdartParams;
	
	public static List<String> buildVmProgramArgument(JDartParameters paramters) {
		JDartParams params = paramters.jdartParams;
		return new ProgramArgumentBuilder()
				.addArgument(CLASSPATH_STR, params.getClasspathStr())
				.addArgument(MAIN_ENTRY, params.getMainEntry())
				.addArgument(TARGET_CLASS_NAME, params.getClassName())
				.addArgument(TARGET_METHOD_NAME, params.getMethodName())
				.addArgument(TARGET_METHOD_PARAM_STR, params.getParamString())
				.addArgument(JDART_APP_PROPERTIES, params.getAppProperties())
				.addArgument(JDART_SITE_PROPERTIES, params.getSiteProperties())
				.addArgument(TIME_LIMIT, params.getTimeLimit())
				.addArgument(MIN_MEMORY_FREE, String.valueOf(params.getMinFree()))
				.addArgument(LIMIT_RESULT, params.getLimitNumberOfResultSet())
				.addArgument(RESULT_FILE_PATH, paramters.resultFile)
				.build();
	}
	
	public static JDartParameters parse(String[] args) throws ParseException {
		CommandLine cmd = parse(opts, args);
		return createFrom(cmd);
	}

	private static JDartParameters createFrom(CommandLine cmd) {
		JDartParameters paramters = new JDartParameters();
		JDartParams params = new JDartParams();
		params.setClasspathStr(getOption(cmd, CLASSPATH_STR));
		params.setMainEntry(getOption(cmd, MAIN_ENTRY));
		params.setClassName(getOption(cmd, TARGET_CLASS_NAME));
		params.setMethodName(getOption(cmd, TARGET_METHOD_NAME));
		params.setParamString(getOption(cmd, TARGET_METHOD_PARAM_STR));
		params.setAppProperties(getOption(cmd, JDART_APP_PROPERTIES));
		params.setSiteProperties(getOption(cmd, JDART_SITE_PROPERTIES));
		params.setTimeLimit(getLongOption(cmd, TIME_LIMIT));
		params.setMinFree(getLongOption(cmd, MIN_MEMORY_FREE));
		params.setLimitNumberOfResultSet(getIntOption(cmd, LIMIT_RESULT));
		paramters.jdartParams = params;
		paramters.resultFile = getOption(cmd, RESULT_FILE_PATH);
		return paramters;
	}

	public String getResultFile() {
		return resultFile;
	}

	public void setResultFile(String resultFile) {
		this.resultFile = resultFile;
	}

	public JDartParams getJdartParams() {
		return jdartParams;
	}

	public void setJdartParams(JDartParams jdartParams) {
		this.jdartParams = jdartParams;
	}
	
}
