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

import org.jacop.core.Domain;
import org.jacop.floats.core.FloatDomain;
import org.jacop.floats.core.FloatIntervalDomain;

import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.utils.BreakpointDataUtils;
import sav.common.core.formula.Eq;
import sav.common.core.utils.StringUtils;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class DomainUtils {
	private DomainUtils(){}
	
	/**
	 * @param assignments
	 * @param originVars
	 * @param testInputs
	 */
	public static List<Domain[]> buildSolutionsFromAssignments(List<List<Eq<?>>> assignments, List<ExecVar> originVars,
			List<BreakpointValue> testInputs) {
		List<Domain[]> result = new ArrayList<Domain[]>(assignments.size());
		int tcInputIdx = 0;
		
		for (List<Eq<?>> ele : assignments) {
			List<Eq<?>> assignment = new ArrayList<Eq<?>>(ele);
			
			/* select one of current test inputs to merge with sample value */
			BreakpointValue testInput = testInputs.get(tcInputIdx);
			if (tcInputIdx >= testInputs.size()) {
				tcInputIdx = 0;
			}

			/* build solution */
			Domain[] sol = new Domain[originVars.size()];
			for (int i = 0; i < originVars.size(); i++) {
				Iterator<Eq<?>> it = assignment.iterator();
				/*
				 * build up solution using generated assignment or existing test
				 * input if there is no assignemnt
				 */
				ExecVar execVar = originVars.get(i);
				Domain domain = null;
				while(it.hasNext()) {
					Eq<?> asgmt = it.next();
					if (execVar.equals(asgmt.getVar())) {
						double value = execVar.getDoubleValue(asgmt.getValue());
						domain = new FloatIntervalDomain(value, value);
						it.remove();
						break;
					}
				}
				if (domain == null) {
					/* collect from existing input value */
					domain = getDomain(testInput, execVar);
				}
				sol[i] = domain;
			}
			result.add(sol);
		}
		return result;
	}

	public static List<Domain[]> buildSolutions(List<BreakpointValue> records, List<ExecVar> originVars) {
		List<Domain[]> res = new ArrayList<Domain[]>();
		int size = originVars.size();
		for (BreakpointValue record : records) {
			Domain[] solution = new Domain[size];
			for (int i = 0; i < size; i++) {
				solution[i] = getDomain(record, originVars.get(i));
			}
			res.add(solution);
		}
		return res;
	}

	private static FloatIntervalDomain getDomain(BreakpointValue record, ExecVar execVar) {
		double value = record.getValue(execVar.getLabel(), 0.0).doubleValue();
		FloatIntervalDomain domain = new FloatIntervalDomain(value, value);
		return domain;
	}
	
	public static double getDomainValue(Domain domain) {
		return ((FloatDomain)domain).min();
	}

	public static BreakpointValue toBreakpointValue(Domain[] solution, List<ExecVar> vars) {
		BreakpointValue value = new BreakpointValue(StringUtils.EMPTY);
		for (int i = 0; i < vars.size(); i++) {
			BreakpointDataUtils.addToBreakpointValue(value, vars.get(i), getDomainValue(solution[i]));
		}
		return value;
	}

}
