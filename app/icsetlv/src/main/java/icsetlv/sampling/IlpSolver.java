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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.javailp.Linear;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryLpSolve;
import net.sf.javailp.Term;

import org.apache.commons.collections.ListUtils;

import sav.common.core.Logger;
import sav.common.core.Pair;
import sav.common.core.formula.Atom;
import sav.common.core.formula.ConjunctionFormula;
import sav.common.core.formula.Eq;
import sav.common.core.formula.LIAAtom;
import sav.common.core.formula.LIATerm;
import sav.common.core.formula.utils.ExpressionVisitor;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.Randomness;


/**
 * @author LLT
 *
 */
public class IlpSolver extends ExpressionVisitor {
	private Logger<?> log = Logger.getDefaultLogger();
	/* max number of vars need to calculate if using selective sampling */
	private static final int MAX_VAR_TO_CALCULATE = 2;
	private static final int MAX_MORE_SELECTED_SAMPLE = 4;
	private SolverFactory solverFactory;
	private Map<String, Pair<Double, Double>> minMax;
	private List<List<Eq<?>>> resultSet;
	private boolean useSampling;
	
	public IlpSolver(Map<String, Pair<Double, Double>> minMax, boolean useSampling) {
		initSolverFactory();
		this.minMax = minMax;
		resultSet = new ArrayList<List<Eq<?>>>();
		this.useSampling = useSampling;
	}
	
	public void reset() {
		resultSet.clear();
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
		log.debug("----ilp solver----");
		/* original problem */
		Problem problem = new Problem();
		constructSubjectives(atoms, problem);
		
			/* set objective */
		List<ExecVar> vars = new ArrayList<ExecVar>(constructObjective(atoms, problem));
		
			/* get result */
		Result result = solveProblem(problem, vars);
		
		/* selective sampling */
		if (minMax != null && useSampling) {
			int constraintsCount = problem.getConstraintsCount();
			int maxVarToCalcul = Math.min(MAX_VAR_TO_CALCULATE, vars.size());
			log.debug("useSampling:");
			for (int i = 1; i <= MAX_MORE_SELECTED_SAMPLE; i++) {
				List<ExecVar> selectedVars = selectVarsForSampling(vars, maxVarToCalcul);
				List<Eq<?>> samples = selectSample(selectedVars, result);
				if (samples.isEmpty()) {
					continue;
				}
				/* construct more subjective from selected samples */
				constructSubjective(samples, problem);
				
				solveProblem(problem, vars);
				
				/* reset */
				problem.getConstraints().subList(constraintsCount,
								problem.getConstraintsCount()).clear();
			}
		}
		log.debug("----finish ilp solver----");
	}

	private List<ExecVar> selectVarsForSampling(List<ExecVar> vars, int maxVarToCalcul) {
		if (vars.size() == 1) {
			return vars;
		}
		return Randomness.randomSubList(vars, vars.size() - 1);
//		int varToCalcul = Randomness.nextInt(maxVarToCalcul) + 1;
//		return Randomness.randomSubList(vars, vars.size() - i);
//		if (i >= vars.size()) {
//			return vars;
//		}
//		List<ExecVar> selectedVars = new ArrayList<ExecVar>(vars);
//		selectedVars.remove(i);
//		return selectedVars;
	}

	private List<Eq<?>> selectSample(Collection<ExecVar> vars, Result result) {
		List<Eq<?>> atoms = new ArrayList<Eq<?>>();
		for (ExecVar var : vars) {
			Number value = null;
			Pair<Double, Double> range = minMax.get(var.getLabel());
			if (range != null) {
				value = Randomness.nextInt(range.a.intValue(), range.b.intValue());
			} else {
				if (result == null) {
					continue;
				}
				value = result.get(var);
			}
			Eq<Number> eq = new Eq<Number>(var, value);
			atoms.add(eq);
		}
		return atoms;
	}

	private Result solveProblem(Problem problem, Collection<ExecVar> vars) {
		Result result = getSolver().solve(problem);
		updateResult(result, vars);
		return result;
	}

	private void constructSubjective(List<Eq<?>> atoms, Problem problem) {
		for (Eq<?> atom : atoms) {
			Linear divider = new Linear();
			divider.add(1, atom.getVar());
			problem.add(divider, "=", (Number) atom.getValue());
		}
	}
	
	private void constructSubjectives(List<LIAAtom> atoms, Problem problem) {
		for (LIAAtom atom : atoms) {
			Linear divider = new Linear();
			for (LIATerm varExp : atom.getMVFOExpr()) {
				ExecVar var = varExp.getVariable();
				divider.add(varExp.getCoefficient(), var);
			}
			problem.add(divider, atom.getOperator().getCode(),
					atom.getConstant());
		}
	}
	
	private Set<ExecVar> constructObjective(List<LIAAtom> atoms, Problem problem) {
		Linear obj = new Linear();
		Map<ExecVar, Term> terms = new HashMap<ExecVar, Term>();
		for (LIAAtom atom : atoms) {
			appendObjectiveTerms(terms, atom);
		}
		for (Term term : terms.values()) {
			obj.add(term);
		}
		
		setObjective(problem, obj);
		return terms.keySet();
	}
	
	private void appendObjectiveTerms(Map<ExecVar, Term> terms, LIAAtom atom) {
		for (LIATerm varExp : atom.getMVFOExpr()) {
			ExecVar var = varExp.getVariable();
			if (!terms.containsKey(var)) {
				terms.put(var, new Term(var, varExp.getCoefficient()));
//						Math.signum(varExp.getCoefficient())));
			}
		}
	}
	
	private List<Object> setObjective(Problem problem, Linear obj) {
		List<Object> vars = obj.getVariables();
		for (Object var : vars) {
			problem.setVarType(var, Integer.class);
		}
		problem.setObjective(obj, OptType.MIN);
		return vars;
	}
	
	private void updateResult(Result result, Collection<ExecVar> vars) {
		if (result == null) {
			return;
		}
		List<Eq<?>> assignments = new ArrayList<Eq<?>>();
		for (ExecVar var : vars) {
			Eq<?> asg = getAssignment(var, result.get(var));
			if (asg != null) {
				assignments.add(asg);
			}
		}
		
		if (!assignmentsAlreadyExist(assignments)) {
			log.debug("add result: ", assignments);
			resultSet.add(assignments);
		}
	}
	
	private Eq<?> getAssignment(ExecVar var, Number number) {
		switch (var.getType()) {
		case PRIMITIVE:
			return new Eq<Number>(var, number);
		case BOOLEAN:
			if (number.intValue() > 0) {
				return new Eq<Boolean>(var, true);
			} else {
				return new Eq<Boolean>(var, false);
			}
		default:
			return null;
		}
	}

	private boolean assignmentsAlreadyExist(Collection<Eq<?>> assignments) {
		for (List<Eq<?>> curAss : resultSet) {
			if (ListUtils.isEqualList(curAss, assignments)) {
				return true;
			}
		}
		return false;
	}
	
	public List<List<Eq<?>>> getResult() {
		return resultSet;
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
