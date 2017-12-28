/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.view.report;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import cfgcoverage.jacoco.analysis.data.DecisionBranchType;
import learntest.plugin.commons.data.IModelRuntimeInfo;
import learntest.plugin.commons.data.MethodRuntimeInfo;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class ReportLabelProvider {
	private WorkbenchLabelProvider workbenchLabelProvider = new WorkbenchLabelProvider();

	public String getJEleColumnText(Object element) {
		return workbenchLabelProvider.getText(element);
	}

	public String getBranchCoverageText(Object element) {
		if(element == ReportContentProvider.LOADING) {
			return StringUtils.EMPTY;
		}
		MethodRuntimeInfo runtimeInfo = getMethodInfo(element);
		if (runtimeInfo == null) {
			return StringUtils.EMPTY;
		}
		return (int)(runtimeInfo.getRawRuntimeInfo().getCoverage() * 100) + "%";
	}

	public String getMethodLengthText(Object element) {
		if(element == ReportContentProvider.LOADING) {
			return StringUtils.EMPTY;
		}
		MethodRuntimeInfo runtimeInfo = getMethodInfo(element);
		if (runtimeInfo == null) {
			return StringUtils.EMPTY;
		}
		return String.valueOf(runtimeInfo.getRawRuntimeInfo().getMethodInfo().getMethodLength());
	}
	
	private MethodRuntimeInfo getMethodInfo(Object element) {
		IJavaElement jEle = (IJavaElement)element;
		if (jEle.getElementType() != IJavaElement.METHOD) {
			return null;
		}
		MethodRuntimeInfo runtimeInfo = (MethodRuntimeInfo)jEle.getAdapter(IModelRuntimeInfo.class);
		if (runtimeInfo.getJavaElement() == element) {
			return runtimeInfo;
		}
		return null;
	}

	public String getUncoveredBranchesText(Object element) {
		if(element == ReportContentProvider.LOADING) {
			return StringUtils.EMPTY;
		}
		MethodRuntimeInfo runtimeInfo = getMethodInfo(element);
		if (runtimeInfo == null) {
			return StringUtils.EMPTY;
		}
		StringBuilder sb = new StringBuilder();
		LinkedHashMap<String, Set<DecisionBranchType>> branchCoveredMap = runtimeInfo.getRawRuntimeInfo().getCoveredBranchMap();
		List<DecisionBranchType> missingBranchs = new ArrayList<DecisionBranchType>(2);
		for (String node : branchCoveredMap.keySet()) {
			missingBranchs.clear();
			Set<DecisionBranchType> coveredType = branchCoveredMap.get(node);
			if (!coveredType.contains(DecisionBranchType.TRUE)) {
				missingBranchs.add(DecisionBranchType.TRUE);
			}
			if (!coveredType.contains(DecisionBranchType.FALSE)) {
				missingBranchs.add(DecisionBranchType.FALSE);
			}
			if (!missingBranchs.isEmpty()) {
				sb.append(node).append(": ").append(missingBranchs).append("\n");
			}
		}
		return sb.toString();
	}
}
