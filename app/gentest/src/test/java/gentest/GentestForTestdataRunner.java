/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest;

import junit.FileCompilationUnitPrinter;
import junit.TestsPrinter;

import org.junit.Test;

import sav.common.core.SavException;
import sav.common.core.utils.StringUtils;
import sav.commons.testdata.autogeneration.FindMaxArray;
import sav.commons.testdata.autogeneration.FindMaxArray2D;
import sav.commons.testdata.autogeneration.FindMaxList;
import sav.commons.testdata.autogeneration.FindMaxNums;
import builder.FixTraceGentestBuilder;
import builder.GentestBuilder;

/**
 * @author LLT
 *
 */
public class GentestForTestdataRunner extends AbstractGTTest {

	@Test
	public void testFindMaxList() throws SavException {
		generateTestcase(FindMaxList.class);
	}
	
	@Test
	public void testFindMaxNums() throws SavException {
		generateTestcase(FindMaxNums.class);
	}
	
	@Test
	public void testFindMax() throws SavException {
		generateTestcase(FindMaxList.class);
	}
	
	@Test
	public void testFindMaxArray() throws SavException {
		generateTestcase(FindMaxArray.class);
	}
	
	@Test
	public void testFindMaxArray2D() throws SavException {
		generateTestcase(FindMaxArray2D.class);
	}
	
	private void generateTestcase(Class<?> targetClazz) throws SavException {
		printTc(getBuilderForFindMax(targetClazz), targetClazz);
	}
	
	public GentestBuilder<?> getBuilderForFindMax(Class<?> targetClazz) {
		FixTraceGentestBuilder builder = new FixTraceGentestBuilder(10);
		builder.forClass(targetClazz).method("Max");
		return builder;
	}
	
	public void printTc(GentestBuilder<?> builder, Class<?> targetClazz) throws SavException {
		TestsPrinter printer = new TestsPrinter(srcPath,
				getTestPkg(targetClazz), null, "test",
				targetClazz.getSimpleName());
		printer.setCuPrinter(new FileCompilationUnitPrinter());
		printer.printTests(builder.generate());
	}


	private String getTestPkg(Class<?> targetClazz) {
		return StringUtils.dotJoin("testdata.gentest", targetClazz.getSimpleName());
	}
}
