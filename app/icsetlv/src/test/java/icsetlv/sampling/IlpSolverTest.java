/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.sampling;

import icsetlv.common.dto.ExecVar;

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

/**
 * @author LLT
 *
 */
public class IlpSolverTest {
	
	@Test
	public void test1() {
		Map<String, Pair<Double, Double>> minMax = new HashMap<String, Pair<Double,Double>>();
		put(minMax, "a", -10.0, 50.0);
		IlpSolver solver = new IlpSolver(minMax);
		List<LIATerm> terms = new ArrayList<LIATerm>();
		terms.add(term("a", -1));
		Formula formula = new LIAAtom(terms, Operator.GE, -3);
		formula.accept(solver);
		System.out.println(solver.getResult());
	}
	
	@Test
	public void test() {
		Map<String, Pair<Double, Double>> minMax = new HashMap<String, Pair<Double,Double>>();
		put(minMax, "a", -10.0, 50.0);
		put(minMax, "b", -50.0, 100.0);
		IlpSolver solver = new IlpSolver(minMax);
		List<LIATerm> terms = new ArrayList<LIATerm>();
		terms.add(term("a", 100));
		terms.add(term("b", -10));
		Formula formula = new LIAAtom(terms, Operator.GE, 10);
		formula.accept(solver);
		System.out.println(solver.getResult());
	}

	private void put(Map<String, Pair<Double, Double>> minMax, String var,
			double min, double max) {
		minMax.put(var, new Pair<Double, Double>(min, max));		
	}

	private LIATerm term(String var, double val) {
		return new LIATerm(new ExecVar(var), val);
	}
}
