/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest;

import gentest.data.Sequence;

import java.util.List;

import junit.FileCompilationUnitPrinter;
import junit.TestsPrinter;

import org.junit.Before;
import org.junit.Test;

import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.commons.testdata.SamplePrograms;
import sav.commons.testdata.simplePrograms.SimplePrograms;
import builder.FixTraceGentestBuilder;

/**
 * @author LLT
 *
 */
public class FixTraceGentestBuilderTest extends AbstractGTTest {
	private String srcPath;
	
	@Before
	public void beforeMethod()  {
		srcPath = config.getTestScrPath("gentest");
	}
	
	@Test
	public void testGenerate() throws SavException {
		FixTraceGentestBuilder builder = new FixTraceGentestBuilder(100);
		Pair<List<Sequence>, List<Sequence>> tcs = builder.forClass(SamplePrograms.class)
			.method("Max", "m")
			.evaluationMethod(SamplePrograms.class, "checkMax")
			.param("m.a", "m.b", "m.c", "m.return")
			.generate();
		TestsPrinter printer = new TestsPrinter(srcPath, 
				"testdata.gentest.pass", "testdata.gentest.fail", 
				"test", SamplePrograms.class.getSimpleName());
		printer.setCuPrinter(new FileCompilationUnitPrinter());
		printer.printTests(tcs);
	}
	
	
	@Test
	public void generateTc() throws SavException {
		FixTraceGentestBuilder builder = new FixTraceGentestBuilder(100);
		Pair<List<Sequence>, List<Sequence>> tcs = builder.forClass(SimplePrograms.class)
			.method("duplicatedNumber", "dup")
//			.evaluationMethod(SamplePrograms.class, "checkMax")
//			.param("m.a", "m.b", "m.c", "m.return")
			.generate();
		TestsPrinter printer = new TestsPrinter(srcPath, 
				"testdata.gentest", null, 
				"test", SimplePrograms.class.getSimpleName());
		printer.setCuPrinter(new FileCompilationUnitPrinter());
		printer.printTests(tcs);
	}
}
