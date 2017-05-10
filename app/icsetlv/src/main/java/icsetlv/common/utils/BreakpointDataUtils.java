/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.BreakpointValue;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class BreakpointDataUtils {
	private BreakpointDataUtils(){}
	
	/**
	 * this will cover all variables in the safe way by dumping into 
	 * every testvalue.
	 * @param bkpData
	 * @return
	 */
	public static List<ExecVar> collectAllVars(BreakpointData bkpData) {
		List<List<BreakpointValue>> bkpValsList = Arrays.asList(bkpData.getFailValues(), bkpData.getPassValues());
		return collectAllVarsForMultiValList(bkpValsList);
	}

	public static List<ExecVar> collectAllVarsForMultiValList(List<List<BreakpointValue>> bkpValsList) {
		Set<ExecVar> allVars = new HashSet<ExecVar>();
		for (List<BreakpointValue> bkpVals : bkpValsList) {
			for (ExecValue bkpVal : bkpVals) {
				collectExecVar(bkpVal.getChildren(), allVars);
			}
		}
		return new ArrayList<ExecVar>(allVars);
	}
	
	public static List<ExecVar> collectAllVars(List<BreakpointValue> bkpVals) {
		return collectAllVarsForMultiValList(Arrays.asList(bkpVals));
	}
	
	public static void collectExecVar(List<ExecValue> vals, Set<ExecVar> vars) {
		if (CollectionUtils.isEmpty(vals)) {
			return;
		}
		for (ExecValue val : vals) {
			if (CollectionUtils.isEmpty(val.getChildren())) {
				String varId = val.getVarId();
				vars.add(new ExecVar(varId, val.getType()));
			}
			collectExecVar(val.getChildren(), vars);
		}
	}
	
	/**
	 * collect variables by only check one breakpoint value, not all.
	 * for performance, if you can make sure that all breakpointValue is parallel
	 * then call this function.
	 * 
	 * @param bkpData
	 * @return
	 */
	public static List<ExecVar> collectVars(BreakpointData bkpData) {
		Set<ExecVar> allVars = new HashSet<ExecVar>();
		List<BreakpointValue> values = CollectionUtils.isNotEmpty(bkpData.getFailValues()) ? 
				bkpData.getFailValues() : bkpData.getPassValues();
		if (CollectionUtils.isNotEmpty(values)) {
			collectExecVar(values.get(0).getChildren(), allVars);
		} 
		return new ArrayList<ExecVar>(allVars);
	}
	
	public static List<ExecVar> collectVars(BreakpointValue bkpVal) {
		Set<ExecVar> allVars = new HashSet<ExecVar>();
		collectExecVar(bkpVal.getChildren(), allVars);
		return new ArrayList<ExecVar>(allVars);
	}

	/**
	 * @param bkpData
	 * @return 
	 */
	public static BreakpointValue getFirstValue(BreakpointData bkpData) {
		List<BreakpointValue> values = CollectionUtils.isNotEmpty(bkpData.getPassValues()) ? 
				bkpData.getPassValues() : bkpData.getFailValues();
		if (CollectionUtils.isNotEmpty(values)) {
			return values.get(0);
		} 
		return null;
	}

	public static List<String> extractLabels(List<ExecVar> allVars) {
		List<String> labels = new ArrayList<String>(allVars.size());
		for (ExecVar var : allVars) {
			labels.add(var.getVarId());
		}
		return labels;
	}
}
