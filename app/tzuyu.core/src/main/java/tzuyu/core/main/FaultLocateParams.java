/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main;

import icsetlv.variable.VariableNameCollector.VarNameCollectionMode;

import java.util.List;

import sav.common.core.utils.Assert;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 * 
 */
public class FaultLocateParams {
	private List<String> testingClassNames;
	private List<String> testingPkgs;
	private String methodName;
	private String verificationMethod;
	private List<String> junitClassNames;
	private boolean slicerEnable;
	private boolean genTestEnable;
	private int numberOfTestCases = 100;
	private int rankToExamine = Integer.MAX_VALUE;
	private boolean mutationEnable;
	private boolean machineLearningEnable;
	private int valueRetrieveLevel;
	private VarNameCollectionMode varNameCollectionMode = VarNameCollectionMode.FULL_NAME;

	public List<String> getTestingClassNames() {
		return testingClassNames;
	}

	public void setTestingClassNames(List<String> testingClassNames) {
		this.testingClassNames = testingClassNames;
	}

	public List<String> getTestingPkgs() {
		return testingPkgs;
	}

	public void setTestingPkgs(List<String> testingPkgs) {
		this.testingPkgs = testingPkgs;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getVerificationMethod() {
		return verificationMethod;
	}

	public void setVerificationMethod(String verificationMethod) {
		this.verificationMethod = verificationMethod;
	}

	public List<String> getJunitClassNames() {
		return junitClassNames;
	}

	public void setJunitClassNames(List<String> junitClassNames) {
		this.junitClassNames = junitClassNames;
	}

	public boolean isSlicerEnable() {
		return slicerEnable;
	}

	public void setUseSlicer(boolean useSlicer) {
		this.slicerEnable = useSlicer;
	}

	public boolean isGenTestEnable() {
		return genTestEnable && CollectionUtils.checkSize(testingClassNames, 1)
				&& methodName != null && verificationMethod != null;
	}

	public String getTestingClassName() {
		Assert.assertTrue(CollectionUtils.checkSize(testingClassNames, 1), "expect only 1 testing class");
		return testingClassNames.get(0);
	}
	
	public void setGenTest(boolean genTest) {
		this.genTestEnable = genTest;
	}

	public int getNumberOfTestCases() {
		return numberOfTestCases;
	}

	public void setNumberOfTestCases(int numberOfTestCases) {
		this.numberOfTestCases = numberOfTestCases;
	}

	public int getRankToExamine() {
		return rankToExamine;
	}
	
	public void setRankToExamine(int rankToExamine) {
		this.rankToExamine = rankToExamine;
	}

	public boolean isMutationEnable() {
		return mutationEnable;
	}

	public void setRunMutation(boolean runMutation) {
		this.mutationEnable = runMutation;
	}

	public boolean isMachineLearningEnable() {
		return machineLearningEnable;
	}

	public void setMachineLearningEnable(boolean machineLearningEnable) {
		this.machineLearningEnable = machineLearningEnable;
	}

	public int getValueRetrieveLevel() {
		return valueRetrieveLevel;
	}

	public void setValueRetrieveLevel(int valueRetrieveLevel) {
		this.valueRetrieveLevel = valueRetrieveLevel;
	}

	public VarNameCollectionMode getVarNameCollectionMode() {
		return varNameCollectionMode;
	}

	public void setVarNameCollectionMode(VarNameCollectionMode varNameCollectionMode) {
		this.varNameCollectionMode = varNameCollectionMode;
	}
	
}
