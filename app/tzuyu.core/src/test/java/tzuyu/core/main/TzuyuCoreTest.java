/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import java.util.List;

import org.junit.Before;

import faultLocalization.SpectrumBasedSuspiciousnessCalculator.SpectrumAlgorithm;

import sav.common.core.SystemVariables;
import sav.common.core.SystemVariablesUtils;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.SystemPreferences;

/**
 * @author LLT
 * 
 */
public abstract class TzuyuCoreTest extends AbstractTzTest {
	protected TzuyuCore app;
	
	@Before
	public void setup() throws Exception {
		String jarPath = SystemVariablesUtils.updateSavJunitJarPath(appData);
		testContext.getAppData().addClasspath(jarPath);
		app = new TzuyuCore(testContext);
	}
	
	@Override
	protected void loadPreferences(SystemPreferences preferences) {
		preferences.set(SystemVariables.FAULT_LOCATE_SPECTRUM_ALGORITHM, 
				SpectrumAlgorithm.OCHIAI.name());
	}
	
	protected FaultLocateParams initFaultLocateParams(String testingClassName, String methodName, String verificationMethod,
			List<String> testingPackages, List<String> junitClassNames, boolean useSlicer) {
		FaultLocateParams params = new FaultLocateParams();
		params.setTestingClassNames(CollectionUtils.listOf(testingClassName));
		params.setMethodName(methodName);
		params.setVerificationMethod(verificationMethod);
		params.setTestingPkgs(testingPackages);
		params.setJunitClassNames(junitClassNames);
		params.setUseSlicer(useSlicer);
		params.setGenTest(true);
		params.setRunMutation(true);
		params.setMachineLearningEnable(true);
		params.setValueRetrieveLevel(3);
		return params;
	}
}
