/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest;

import gentest.builder.FixTraceGentestBuilder;
import gentest.builder.GentestBuilder;
import gentest.builder.RandomTraceGentestBuilder;
import gentest.junit.FileCompilationUnitPrinter;
import gentest.junit.TestsPrinter;

import org.junit.Test;

import sav.common.core.SavException;
import sav.common.core.utils.StringUtils;
import sav.commons.testdata.BoundedStack;
import sav.commons.testdata.autogeneration.FindMaxArray;
import sav.commons.testdata.autogeneration.FindMaxArray2D;
import sav.commons.testdata.autogeneration.FindMaxComplexMap;
import sav.commons.testdata.autogeneration.FindMaxCompositionArray;
import sav.commons.testdata.autogeneration.FindMaxList;
import sav.commons.testdata.autogeneration.FindMaxList2D;
import sav.commons.testdata.autogeneration.FindMaxList3D;
import sav.commons.testdata.autogeneration.FindMaxMap;
import sav.commons.testdata.autogeneration.FindMaxNums;
import sav.commons.testdata.autogeneration.FindMaxSet;
import sav.commons.testdata.autogeneration.FindMaxStatic;
import sav.commons.testdata.autogeneration.FindMaxString;
import sav.commons.testdata.autogeneration.FindMaxUtils;
import sav.commons.testdata.autogeneration.FindMaxWrapper;
import sav.commons.testdata.autogeneration.FindMaxWrapper1;

/**
 * @author LLT
 *
 */
public class GentestForTestdataRunner extends AbstractGTTest {
	protected static final int NUMBER_OF_TESTCASES = 100;
	protected static final int METHOD_PER_CLASS = 10;

	@Test
	public void testBoundedStack() throws SavException {
		RandomTraceGentestBuilder builder = new RandomTraceGentestBuilder(100);
		builder.queryMaxLength(7)
				.testPerQuery(10);
		Class<BoundedStack> targetClazz = BoundedStack.class;
		builder.forClass(targetClazz);
		printTc(builder, targetClazz);
	}
	
	@Test
	public void testFindMaxUtils() throws SavException {
		FixTraceGentestBuilder builder = new FixTraceGentestBuilder(NUMBER_OF_TESTCASES);
		builder.forClass(FindMaxUtils.class)
				.method("findMaxByToString");
		printTc(builder, FindMaxUtils.class);
	}
	
	@Test
	public void testFindMaxWrapper1() throws SavException {
		generateTestcase(FindMaxWrapper1.class);
	}
	
	@Test
	public void testFindMaxCompositionArray() throws SavException {
		generateTestcase(FindMaxCompositionArray.class); 
	}
	
	@Test
	public void testFindMaxStatic() throws SavException {
		generateTestcase(FindMaxStatic.class); 
	}
	
	@Test
	public void testFindMaxString() throws SavException {
		generateTestcase(FindMaxString.class); 
	}
	
	@Test
	public void testFindMaxComplexMap() throws SavException {
		generateTestcase(FindMaxComplexMap.class); 
	}
	
	@Test
	public void testFindMaxMap() throws SavException {
		generateTestcase(FindMaxMap.class); 
	}
	
	@Test
	public void testFindMaxList3D() throws SavException {
		generateTestcase(FindMaxList3D.class); 
	}
	
	@Test
	public void testFindMaxList2D() throws SavException {
		generateTestcase(FindMaxList2D.class); 
	}
	
	@Test
	public void testFindMaxInterface() throws SavException {
		generateTestcase(FindMaxWrapper.class); 
	}
	
	@Test
	public void testFindMaxSet() throws SavException {
		generateTestcase(FindMaxSet.class);
	}
	
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
		FixTraceGentestBuilder builder = new FixTraceGentestBuilder(NUMBER_OF_TESTCASES);
		builder.forClass(targetClazz).method("Max");
		return builder;
	}
	
	public void printTc(GentestBuilder<?> builder, Class<?> targetClazz) throws SavException {
		TestsPrinter printer = new TestsPrinter(srcPath,
				getTestPkg(targetClazz), null, "test",
				targetClazz.getSimpleName());
		printer.setMethodSPerClass(METHOD_PER_CLASS);
		printer.setCuPrinter(new FileCompilationUnitPrinter());
		printer.printTests(builder.generate());
	}

	private String getTestFailPkg(Class<?> targetClazz) {
		return StringUtils.dotJoin(getTestPkg(targetClazz), "fail");
	}

	private String getTestPkg(Class<?> targetClazz) {
		return StringUtils.dotJoin("testdata.gentest", targetClazz.getSimpleName());
	}
}
