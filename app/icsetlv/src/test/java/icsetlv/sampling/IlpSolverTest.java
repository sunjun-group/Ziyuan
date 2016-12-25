/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.sampling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import sav.common.core.Pair;
import sav.common.core.formula.Formula;
import sav.common.core.formula.LIAAtom;
import sav.common.core.formula.LIATerm;
import sav.common.core.formula.Operator;
import sav.commons.AbstractTest;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

/**
 * @author LLT
 *
 */
public class IlpSolverTest extends AbstractTest {
	
	@Test
	public void test1() {
		// 9.408258431702611E-10*fraction.numerator >= -0.9999999431761365
		Map<String, Pair<Double, Double>> minMax = new HashMap<String, Pair<Double,Double>>();
		put(minMax, "a", -10.0, 50.0);
		IlpSolver solver = new IlpSolver(minMax, false);
		// -a >= -3
		List<LIATerm> terms = new ArrayList<LIATerm>();
		terms.add(term("a", 9.408258431702611E-10));
		Formula formula = new LIAAtom(terms, Operator.GE, -0.9999999431761365);
		formula.accept(solver);
		System.out.println(solver.getResult());
	}
	
	@Test
	public void test() {
		//  0.6753333896568137*max-0.6749565756137592*students[2].score >= -0.6484853890895289
		Map<String, Pair<Double, Double>> minMax = new HashMap<String, Pair<Double,Double>>();
		minMax.put("a", new Pair<Double, Double>(0.0, 99.0));
		minMax.put("b", new Pair<Double, Double>(0.0, 100.0));
		IlpSolver solver = new IlpSolver(minMax, true);
		List<LIATerm> terms = new ArrayList<LIATerm>();
		terms.add(term("a", 0.6753333896568137));
		terms.add(term("b", -0.6749565756137592));
		Formula formula = new LIAAtom(terms, Operator.GE, -0.6484853890895289);
		formula.accept(solver);
		System.out.println(solver.getResult());
	}

	private void put(Map<String, Pair<Double, Double>> minMax, String var,
			double min, double max) {
		minMax.put(var, new Pair<Double, Double>(min, max));		
	}

	private LIATerm term(String var, double val) {
		ExecVar var2 = new ExecVar(var, ExecVarType.PRIMITIVE);
		return new LIATerm(var2, val);
	}
}
