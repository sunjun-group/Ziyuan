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

import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

/**
 * @author LLT
 *
 */
public class LearningUtils {
	private LearningUtils() {
	}
	
	public static List<ExecVar> createPolyClassifierVars(List<ExecVar> orgVars) {
		List<ExecVar> polyClassifierVars = new ArrayList<ExecVar>(orgVars);
		int size = orgVars.size();
		for (int i = 0; i < size; i++) {
			ExecVar var = orgVars.get(i);
			for (int j = i; j < size; j++) {
				polyClassifierVars
						.add(new ExecVar(var.getLabel() + " * " + orgVars.get(j).getLabel(), ExecVarType.INTEGER));
			}
		}
		return polyClassifierVars;
	}
}
