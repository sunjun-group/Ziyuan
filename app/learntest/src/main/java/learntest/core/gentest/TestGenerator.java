/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.gentest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gentest.builder.RandomTraceGentestBuilder;
import gentest.core.data.Sequence;
import gentest.junit.FileCompilationUnitPrinter;
import gentest.junit.TestsPrinter;
import icsetlv.common.dto.BreakpointValue;
import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.SystemVariables;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 * 
 */
/* LLT: THIS IS CREATED FOR A PURPOSE! */
public class TestGenerator {
	private ClassLoader prjClassLoader;
	
	public TestGenerator(AppJavaClassPath appClasspath) {
		this.prjClassLoader = appClasspath.getPreferences().get(SystemVariables.PROJECT_CLASSLOADER);
	}
	
	public GentestResult genTest(GentestParams params) throws ClassNotFoundException, SavException {
		TestsPrinter printer = new TestsPrinter(params.getTestPkg(), null, params.getTestMethodPrefix(), 
				params.getTestClassPrefix(), params.getTestSrcFolder());
		if (!params.generateMainClass()) {
			return gentest(params, printer);
		} 
		/* if main class is required to be generated, we need to do a little more */
		MainClassJWriter cuWriter = new MainClassJWriter(params.getTestPkg(), params.getTestClassPrefix());
		printer.setCuWriter(cuWriter);
		GentestResult result = gentest(params, printer);
		/* add main class */
		result.setMainClassName(TestsPrinter.getJunitClassName(cuWriter.getMainClass()));
		FileCompilationUnitPrinter cuPrinter = new FileCompilationUnitPrinter(params.getTestSrcFolder());
		cuPrinter.print(CollectionUtils.listOf(cuWriter.getMainClass(), 1));
		result.setMainClassFile(CollectionUtils.getFirstElement(cuPrinter.getGeneratedFiles()));
		return result;
	}
	
	protected GentestResult gentest(GentestParams params, TestsPrinter printer) throws SavException, ClassNotFoundException {
		Class<?> clazz = prjClassLoader.loadClass(params.getTargetClassName());
		RandomTraceGentestBuilder gentest = new RandomTraceGentestBuilder(params.getNumberOfTcs())
										.classLoader(prjClassLoader)
										.queryMaxLength(params.getQueryMaxLength())
										.testPerQuery(params.getTestPerQuery())
										.forClass(clazz)
										.method(params.getMethodSignature());
		Pair<List<Sequence>, List<Sequence>> pair = gentest.generate();
		GentestResult result = new GentestResult();
		result.junitClassNames = printer.printTests(pair);
		result.junitfiles = ((FileCompilationUnitPrinter) printer.getCuPrinter()).getGeneratedFiles();
		return result;
	}
	
	public static class GentestResult {
		private static GentestResult EMTPY_RESULT;
		List<String> junitClassNames;
		List<File> junitfiles;
		File mainClassFile;
		String mainClassName;
		List<BreakpointValue> inputData = new ArrayList<BreakpointValue>();
		
		public void addInputData(BreakpointValue value) {
			inputData.add(value);
		}

		public List<String> getJunitClassNames() {
			return junitClassNames;
		}

		public List<File> getJunitfiles() {
			return junitfiles;
		}
		
		public List<BreakpointValue> getTestInputs() {
			return inputData;
		}
		
		public static GentestResult getEmptyResult() {
			if (EMTPY_RESULT == null) {
				EMTPY_RESULT = new GentestResult();
			}
			return EMTPY_RESULT;
		}

		public void setJunitClassNames(List<String> junitClassNames) {
			this.junitClassNames = junitClassNames;
		}

		public void setJunitfiles(List<File> junitfiles) {
			this.junitfiles = junitfiles;
		}

		public void setInputData(List<BreakpointValue> inputData) {
			this.inputData = inputData;
		}
		
		public List<File> getAllFiles() {
			if (mainClassFile == null) {
				return getJunitfiles();
			}
			List<File> allfiles = new ArrayList<File>(junitfiles);
			allfiles.add(mainClassFile);
			return allfiles;
		}
		
		public String getMainClassNames() {
			return mainClassName;
		}

		public File getMainClassFile() {
			return mainClassFile;
		}

		public void setMainClassFile(File mainClassFile) {
			this.mainClassFile = mainClassFile;
		}

		public String getMainClassName() {
			return mainClassName;
		}

		public void setMainClassName(String mainClassName) {
			this.mainClassName = mainClassName;
		}
	}
}
