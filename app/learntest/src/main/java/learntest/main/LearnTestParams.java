/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import learntest.core.LearntestParamsUtils;
import learntest.core.LearntestParamsUtils.GenTestPackage;
import learntest.core.commons.data.LearnTestApproach;
import learntest.core.commons.data.classinfo.JunitTestsInfo;
import learntest.core.commons.data.classinfo.TargetClass;
import learntest.core.commons.data.classinfo.TargetMethod;
import learntest.util.LearnTestUtil;
import sav.common.core.ISystemVariable;
import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.SystemPreferences;

/**
 * @author LLT
 *
 */
public class LearnTestParams {
	private LearnTestApproach approach;
	private TargetMethod targetMethod;
	private JunitTestsInfo initialTests;
	private SystemPreferences systemConfig;
	private int maxTcs;
	
	public LearnTestParams() {
		systemConfig = new SystemPreferences();
	}
	
	public LearnTestParams(TargetMethod targetMethod) {
		this();
		this.targetMethod = targetMethod;
	}

	@Deprecated
	public static LearnTestParams initFromLearnTestConfig() throws SavException {
		LearnTestParams params = new LearnTestParams();
		params.setApproach(LearnTestConfig.isL2TApproach ? LearnTestApproach.L2T : LearnTestApproach.RANDOOP);
		try {
			initTargetMethod(params);
		} catch (JavaModelException e) {
			throw new SavException(e, ModuleEnum.UNSPECIFIED, e.getMessage());
		}
		return params;
	}
	
	@Deprecated
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

	public TargetMethod getTargetMethod() {
		return targetMethod;
	}

	public void setTargetMethod(TargetMethod targetMethod) {
		this.targetMethod = targetMethod;
	}
	
	public boolean isLearnByPrecond() {
		return approach == LearnTestApproach.L2T;
	}
	
	public LearnTestApproach getApproach() {
		return approach;
	}

	@SuppressWarnings("unchecked")
	public List<String> getInitialTestcases() {
		if (initialTests == null) {
			return Collections.EMPTY_LIST;
		}
		return initialTests.getJunitTestcases();
	}
	
	public void setInitialTests(JunitTestsInfo initialTests) {
		this.initialTests = initialTests;
	}
	
	public JunitTestsInfo getInitialTests() {
		if (initialTests == null) {
			initialTests = new JunitTestsInfo();
		}
		return initialTests;
	}

	public void setApproach(LearnTestApproach approach) {
		LearnTestConfig.isL2TApproach = (approach == LearnTestApproach.L2T); //  TODO: TO REMOVE
		this.approach = approach;
	}

	@Deprecated
	public String getFilePath() {
		return LearnTestConfig.getTestClassFilePath();
	}

	@Deprecated
	public String getTestClass() {
		return LearnTestConfig.getTestClass(LearnTestConfig.isL2TApproach);
	}
	
	public int getMaxTcs() {
		return maxTcs;
	}

	public void setMaxTcs(int maxTcs) {
		this.maxTcs = maxTcs;
	}

	public String getTestPackage(GenTestPackage phase) {
		return LearntestParamsUtils.getTestPackage(this, phase);
	}
	
	public LearnTestParams createNew() {
		LearnTestParams params = new LearnTestParams();
		params.targetMethod = targetMethod;
		params.systemConfig = systemConfig;
		return params;
	}
	
	public SystemPreferences getSystemConfig() {
		return systemConfig;
	}
	
	public static enum LearntestSystemVariable implements ISystemVariable {
		JDART_APP_PROPRETIES,
		JDART_SITE_PROPRETIES;

		public String getName() {
			return name();
		}
	}
}
