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

import sav.common.core.Constants;
import sav.common.core.utils.CollectionUtils;
import sav.commons.TestConfiguration;
import faultLocalization.SpectrumBasedSuspiciousnessCalculator.SpectrumAlgorithm;

/**
 * @author LLT
 * 
 */
public abstract class TzuyuCoreTest extends AbstractTzTest {
	protected TzuyuCore app;
	
	@Before
	public void setup() throws Exception {
		testContext.getAppData().addClasspath(TestConfiguration.getTarget("slicer.javaslicer"));
		appData.setSuspiciousCalculAlgo(SpectrumAlgorithm.OCHIAI);
		app = new TzuyuCore(testContext, appData);
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
