/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.vm;

import java.io.File;

import org.junit.Test;

import sav.common.core.SavException;
import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;

/**
 * @author LLT
 *
 */
public class JavaCompilerTest extends AbstractTest {

	@Test
	public void test() {
		VMConfiguration vmConfig = initVmConfig();
		JavaCompiler compiler = new JavaCompiler(vmConfig);
		File javaFiles = new File(TestConfiguration.getTestScrPath(SAV_COMMONS) + 
				"/sav/strategies/vm/TargetClass.java");
		try {
			boolean success = compiler.compile(TestConfiguration.getTestTarget(SAV_COMMONS), javaFiles);
			System.out.println(success ? "compile successful" : "compile fail");
		} catch (SavException e) {
			e.printStackTrace();
		}
	}
}
