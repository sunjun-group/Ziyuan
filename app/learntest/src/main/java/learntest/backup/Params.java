/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.backup;

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
import learntest.plugin.LearnTestConfig;
import learntest.plugin.utils.LearnTestUtil;
import sav.common.core.ISystemVariable;
import sav.common.core.ModuleEnum;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.SystemPreferences;

/**
 * @author LLT
 *
 */
public class Params {
	private LearnTestApproach approach;
	private TargetMethod targetMethod;
	private JunitTestsInfo initialTests;
	private SystemPreferences systemConfig;
	private int maxTcs;
	
	public Params() {
		systemConfig = new SystemPreferences();
	}
	
	public Params(TargetMethod targetMethod) {
		this();
		this.targetMethod = targetMethod;
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
//		LearnTestConfig.isL2TApproach = (approach == LearnTestApproach.L2T); //  TODO: TO REMOVE
		this.approach = approach;
	}

	@Deprecated
	public String getTestClass() {
		return null;
	}
	
	public int getMaxTcs() {
		return maxTcs;
	}

	public void setMaxTcs(int maxTcs) {
		this.maxTcs = maxTcs;
	}

	public String getTestPackage(GenTestPackage phase) {
		return null;
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
	/**
	 * @return
	 */
	public String getFilePath() {
		// TODO Auto-generated method stub
		return null;
	}

}
