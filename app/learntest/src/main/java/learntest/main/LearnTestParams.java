/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.main;

import java.util.Arrays;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import learntest.core.commons.data.testtarget.TargetClass;
import learntest.core.commons.data.testtarget.TargetMethod;
import learntest.util.LearnTestUtil;
import sav.common.core.ModuleEnum;
import sav.common.core.SavException;

/**
 * @author LLT
 *
 */
public class LearnTestParams {
	private boolean learnByPrecond;
	private String filePath;
	private String testClass;
	private TargetMethod targetMethod;
	
	private boolean randomDecision;
	
	public static LearnTestParams initFromLearnTestConfig() throws SavException {
		LearnTestParams params = new LearnTestParams();
		params.filePath = LearnTestConfig.getTestClassFilePath();
		params.testClass = LearnTestConfig.getTestClass(LearnTestConfig.isL2TApproach);
		params.learnByPrecond = LearnTestConfig.isL2TApproach;
		try {
			initTargetMethod(params);
		} catch (JavaModelException e) {
			throw new SavException(ModuleEnum.UNSPECIFIED, e, e.getMessage());
		}
		return params;
	}

	private static void initTargetMethod(LearnTestParams params) throws SavException, JavaModelException {
		TargetClass targetClass = new TargetClass(LearnTestConfig.targetClassName);
		TargetMethod method = new TargetMethod(targetClass);
		method.setMethodName(LearnTestConfig.targetMethodName);
		method.setLineNum( LearnTestConfig.getMethodLineNumber());
		IMethod imethod = LearnTestUtil.findMethod(method.getClassName(), method.getMethodName(), method.getLineNum());
		method.setMethodSignature(LearnTestUtil.getMethodSignature(imethod));
		String[] parameterNames = imethod.getParameterNames();
		if (parameterNames != null && parameterNames.length > 0) {
			method.setParams(Arrays.asList(parameterNames));
			method.setParamTypes(Arrays.asList(imethod.getParameterTypes()));
		}
		params.targetMethod = method;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getTestClass() {
		return testClass;
	}

	public void setTestClass(String testClass) {
		this.testClass = testClass;
	}

	public boolean isRandomDecision() {
		return randomDecision;
	}

	public void setRandomDecision(boolean randomDecision) {
		this.randomDecision = randomDecision;
	}
	
	public TargetMethod getTargetMethod() {
		return targetMethod;
	}

	public void setTargetMethod(TargetMethod targetMethod) {
		this.targetMethod = targetMethod;
	}
	
	public boolean isLearnByPrecond() {
		return learnByPrecond;
	}
}
