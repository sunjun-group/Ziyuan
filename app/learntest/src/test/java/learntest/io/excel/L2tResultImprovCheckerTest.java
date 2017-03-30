/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.io.excel;

import org.junit.Test;

import learntest.io.excel.improvement.check.L2tResultImprovChecker;
import learntest.io.excel.improvement.check.L2tResultImprovChecker.ImprovementResult;

/**
 * @author LLT
 *
 */
public class L2tResultImprovCheckerTest {

	@Test
	public void testImprovCheck() throws Exception {
		ImprovementResult result = L2tResultImprovChecker.getINSTANCE().checkImprovement("apache-ant-1.9.6_0.xlsx",
				"apache-ant-1.9.6_new.xlsx");
		System.out.println(result);
	}
}
