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
import java.util.List;

import sav.common.core.SavException;
import sav.common.core.utils.CollectionBuilder;
import sav.strategies.vm.VMConfiguration;
import sav.strategies.vm.VMRunner;
import sav.strategies.vm.VmRunnerUtils;
import tzuyu.core.inject.ApplicationData;

import com.google.inject.Inject;

/**
 * @author LLT
 *
 */
public class Recompiler {
	@Inject
	private ApplicationData appData;
	private VMConfiguration vmConfig;
	
	public Recompiler(ApplicationData appData) {
		setAppData(appData);
	}
	
	public void recompileJFile(File mutatedFile) throws SavException {
		CollectionBuilder<String, List<String>> builder 
				= new CollectionBuilder<String, List<String>>(new ArrayList<String>())
					.add(VmRunnerUtils.buildJavaCPrefix(vmConfig))
					.add("-classpath")
					.add(vmConfig.getClasspathStr())
					.add("-d")
					.add(appData.getAppTarget())
					.add(mutatedFile.getAbsolutePath());
		VMRunner.startAndWaitUntilStop(builder.toCollection());
	}
	
	public void setAppData(ApplicationData appData) {
		this.appData = appData;
		this.vmConfig = appData.getVmConfig();
	}
	
}
