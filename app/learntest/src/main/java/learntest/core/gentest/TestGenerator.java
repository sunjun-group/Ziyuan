/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.gentest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gentest.builder.RandomTraceGentestBuilder;
import gentest.core.data.Sequence;
import gentest.junit.FileCompilationUnitPrinter;
import gentest.junit.PrinterParams;
import gentest.junit.TestsPrinter;
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
	private static Logger log = LoggerFactory.getLogger(TestGenerator.class);
	private ClassLoader prjClassLoader;
	
	public TestGenerator(AppJavaClassPath appClasspath) {
		this.prjClassLoader = appClasspath.getPreferences().get(SystemVariables.PROJECT_CLASSLOADER);
	}
	
	public GentestResult genTest(GentestParams params) throws ClassNotFoundException, SavException {
		PrinterParams printerParams = params.getPrinterParams();
		TestsPrinter printer = new TestsPrinter(printerParams);
		if (!params.generateMainClass()) {
			return gentest(params, printer);
		} 
		/* if main class is required to be generated, we need to do a little more */
		MainClassJWriter cuWriter = new MainClassJWriter(printerParams.getPkg(),
				printerParams.getClassPrefix());
		printer.setCuWriter(cuWriter);
		GentestResult result = gentest(params, printer);
		/* add main class */
		result.setMainClassName(TestsPrinter.getJunitClassName(cuWriter.getMainClass()));
		FileCompilationUnitPrinter cuPrinter = new FileCompilationUnitPrinter(printerParams.getSrcPath());
		cuPrinter.print(CollectionUtils.listOf(cuWriter.getMainClass(), 1));
		result.setMainClassFile(CollectionUtils.getFirstElement(cuPrinter.getGeneratedFiles()));
		return result;
	}
	
	protected GentestResult gentest(GentestParams params, TestsPrinter printer) throws SavException, ClassNotFoundException {
		log.debug("start random gentest..");
		Class<?> clazz = prjClassLoader.loadClass(params.getTargetClassName());
		RandomTraceGentestBuilder gentest = new RandomTraceGentestBuilder(params.getNumberOfTcs())
										.classLoader(prjClassLoader)
										.methodExecTimeout(params.getMethodExecTimeout())
										.queryMaxLength(params.getQueryMaxLength())
										.testPerQuery(params.getTestPerQuery())
										.forClass(clazz)
										.method(params.getMethodSignature());
		Pair<List<Sequence>, List<Sequence>> pair = gentest.generate() ;
		GentestResult result = new GentestResult();
		result.setJunitClassNames(printer.printTests(pair));
		result.setJunitfiles(((FileCompilationUnitPrinter) printer.getCuPrinter()).getGeneratedFiles());
		log.debug("generated junit classes: {}", result.getJunitClassNames());
		return result;
	}
	
}
