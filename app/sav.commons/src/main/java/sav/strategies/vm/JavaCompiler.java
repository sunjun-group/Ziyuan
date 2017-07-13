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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.SavException;
import sav.common.core.SavExceptionType;
import sav.common.core.utils.CollectionBuilder;

/**
 * @author LLT
 * 
 */
public class JavaCompiler {
	private Logger log = LoggerFactory.getLogger(JavaCompiler.class);
	private VMConfiguration vmConfig;

	public JavaCompiler(VMConfiguration vmConfig) {
		setVmConfig(vmConfig);
	}

	public boolean compile(String targetFolder, File... javaFiles)
			throws SavException {
		return compile(targetFolder, Arrays.asList(javaFiles));
	}

	public boolean compile(String targetFolder, List<File> javaFiles)
			throws SavException {
		CollectionBuilder<String, List<String>> builder = new CollectionBuilder<String, List<String>>(
				new ArrayList<String>())
				.append(VmRunnerUtils.buildJavaCPrefix(vmConfig))
				.append("-classpath").append(vmConfig.getClasspathStr()).append("-d")
				.append(targetFolder);
		for (File mutatedFile : javaFiles) {
			builder.append(mutatedFile.getAbsolutePath());
		}
		builder.append("-g")
			.append("-nowarn");
		VMRunner vmRunner = VMRunner.getDefault();
		vmRunner.setLog(vmConfig.isVmLogEnable());
		boolean success = vmRunner.startAndWaitUntilStop(builder.toCollection());
		if (!success ) {
			String errorMsg = vmRunner.getProccessError();
			if (errorMsg.startsWith("Note: ")) {
				log.warn(errorMsg);
				return success;
			} else {
				throw new SavException("compilation error: " + errorMsg, SavExceptionType.COMPILATION_ERROR);
			}
		}
		return success;
	}

	public void setVmConfig(VMConfiguration vmConfig) {
		this.vmConfig = vmConfig;
	}

}
