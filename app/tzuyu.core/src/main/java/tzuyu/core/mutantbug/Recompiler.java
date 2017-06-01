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

import sav.common.core.ModuleEnum;
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

	public boolean recompileJFile(String targetFolder, File... mutatedFiles)
			throws SavException {
		return recompileJFile(targetFolder, Arrays.asList(mutatedFiles));
	}

	public boolean recompileJFile(String targetFolder, List<File> mutatedFiles)
			throws SavException {
		CollectionBuilder<String, List<String>> builder = new CollectionBuilder<String, List<String>>(
				new ArrayList<String>())
				.append(VmRunnerUtils.buildJavaCPrefix(vmConfig))
				.append("-classpath").append(vmConfig.getClasspathStr()).append("-d")
				.append(targetFolder);
		for (File mutatedFile : mutatedFiles) {
			builder.append(mutatedFile.getAbsolutePath());
		}
		builder.append("-g");
		VMRunner vmRunner = VMRunner.getDefault();
		vmRunner.setLog(vmConfig.isVmLogEnable());
		boolean success = vmRunner.startAndWaitUntilStop(builder.toCollection());
		if (!success) {
			throw new SavException(ModuleEnum.JVM, "Recompilation error: " + vmRunner.getProccessError());
		}
		return success;
	}

	public void setVmConfig(VMConfiguration vmConfig) {
		this.vmConfig = vmConfig;
	}

}
