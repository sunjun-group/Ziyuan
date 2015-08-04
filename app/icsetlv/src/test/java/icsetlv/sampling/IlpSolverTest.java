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
import sav.commons.AbstractTest;

/**
 * @author LLT
 *
 */
public class IlpSolverTest extends AbstractTest {
	
	@Test
	public void test1() {
		Map<String, Pair<Double, Double>> minMax = new HashMap<String, Pair<Double,Double>>();
		put(minMax, "a", -10.0, 50.0);
		IlpSolver solver = new IlpSolver(minMax, false);
		// -a >= -3
		List<LIATerm> terms = new ArrayList<LIATerm>();
		terms.add(term("a", -1));
		Formula formula = new LIAAtom(terms, Operator.GE, -3);
		formula.accept(solver);
		System.out.println(solver.getResult());
	}
	
	@Test
	public void test() {
		// 0.6566677370444086*max-0.662653796182137*students[2].score >= 0.0799124143064748
		//0.5789615304618991*max-0.5701910168414223*students[2].score >= 0.22098465378289825
		// 1.002993795487015*max-1.0035381219392292*students[2].score >= -0.5402801574634204
		Map<String, Pair<Double, Double>> minMax = new HashMap<String, Pair<Double,Double>>();
		minMax.put("a", new Pair<Double, Double>(0.0, 99.0));
		minMax.put("b", new Pair<Double, Double>(0.0, 100.0));
		IlpSolver solver = new IlpSolver(minMax, true);
		List<LIATerm> terms = new ArrayList<LIATerm>();
		terms.add(term("a", 0.5789615304618991));
		terms.add(term("b", -0.5701910168414223));
		Formula formula = new LIAAtom(terms, Operator.GE, -0.22098465378289825);
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
