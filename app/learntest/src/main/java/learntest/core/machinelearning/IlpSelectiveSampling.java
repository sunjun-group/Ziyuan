/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.machinelearning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.calculator.OrCategoryCalculator;
import learntest.core.commons.data.decision.IDecisionNode;
import learntest.core.commons.utils.VarSolutionUtils;
import learntest.sampling.javailp.ProblemBuilder;
import learntest.sampling.javailp.ProblemSolver;
import learntest.util.Settings;
import libsvm.core.Divider;
import libsvm.core.Machine.DataPoint;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import sav.common.core.SavException;
import sav.common.core.formula.Eq;
import sav.common.core.utils.Randomness;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

public class IlpSelectiveSampling {
	private static Logger log = LoggerFactory.getLogger(IlpSelectiveSampling.class);
	private int numPerExe = 100;
	private List<ExecVar> vars;
	private List<double[]> initValues;
	private List<double[]> allSelectedSamples;
	private int maxSamplesPerSelect = 100; // by default;
	
	/* ilpSolver */
	private ProblemSolver solver = new ProblemSolver();
	
	public IlpSelectiveSampling(List<ExecVar> vars, List<double[]> initVals) {
		this.vars = vars;
		this.initValues = initVals;
		allSelectedSamples = new ArrayList<double[]>();
		allSelectedSamples.addAll(initVals);
	}
	
	public List<double[]> selectData(List<ExecVar> vars, OrCategoryCalculator precondition, List<Divider> divider)
			throws SavException, SAVExecutionTimeOutException {
		return selectData(vars, precondition, divider, maxSamplesPerSelect);
	}
	
	public List<double[]> selectData(List<ExecVar> vars, OrCategoryCalculator precondition, List<Divider> divider, int maxTcs)
			throws SavException, SAVExecutionTimeOutException {
		List<double[]> samples = new ArrayList<double[]>();
		List<Problem> problems = ProblemBuilder.buildTrueValueProblems(vars, precondition, divider, true);
		if (problems.isEmpty()) {
				return null;
		}
		int num = numPerExe / problems.size() + 1;
		int firstCount = solver.getSolvingTotal();
		log.debug("solveMultiple: attempt {} times", num);
		for (Problem problem : problems) {
			List<Result> results = solver.calculateRanges(problem, vars);
			updateSamples(results, samples);
			
			problem.setObjective(problem.getObjective(), OptType.MAX);
			results = solver.solveMultipleTimes(problem, num);
			updateSamples(results, samples);
			
			problem.setObjective(problem.getObjective(), OptType.MIN);
			results = solver.solveMultipleTimes(problem, num);
			updateSamples(results, samples);
		}
		log.debug("run solver {} times", solver.getSolvingTotal() - firstCount);
		samples = limitSamples(samples, maxTcs);
		return samples;
	}
	
	private List<double[]> limitSamples(List<double[]> samples) {
		return limitSamples(samples, maxSamplesPerSelect);
	}
	
	private List<double[]> limitSamples(List<double[]> samples, int maxTcs) {
		if (samples.size() <= maxTcs) {
			log.debug("selected samples: {}", samples.size());
			return samples;
		}
		log.debug("generated samples: {}", samples.size());
		List<double[]> selectedSamples = Randomness.randomSubList(samples, maxTcs);
		samples.removeAll(selectedSamples);
		allSelectedSamples.removeAll(samples);
		log.debug("selected samples: {}, all selected samples: {}", selectedSamples.size(), allSelectedSamples.size());
		return selectedSamples;
	}

	public List<double[]> selectDataForModel(IDecisionNode target, List<ExecVar> originVars, List<DataPoint> datapoints,
			OrCategoryCalculator preconditions, List<Divider> learnedFormulas) throws SavException {
		List<double[]> samples = new ArrayList<double[]>();

		/**
		 * generate data point on the border of divider
		 */
		for (Divider learnedFormula : learnedFormulas) {
			List<Problem> problems = ProblemBuilder.buildProblemWithPreconditions(originVars, preconditions, false);
			if (!problems.isEmpty() && learnedFormula != null) {
				for (Problem problem : problems) {
					ProblemBuilder.addOnBorderConstaints(problem, learnedFormula, originVars);
				}
			}

			for (Problem problem : problems) {
				solver.generateRandomObjective(problem, originVars);
				Result result = solver.solve(problem);
				if (result != null) {
					updateSamples(Arrays.asList(result), samples);
				}
			}
		}

		/**
		 * randomly generate more data points on svm model.
		 */
		samples.addAll(generateRandomPointsWithPrecondition(preconditions, originVars, datapoints, 3));
		samples.addAll(selectHeuristicsSamples(samples, originVars, maxSamplesPerSelect * 2));

		return limitSamples(samples);
	}
	
	private List<double[]> generateRandomPointsWithPrecondition(OrCategoryCalculator preconditions,
			List<ExecVar> originVars, List<DataPoint> datapoints, int toBeGeneratedDataNum) {
		
		int trialNumThreshold = 100;
		List<Result> dataPoints = new ArrayList<>();
		List<double[]> samples = new ArrayList<double[]>();
		if (originVars.size() > 1) {
			
			for (int i = 0; dataPoints.size() < toBeGeneratedDataNum && i < trialNumThreshold; i++) {
				List<Problem> pList = ProblemBuilder.buildProblemWithPreconditions(originVars, preconditions, true);

				if(pList.isEmpty()){
					break;
				}
				
//				Problem p = ProblemBuilder.buildVarBoundContraint(originVars);
				Problem p = pList.get(0);
				solver.generateRandomObjective(p, originVars);
				for (int reducedVarNum = 0; reducedVarNum <= originVars.size(); reducedVarNum++) {
					double[] sample = generateRandomVariableAssignment(originVars, reducedVarNum);
					int bound = 5;
					int k = 0;
					while (sample == null && k < bound) {
						sample = generateRandomVariableAssignment(originVars, reducedVarNum);
					}

					if (sample != null) {
						ProblemBuilder.addEqualConstraints(p, toAssignments(sample, originVars));
						Result res = solver.solve(p);
						updateSamples(Arrays.asList(res), samples);
						break;
					}
				}
			}
		}
		
		return samples;
	}
	
	private List<Eq<Number>> toAssignments(double[] sample, List<ExecVar> originVars) {
		List<Eq<Number>> assignments = new ArrayList<Eq<Number>>();
		for (int i = 0; i < originVars.size(); i++) {
			ExecVar var = originVars.get(i);
				Number number = sample[i];
				switch (var.getType()) {
					case INTEGER:
					case BYTE:
					case CHAR:
					case DOUBLE:
					case FLOAT:
					case LONG:
					case SHORT:
						assignments.add(new Eq<Number>(var, number));
						break;
					case BOOLEAN:
						if (number.intValue() > 0) {
							assignments.add(new Eq<Number>(var, 1));
						} else {
							assignments.add(new Eq<Number>(var, 0));
						}
						break;
					default:
						break;
				}
		}
		return assignments;
	}
	
	/**
	 * slight moving existing data points.
	 * @return 
	 */
	private List<double[]> selectHeuristicsSamples(List<double[]> existingSamples, List<ExecVar> originVars, int maxSamples) {
		double selectiveBound = 5;
		Random random = new Random();
		double offset = random.nextDouble() * selectiveBound;
		List<double[]> candidates = new ArrayList<double[]>();
		for (double[] result : existingSamples) {
			double[] rightPoint = new double[originVars.size()];
			double[] leftPoint =  new double[originVars.size()];
			for (int i = 0; i < originVars.size(); i++) {
				ExecVar var = originVars.get(i);
				Number value = result[i];
				switch (var.getType()) {
					case INTEGER:
						rightPoint[i] = value.intValue() + (int)offset;
						leftPoint[i] = value.intValue() - (int)offset;
						break;
					case CHAR:
						rightPoint[i] = value.intValue() + 1;
						leftPoint[i] = value.intValue() - 1;
						break;
					case BYTE:
						rightPoint[i] = value.byteValue() + 1;
						leftPoint[i] = value.byteValue() - 1;
						break;
					case DOUBLE:
						rightPoint[i] = value.doubleValue() + offset;
						leftPoint[i] = value.doubleValue() - offset;
						break;
					case FLOAT:
						rightPoint[i] = value.floatValue() + (float)offset;
						leftPoint[i] = value.floatValue() - (float)offset;
						break;
					case LONG:
						rightPoint[i] = value.longValue() + (long)offset;
						leftPoint[i] = value.longValue() - (long)offset;
						break;
					case SHORT:
						rightPoint[i] = value.shortValue() + (short)offset;
						leftPoint[i] = value.shortValue() - (short)offset;
						break;
					case BOOLEAN:
						rightPoint[i] = 1 - value.intValue();
						leftPoint[i] = 1 - value.intValue();
						break;
					default:
						break;
				}
			}
			addIfNotDuplicate(candidates, rightPoint);
			addIfNotDuplicate(candidates, leftPoint);
		}
		int fullCandidateSize = candidates.size();
		candidates = Randomness.randomSubList(candidates, maxSamples);
		log.debug("heuristics samples: generated {}, selected candidates {}", fullCandidateSize, candidates.size());
		allSelectedSamples.addAll(candidates);
		return candidates;
	}

	/**
	 * add to candidates list but not update allSelectedSamples
	 * */
	private void addIfNotDuplicate(List<double[]> candidates, double[] rightPoint) {
		if (!isDuplicate(rightPoint)) {
			candidates.add(rightPoint);
		}
	}

	/**
	 * randomly reduce some vars to generate equation constraints.
	 */
	private double[] generateRandomVariableAssignment(List<ExecVar> vars, int reducedVarNum) {
		if (vars.size() <= reducedVarNum) {
			return null;
		}
		int remainedVars = vars.size();
		Random random = new Random();
		double[] sample = new double[vars.size()];
		for (int i = 0; i < vars.size(); i++) {
			if ((remainedVars - reducedVarNum) == 0) {
				continue;
			}
			if (reducedVarNum > 0) {
				if (random.nextDouble() >= 0.5) {
					reducedVarNum--;
					remainedVars--;
					continue;
				}
			}
			Number value = Randomness.nextInt(-Settings.bound, Settings.bound);
			ExecVar var = vars.get(i);
			if (var.getType() == ExecVarType.BOOLEAN) {
				if (value.intValue() > 0) {
					sample[i] = 1;
				} else {
					sample[i] = 0;
				}
			} else {
				sample[i] = value.doubleValue();
			}
			remainedVars--;
		}

		return sample;
	}
	
	private void updateSamples(List<Result> results, List<double[]> samples) {
		List<double[]> solutions = VarSolutionUtils.buildSolutionFromIlpResult(results, vars, initValues);
		for (double[] solution : solutions) {
			updateSamples(samples, solution);
		}
	}

	private void updateSamples(List<double[]> samples, double[] solution) {
		boolean duplicate = isDuplicate(solution);
		updateSamples(samples, solution, duplicate);
	}

	private void updateSamples(List<double[]> samples, double[] solution, boolean duplicate) {
		if (!duplicate) {
			allSelectedSamples.add(solution);
			samples.add(solution);
		}
	}

	private boolean isDuplicate(double[] solution) {
		boolean duplicate = false;
		for (double[] prevSample : allSelectedSamples) {
			if (Arrays.equals(prevSample, solution)) {
				duplicate = true;
				break;
			}
		}
		return duplicate;
	}
	
	
	public void setMaxSamplesPerSelect(int maxSamplesPerSelect) {
		this.maxSamplesPerSelect = maxSamplesPerSelect;
	}
}
