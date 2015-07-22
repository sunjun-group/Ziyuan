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

import net.sf.javailp.Linear;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryLpSolve;
import net.sf.javailp.Term;
import sav.common.core.Pair;
import sav.common.core.formula.Atom;
import sav.common.core.formula.ConjunctionFormula;
import sav.common.core.formula.Eq;
import sav.common.core.formula.LIAAtom;
import sav.common.core.formula.LIATerm;
import sav.common.core.formula.utils.ExpressionVisitor;
import sav.common.core.utils.CollectionUtils;


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
		assignments = new ArrayList<Eq<?>>();
	}
	
	@Override
	public void visitConjunctionFormula(ConjunctionFormula conj) {
		List<Atom> atomics = conj.getAtomics();
		List<LIAAtom> atoms = new ArrayList<LIAAtom>(atomics.size());
		solveProblem(atoms);
	}

	@Override
	public void visit(LIAAtom atom) {
		solveProblem(CollectionUtils.listOf(atom));
	}
	
	private void solveProblem(List<LIAAtom> atoms) {
		Problem problem = new Problem();
		for (LIAAtom atom : atoms) {
			constructSubjective(atom, problem);
		}
		/* set objective */
		Linear obj = getObjective(atoms);
		
		/* get result */
		solveProblem(problem, obj);
	}

	private void solveProblem(Problem problem, Linear obj) {
		List<Object> vars = obj.getVariables();
		for (Object var : vars) {
			problem.setVarType(var, Integer.class);
		}
		problem.setObjective(obj, OptType.MIN);
		Result result = getSolver().solve(problem);
		updateResult(result, vars);
	}

	private void constructSubjective(LIAAtom atom, Problem problem) {
		Linear divider = new Linear();
		for (LIATerm varExp : atom.getMVFOExpr()) {
			ExecVar var = varExp.getVariable();
			divider.add(varExp.getCoefficient(), var.getVarId());
		}
		problem.add(divider, atom.getOperator().getCode(), atom.getConstant());
	}
	
	private Linear getObjective(List<LIAAtom> atoms) {
		Linear obj = new Linear();
		Map<ExecVar, Term> terms = new HashMap<ExecVar, Term>();
		for (LIAAtom atom : atoms) {
			appendObjectiveTerms(terms, atom);
		}
		for (Term term : terms.values()) {
			obj.add(term);
		}
		return obj;
	}
	
	private void appendObjectiveTerms(Map<ExecVar, Term> terms, LIAAtom atom) {
		for (LIATerm varExp : atom.getMVFOExpr()) {
			ExecVar var = varExp.getVariable();
			if (!terms.containsKey(var)) {
				terms.put(var, new Term(var, 
										Math.signum(varExp.getCoefficient())));
			}
		}
	}
	
	private void updateResult(Result result, List<Object> vars) {
		assignments.clear();
		for (Object var : vars) {
			assignments.add(new Eq<Number>((ExecVar)var, result.get(var)));
		}
	}
	
	public List<Eq<?>> getResult() {
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
