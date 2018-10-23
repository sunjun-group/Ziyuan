/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jacop.core.Domain;
import org.jacop.floats.core.FloatDomain;
import org.jacop.floats.core.FloatIntervalDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.utils.BreakpointDataUtils;
import learntest.core.commons.data.SolutionBreakpointValue;
import sav.common.core.Constants;
import sav.common.core.formula.Eq;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class DomainUtils {
	private static Logger log = LoggerFactory.getLogger(DomainUtils.class);
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
		Set<String> existingPatterns = new HashSet<String>();
		int duplicate = 0;
		for (List<Eq<?>> ele : assignments) {
			List<Eq<?>> assignment = new ArrayList<Eq<?>>(ele);
			
			/* select one of current test inputs to merge with sample value */
			BreakpointValue testInput = testInputs.get(tcInputIdx);
			if (tcInputIdx >= testInputs.size()) {
				tcInputIdx = 0;
			}
			StringBuilder sb = new StringBuilder();
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
				sb.append(getDomainValue(domain)).append(Constants.LOW_LINE);
			}
			String valuesPattern = sb.toString();
			if (!existingPatterns.contains(valuesPattern)) {
				result.add(sol);
				existingPatterns.add(valuesPattern);
			} else {
				duplicate++;
			}
		}
		if (duplicate > 0) {
			log.debug(String.format("remove %d duplicated solutions: ", duplicate));
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

	public static BreakpointValue toBreakpointValue(double[] solution, List<ExecVar> vars, int solutionIdx) {
		BreakpointValue value = new SolutionBreakpointValue(solutionIdx);
		for (int i = 0; i < vars.size(); i++) {
			BreakpointDataUtils.addToBreakpointValue(value, vars.get(i), solution[i]);
		}
		return value;
	}
	
	public static BreakpointValue toHierachyBreakpointValue(double[] solution, List<ExecVar> vars) {
		BreakpointValue value = new BreakpointValue();
		for (int i = 0; i < vars.size(); i++) {
			value.append(vars.get(i).getVarId(), 0, vars.get(i), solution[i]);
		}
		return value;
	}

	public static Domain toDomain(int val) {
		return new FloatIntervalDomain(val, val);
	}
	public static List<Integer> getCorrespondingSolutionIdx(List<double[]> allDatapoints,
			List<BreakpointValue> vals) {
		List<Integer> result = new ArrayList<Integer>(CollectionUtils.getSize(vals));
		for (BreakpointValue val : CollectionUtils.nullToEmpty(vals)) {
			if (val instanceof SolutionBreakpointValue) {
				SolutionBreakpointValue sBkVal = (SolutionBreakpointValue) val;
				result.add(sBkVal.getSolutionIdx());
			} else {
				log.warn("Breakpoint value is not SolutionBreakpointValue type!");
			}
		}
		return result;
	}

	public static List<double[]> getCorrespondingSolution(List<double[]> allDatapoints,
			List<BreakpointValue> vals) {
		List<double[]> result = new ArrayList<double[]>(CollectionUtils.getSize(vals));
		for (BreakpointValue val : CollectionUtils.nullToEmpty(vals)) {
			if (val instanceof SolutionBreakpointValue) {
				SolutionBreakpointValue sBkVal = (SolutionBreakpointValue) val;
				result.add(allDatapoints.get(sBkVal.getSolutionIdx()));
			} else {
				log.warn("Breakpoint value is not SolutionBreakpointValue type!");
			}
		}
		return result;
	}

}
