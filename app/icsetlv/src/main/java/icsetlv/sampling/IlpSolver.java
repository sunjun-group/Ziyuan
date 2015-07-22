/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.sampling;

import icsetlv.common.dto.ExecVar;

import java.util.List;
import java.util.Map;

import net.sf.javailp.Linear;
import net.sf.javailp.Problem;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryLpSolve;
import sav.common.core.Pair;
import sav.common.core.formula.Eq;
import sav.common.core.formula.LIAAtom;
import sav.common.core.formula.LIATerm;
import sav.common.core.formula.utils.ExpressionVisitor;


/**
 * @author LLT
 *
 */
public class IlpSolver extends ExpressionVisitor {
	private SolverFactory solverFactory;
	private Map<String, Pair<Double, Double>> minMax;
	private List<Eq<?>> assignments;
	
	public IlpSolver(Map<String, Pair<Double, Double>> minMax) {
		initSolverFactory();
		this.minMax = minMax;
	}

	@Override
	public void visit(LIAAtom atom) {
		Problem problem = new Problem();
		Linear obj = new Linear();
		Linear divider = new Linear();
		for (LIATerm varExp : atom.getMVFOExpr()) {
			ExecVar var = varExp.getVariable();
			divider.add(varExp.getCoefficient(), var.getVarId());
		}
		problem.add(divider, atom.getOperator().getCode(), atom.getConstant());
		
	}
	
	public List<Eq<?>> getAssignments() {
		return assignments;
	}
	
	private Solver getSolver() {
		return solverFactory.get();
	}

	private void initSolverFactory() {
		solverFactory = new SolverFactoryLpSolve();
		solverFactory.setParameter(Solver.VERBOSE, 0);
		solverFactory.setParameter(Solver.TIMEOUT, 100);
	}
	
}
