/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies.vm;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import sav.common.core.SavException;
import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;

/**
 * @author LLT
 *
 */
public class JavaCompilerTest extends AbstractTest {

	public void compile(String... classNames) {
		List<File> javaFiles = new ArrayList<File>();
		for (String className : classNames) {
			String classPath = "/" + className.replace(".", "/") + ".java";
			javaFiles.add(new File(TestConfiguration.getTestScrPath(SAV_COMMONS) + 
					classPath));
		}
		VMConfiguration vmConfig = initVmConfig();
		vmConfig.setJavaHome("/Library/Java/JavaVirtualMachines/jdk1.8.0_112.jdk/Contents/Home");
		JavaCompiler compiler = new JavaCompiler(vmConfig);
		try {
//			String targetFolder = TestConfiguration.getTestTarget(SAV_COMMONS);
			String targetFolder = "/Users/lylytran/Projects/temp/compiledTarget";
			boolean success = compiler.compile(targetFolder, javaFiles);
			System.out.println(success ? "compile successful" : "compile fail");
		} catch (SavException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testWarning() {
		compile(RawUncheckTestData.class.getName(), TargetClass.class.getName());
	}
	
	@Test
	public void testCompilationErrorBak() {
		VMConfiguration vmConfig = initVmConfig();
		vmConfig.setJavaHome("/Library/Java/JavaVirtualMachines/jdk1.8.0_112.jdk/Contents/Home");
		vmConfig.setClasspath(Arrays.asList("/Applications/Eclipse.app/Contents/Eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar",
				"/Applications/Eclipse.app/Contents/Eclipse/plugins/org.junit_4.12.0.v201504281640/junit.jar",
				"/Users/lylytran/apache-common-math-2.2/apache-common-math-2.2/bin"));
		JavaCompiler compiler = new JavaCompiler(vmConfig);
		File javaFiles = new File("/Users/lylytran/apache-common-math-2.2/apache-common-math-2.2/src/test/java/compilationErrorBak/SimpsonIntegrator3.java");
		try {
			boolean success = compiler.compile("/Users/lylytran/Projects/TEST RESULTS/bugs/bin/", javaFiles);
			System.out.println(success ? "compile successful" : "compile fail");
		} catch (SavException e) {
			e.printStackTrace();
		}
	}
}
