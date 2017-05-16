/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.junit.jacocoMock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.junit.JunitResult;
import sav.strategies.junit.JunitRunner;
import sav.strategies.junit.JunitRunnerParameters;
import sav.strategies.junit.Parameters;

/**
 * @author LLT
 *
 */
public class JaCoCoMockJunitRunner extends JunitRunner {
	public static final String ERROR_LOG_FILE = "JacocoMockJunitRunner.log"; 
	private JParameters params;
	private File logFile;
	private Object savMock;
	private Method savMockCollectDataMethod;
	
	public static void main(String[] args) throws Exception {
		if (CollectionUtils.isEmpty(args)) {
			System.exit(0);
		}
		JParameters params = JParameters.parse(args);
		JunitRunnerParameters junitParams = params.getJunitRunnerParams();

		JaCoCoMockJunitRunner junitRunner;
		if (params.isUsingSimpleRunner()) {
			junitRunner = new JaCoCoMockSimpleRunner();
		} else {
			junitRunner = new JaCoCoMockJunitRunner();
		}
		junitRunner.params = params;
		JunitResult result = junitRunner.runTestcases(junitParams);
		if (junitParams.getDestfile() != null) {
			File file = new File(junitParams.getDestfile());
			result.save(file, junitParams.isStoreTestResultDetail());
		}
	}
	/**
	 * make an access to jacoco to build execution result file.
	 * @throws ClassNotFoundException 
	 */
	@Override
	protected void onFinishTestCase(String classMethod) {
		try {
			if ((savMock == null || savMockCollectDataMethod == null) && params.getMockAccessClassName() != null) {
				// class which contains SavMock object 
				Class<?> clazz = Class.forName(params.getMockAccessClassName());
				// field of SavMock
				savMock = clazz.getField(params.getMockAccessFieldName()).get(null);
				// method collectData
				savMockCollectDataMethod = ClassUtils.loockupMethod(savMock.getClass(), "collectData");
			}
			savMockCollectDataMethod.invoke(savMock, classMethod);
		} catch (Exception ex) {
			/* write to error file */
			FileOutputStream out = null;
			try {
				if (logFile == null) {
					logFile = new File(ERROR_LOG_FILE);
					if (!logFile.exists()) {
						logFile.createNewFile();
					}
					out = new FileOutputStream(logFile);
					String id = Long.valueOf(System.currentTimeMillis()).toString();
					out.write(id.getBytes());
					out.write(ex.getClass().getName().getBytes());
					out.write(ex.getMessage().getBytes());
					out.write(ex.getStackTrace().toString().getBytes());
				}
			} catch (Exception e) {
				// do nothing.
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						// do nothing.
					}
				}
			}
		}
 	}
	
	public static class JParameters extends Parameters {
		private JunitRunnerParameters junitRunnerParams;
		private String mockAccessFieldName;
		private String mockAccessClassName;
		private boolean usingSimpleRunner;
		private static Options opts;
		
		/* supposed to be the class which is instrumented to add new field reference to SavMock 
		 * UUID for example.
		 * */
		public static final String MOCK_ACCESS_FIELD_NAME = "mockaccessfieldname";
		/* the field name which is added to common class to access, this field refers to SavMock object */
		public static final String MOCK_ACCESS_CLASS_NAME = "mockaccessclassname";
		/* flag to use JaCoCoMockSimpleRunner instead of using junit to run testcases, this might not work
		 * for complicated junit tests */
		public static final String USING_SIMPLE_RUNNER = "usingsimplerunner";
		
		static {
			opts = new Options();
			Collection<?> options = JunitRunnerParameters.opts.getOptions();
			for (Object opt : options) {
				opts.addOption((Option) opt);
			}
			opts.addOption(MOCK_ACCESS_CLASS_NAME, true,
					"the class which is instrumented to add new field reference to SavMock ");
			opts.addOption(MOCK_ACCESS_FIELD_NAME, true,
					"the field name which is added to common class to access, this field refers to SavMock object");
			opts.addOption(USING_SIMPLE_RUNNER, true, "use JaCoCoMockSimpleRunner to run instead of JaCoCoMockJunitRunner");
		}
		
		public static JParameters parse(String[] args) throws ParseException {
			CommandLine cmd = parse(opts, args);
			JParameters params = new JParameters();
			params.junitRunnerParams = JunitRunnerParameters.createFrom(cmd);
			params.mockAccessClassName = getOption(cmd, MOCK_ACCESS_CLASS_NAME);
			params.mockAccessFieldName = getOption(cmd, MOCK_ACCESS_FIELD_NAME);
			params.usingSimpleRunner = getBooleanOption(cmd, USING_SIMPLE_RUNNER);
			return params;
		}

		public JunitRunnerParameters getJunitRunnerParams() {
			return junitRunnerParams;
		}

		public String getMockAccessFieldName() {
			return mockAccessFieldName;
		}

		public String getMockAccessClassName() {
			return mockAccessClassName;
		}
		
		public boolean isUsingSimpleRunner() {
			return usingSimpleRunner;
		}
	}
	
	public static class JParameterProgramArgBuilder extends JunitRunnerProgramArgBuilder {
		public JParameterProgramArgBuilder mockAccessClassName(String className) {
			addArgument(JParameters.MOCK_ACCESS_CLASS_NAME, className);
			return this;
		}
		
		public JParameterProgramArgBuilder mockAccessFieldName(String fieldName) {
			addArgument(JParameters.MOCK_ACCESS_FIELD_NAME, fieldName);
			return this;
		}
		
		public JParameterProgramArgBuilder usingSimpleRunner(Boolean usingSimpleRunner) {
			addArgument(JParameters.USING_SIMPLE_RUNNER, usingSimpleRunner.toString());
			return this;
		}
	}
}
