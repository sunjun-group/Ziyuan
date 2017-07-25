/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.export.io.excel;

import java.io.File;

import org.junit.Test;

import learntest.plugin.export.io.excel.improvement.check.L2tResultImprovChecker;
import learntest.plugin.export.io.excel.improvement.check.L2tResultImprovChecker.ImprovementResult;

/**
 * @author LLT
 *
 */
public class L2tResultImprovCheckerTest {
	private static final String TEST_RESULT_FOLDER = "/Users/lylytran/Dropbox/Office share/Ziyuan/TestResult/";
	
	@Test
	public void testImprovCheck() throws Exception {
		File newFile = getFile("apache-common-math-2.2_3.xlsx");
		File oldFile = getFile("apache-common-math-2.2_2.xlsx");
		ImprovementResult result = new L2tResultImprovChecker().checkImprovement(oldFile, newFile);
		System.out.println(result);
	}
	
	private File getFile(String fileName) {
		return new File(TEST_RESULT_FOLDER + fileName);
	}
}
