/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.activelearning.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.utils.BreakpointDataUtils;
import learntest.activelearning.core.data.SolutionBreakpointValue;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class DomainUtils {
	private static Logger log = LoggerFactory.getLogger(DomainUtils.class);
	private DomainUtils(){}

	public static BreakpointValue toBreakpointValue(double[] solution, List<ExecVar> vars, int solutionIdx) {
		BreakpointValue value = new SolutionBreakpointValue(solutionIdx);
		for (int i = 0; i < vars.size(); i++) {
			BreakpointDataUtils.addToBreakpointValue(value, vars.get(i), solution[i]);
		}
		return value;
	}
	
	public static List<BreakpointValue> toHierachyBreakpointValue(List<double[]>  solutions, List<ExecVar> vars) {
		List<BreakpointValue> values = new ArrayList<>(solutions.size());
		for (double[] solution : solutions) {
			values.add(toHierachyBreakpointValue(solution, vars));
		}
		return values;
	}
	
	public static BreakpointValue toHierachyBreakpointValue(double[] solution, List<ExecVar> vars) {
		BreakpointValue value = new BreakpointValue();
		for (int i = 0; i < vars.size(); i++) {
			value.append(vars.get(i).getVarId(), 0, vars.get(i), solution[i]);
		}
		return value;
	}

}
