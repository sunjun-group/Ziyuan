/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import icsetlv.common.dto.BreakpointValue;
import sav.common.core.formula.Eq;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class VarSolutionUtils {

	public static List<double[]> buildSolutionsFromAssignments(List<List<Eq<?>>> assignments, List<ExecVar> originVars,
			List<BreakpointValue> testInputs) {
		List<double[]> result = new ArrayList<double[]>(assignments.size());
		int tcInputIdx = 0;
		for (List<Eq<?>> ele : assignments) {
			List<Eq<?>> assignment = new ArrayList<Eq<?>>(ele);
			
			/* select one of current test inputs to merge with sample value */
			BreakpointValue testInput = testInputs.get(tcInputIdx);
			if (tcInputIdx >= testInputs.size()) {
				tcInputIdx = 0;
			}
			/* build solution */
			double[] sol = new double[originVars.size()];
			for (int i = 0; i < originVars.size(); i++) {
				Iterator<Eq<?>> it = assignment.iterator();
				/*
				 * build up solution using generated assignment or existing test
				 * input if there is no assignemnt
				 */
				ExecVar execVar = originVars.get(i);
				double domain = 0.0;
				boolean found = false;
				while(it.hasNext()) {
					Eq<?> asgmt = it.next();
					if (execVar.equals(asgmt.getVar())) {
						double value = execVar.getDoubleValue(asgmt.getValue());
						domain = value;
						it.remove();
						found = true;
						break;
					}
				}
				if (!found) {
					/* collect from existing input value */
					domain = testInput.getValue(execVar.getLabel(), 0.0).doubleValue();
				}
				sol[i] = domain;
			}
			result.add(sol);
		}
		return result;
	}

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
}
