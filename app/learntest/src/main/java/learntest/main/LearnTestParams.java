/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.main;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import learntest.core.commons.data.testtarget.TargetClass;
import learntest.core.commons.data.testtarget.TargetMethod;
import learntest.core.gentest.GentestParams;
import learntest.util.LearnTestUtil;
import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.SignatureUtils;
import sav.common.core.utils.StringUtils;
import sav.strategies.dto.AppJavaClassPath;

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
	
	public GentestParams initGentestParams(AppJavaClassPath appClassPath) {
		GentestParams params = new GentestParams();
		params.setMethodSignature(SignatureUtils.createMethodNameSign(targetMethod.getMethodName(), 
				targetMethod.getMethodSignature()));
		params.setTargetClassName(targetMethod.getClassName());
		params.setNumberOfTcs(1);
		params.setTestPerQuery(1);
		params.setTestSrcFolder(appClassPath.getTestSrc());
		String approachPrefix = learnByPrecond ? "l2t" : "ram";
		params.setTestPkg(StringUtils.dotJoin("testdata", approachPrefix, "test.init",
				targetMethod.getTargetClazz().getClassSimpleName().toLowerCase(),
				targetMethod.getMethodName().toLowerCase()));
		params.setTestClassPrefix(targetMethod.getTargetClazz().getClassSimpleName());
		params.setTestMethodPrefix("test");
		return params;
	}

	private static void initTargetMethod(LearnTestParams params) throws SavException, JavaModelException {
		TargetClass targetClass = new TargetClass(LearnTestConfig.targetClassName);
		TargetMethod method = new TargetMethod(targetClass);
		method.setMethodName(LearnTestConfig.targetMethodName);
		method.setLineNum( LearnTestConfig.getMethodLineNumber());
		MethodDeclaration methodDeclaration = LearnTestUtil.findSpecificMethod(method.getClassName(), method.getMethodName(), method.getLineNum());
		method.setMethodSignature(LearnTestUtil.getMethodSignature(methodDeclaration));
		List<String> paramNames = new ArrayList<String>(CollectionUtils.getSize(methodDeclaration.parameters()));
		List<String> paramTypes = new ArrayList<String>(paramNames.size());
		for(Object obj: methodDeclaration.parameters()){
			if(obj instanceof SingleVariableDeclaration){
				SingleVariableDeclaration svd = (SingleVariableDeclaration)obj;
				paramNames.add(svd.getName().getIdentifier());
				paramTypes.add(svd.getType().toString());
			}
		}
		method.setParams(paramNames);
		method.setParamTypes(paramTypes);
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
