/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.utils;

import java.util.ArrayList;
import java.util.List;

import icsetlv.common.dto.BreakpointValue;
import net.sf.javailp.Result;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class VarSolutionUtils {

	public static List<double[]> buildSolutions(List<BreakpointValue> records, List<ExecVar> originVars) {
		List<double[]> res = new ArrayList<double[]>();
		int size = originVars.size();
		for (BreakpointValue record : records) {
			double[] solution = new double[size];
			for (int i = 0; i < size; i++) {
				solution[i] = record.getValue(originVars.get(i).getVarId(), 0.0);
			}
			res.add(solution);
		}
		return res;
	}
	
	public static List<double[]> buildSolutionFromIlpResult(List<Result> results, List<ExecVar> originVars,
			List<double[]> testInputs) {
		List<double[]> result = new ArrayList<double[]>(results.size());
		int tcInputIdx = 0;
		for (Result ilpResult : results) {

			/* select one of current test inputs to merge with sample value */
			double[] testInput = testInputs.get(tcInputIdx);
			if (tcInputIdx >= testInputs.size()) {
				tcInputIdx = 0;
			}
			/* build solution */
			double[] sol = new double[originVars.size()];
			for (int i = 0; i < originVars.size(); i++) {
				/*
				 * build up solution using generated assignment or existing test
				 * input if there is no assignemnt
				 */
				double domain = 0.0;
				String varLabel = originVars.get(i).getLabel();
				if (ilpResult != null && ilpResult.containsVar(varLabel)) {
					domain = ilpResult.get(varLabel).doubleValue();
				} else {
					/* collect from existing input value */
					domain = testInput[i];
				}
				sol[i] = domain;
			}
			result.add(sol);
		}
		return result;
	}
}
