/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package gentest;

import gentest.builder.GentestBuilder;
import gentest.core.data.Sequence;
import gentest.core.data.statement.Statement;
import gentest.core.data.statement.Statement.RStatementKind;
import gentest.junit.TestsPrinter;
import gentest.junit.TestsPrinter.PrintOption;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.Pair;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;
import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;

/**
 * @author LLT
 * 
 */
public class AbstractGTTest extends AbstractTest {
	protected static final int NUMBER_OF_TESTCASES = 100;
	protected static final int METHOD_PER_CLASS = 10;
	private static Logger log = LoggerFactory.getLogger(AbstractGTTest.class);
	protected TestConfiguration config = TestConfiguration.getInstance();
	protected String srcPath;

	@Before
	public void beforeMethod() {
		srcPath = TestConfiguration.getTestScrPath("gentest");
	}

	public void printTc(GentestBuilder<?> builder, Class<?> targetClazz) throws SavException {
		TestsPrinter printer = new TestsPrinter(
				getTestPkg(targetClazz), null, "test",
				targetClazz.getSimpleName(), srcPath,
				PrintOption.APPEND);
		printer.setMethodsPerClass(METHOD_PER_CLASS);
		Pair<List<Sequence>, List<Sequence>> testcases = builder.generate();
		List<Sequence> allTcs = new ArrayList<Sequence>(testcases.a);
		allTcs.addAll(testcases.b);
		for (Sequence seq : allTcs) {
			for (Statement stmt : seq.getStmts()) {
				if (!CollectionUtils.existIn(stmt.getKind(),
						RStatementKind.ARRAY_ASSIGNMENT)) {
					log.debug(stmt.toString());
				}
			}
		}
		printer.printTests(testcases);
	}

	private String getTestPkg(Class<?> targetClazz) {
		return StringUtils.dotJoin("testdata.gentest", targetClazz.getSimpleName());
	}
}
