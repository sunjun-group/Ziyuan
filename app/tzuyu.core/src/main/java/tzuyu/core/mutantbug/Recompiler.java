/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.mutantbug;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sav.common.core.SavException;
import sav.common.core.utils.CollectionBuilder;
import sav.strategies.vm.VMConfiguration;
import sav.strategies.vm.VMRunner;
import sav.strategies.vm.VmRunnerUtils;

/**
 * @author LLT
 * 
 */
public class Recompiler {
	private VMConfiguration vmConfig;

	public Recompiler(VMConfiguration vmConfig) {
		setVmConfig(vmConfig);
	}

	public void recompileJFile(String targetFolder, File... mutatedFiles)
			throws SavException {
		recompileJFile(targetFolder, Arrays.asList(mutatedFiles));
	}

	public void recompileJFile(String targetFolder, List<File> mutatedFiles)
			throws SavException {
		CollectionBuilder<String, List<String>> builder = new CollectionBuilder<String, List<String>>(
				new ArrayList<String>())
				.add(VmRunnerUtils.buildJavaCPrefix(vmConfig))
				.add("-classpath").add(vmConfig.getClasspathStr()).add("-d")
				.add(targetFolder);
		for (File mutatedFile : mutatedFiles) {
			builder.add(mutatedFile.getAbsolutePath());
		}
		builder.add("-g");
		VMRunner.startAndWaitUntilStop(builder.toCollection());
	}

	public void setVmConfig(VMConfiguration vmConfig) {
		this.vmConfig = vmConfig;
	}

}
