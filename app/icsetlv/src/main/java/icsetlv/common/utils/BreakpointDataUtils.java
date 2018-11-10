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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import icsetlv.common.dto.BreakpointData;
import icsetlv.common.dto.BreakpointValue;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.execute.value.ExecValue;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;
import sav.strategies.dto.execute.value.PrimitiveValue;

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
				if (bkpVal != null) {
					collectExecVar(bkpVal.getChildren(), allVars);
				}
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
			if (val.getType() == ExecVarType.REFERENCE) {
				vars.add(new ExecVar(val.getChildId(ExecVar.IS_NULL_CODE), ExecVarType.BOOLEAN));
			} else if (val.getType() == ExecVarType.ARRAY) {
				vars.add(new ExecVar(val.getChildId(ExecVar.IS_NULL_CODE), ExecVarType.BOOLEAN));
				vars.add(new ExecVar(val.getChildId(ExecVar.LENGTH_CODE), ExecVarType.INTEGER));
			}
			if (CollectionUtils.isEmpty(val.getChildren())) {
				String varId = val.getVarId();
				ExecVar var = new ExecVar(varId, val.getType());
				var.setValueType(val.getValueType());
				vars.add(var);
			}
			collectExecVar(val.getChildren(), vars);
		}
	}
	
	/**
	 * collect variables by only check one breakpoint value, not all.
	 * for performance, if you can make sure that all breakpointValue is parallel
	 * then call this function.
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

	public static void addToBreakpointValue(ExecValue parentVal, ExecVar execVar, Number value) {
		ExecValue child = PrimitiveValue.valueOf(execVar, value);
		parentVal.add(child);
	}
	
	public static List<double[]> toDataPoint(List<ExecVar> vars, List<BreakpointValue> values) {
		List<double[]> result = new ArrayList<double[]>(values.size());
		for (BreakpointValue value : values) {
			double[] datapoint = toDatapoint(vars, value);
			result.add(datapoint);
		}
		return result;
	}

	public static double[] toDatapoint(List<ExecVar> vars, BreakpointValue value) {
		double[] datapoint = new double[vars.size()];
		for (int i = 0; i < vars.size(); i++) {
			datapoint[i] = value.getValue(vars.get(i).getVarId(), 0.0);
		}
		return datapoint;
	}
	
	public static List<ExecVar> collectAllVarsInturn(List<BreakpointValue> bkpVals) {
		return collectAllVarsForMultiValListInturn(Arrays.asList(bkpVals));
	}
	
	public static List<ExecVar> collectAllVarsForMultiValListInturn(List<List<BreakpointValue>> bkpValsList) {
		HashMap<Integer, ExecVar> allVars = new HashMap<Integer, ExecVar>();
		for (List<BreakpointValue> bkpVals : bkpValsList) {
			for (ExecValue bkpVal : bkpVals) {
				collectExecVarInturn(bkpVal.getChildren(), allVars);
			}
		}
		ArrayList<ExecVar> list = new ArrayList<>(allVars.size());
		for (int i = 0; i < allVars.size(); i++) {
			list.add(allVars.get((Integer)i));
		}
		return list;
	}
		
	public static void collectExecVarInturn(List<ExecValue> vals, HashMap<Integer, ExecVar> allVars) {
		if (CollectionUtils.isEmpty(vals)) {
			return;
		}
		for (ExecValue val : vals) {
			if (CollectionUtils.isEmpty(val.getChildren())) {
				String varId = val.getVarId();
				int size = allVars.size();
				allVars.put(size, new ExecVar(varId, val.getType()));
			}
			collectExecVarInturn(val.getChildren(), allVars);
		}
	}
}
