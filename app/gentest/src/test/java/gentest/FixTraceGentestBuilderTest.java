/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest;

import gentest.builder.FixTraceGentestBuilder;
import gentest.core.data.Sequence;
import gentest.junit.TestsPrinter;

import java.util.List;

import org.junit.Test;

import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.commons.testdata.SamplePrograms;
import sav.commons.testdata.simplePrograms.SimplePrograms;

/**
 * @author LLT
 *
 */
public class FixTraceGentestBuilderTest extends AbstractGTTest {
	
	@Test
	public void testGenerate() throws SavException {
		FixTraceGentestBuilder builder = new FixTraceGentestBuilder(100);
		Pair<List<Sequence>, List<Sequence>> tcs = builder.forClass(SamplePrograms.class)
			.method("Max", "m")
			.evaluationMethod(SamplePrograms.class, "checkMax")
			.param("m.a", "m.b", "m.c", "m.return")
			.generate();
		TestsPrinter printer = new TestsPrinter(
				"testdata.gentest.pass", "testdata.gentest.fail", 
				"test", SamplePrograms.class.getSimpleName(), srcPath);
		printer.printTests(tcs);
	}
	
	
	@Test
	public void generateTc() throws SavException {
		FixTraceGentestBuilder builder = new FixTraceGentestBuilder(100);
		Pair<List<Sequence>, List<Sequence>> tcs = builder.forClass(
				SimplePrograms.class).generate();
		TestsPrinter printer = new TestsPrinter(
				"testdata.gentest.simpleprogram", null, "test",
				SimplePrograms.class.getSimpleName(), srcPath);
		printer.printTests(tcs);
	}
}
