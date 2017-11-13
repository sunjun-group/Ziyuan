/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.machinelearning.sampling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.core.commons.data.decision.IDecisionNode;
import learntest.core.commons.utils.VarSolutionUtils;
import learntest.core.machinelearning.calculator.OrCategoryCalculator;
import learntest.core.machinelearning.sampling.javailp.ProblemBuilder;
import learntest.core.machinelearning.sampling.javailp.ProblemSolver;
import learntest.plugin.utils.Settings;
import libsvm.core.Divider;
import libsvm.core.Machine.DataPoint;
import net.sf.javailp.Constraint;
import net.sf.javailp.Linear;
import net.sf.javailp.Operator;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import sav.common.core.Pair;
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
	private Set<Integer> samplesHashcodes;
	private int maxSamplesPerSelect = Settings.getSelectiveNumber(); // by default;
	public static long solveTimeLimit = 60 * 1000;

	/* ilpSolver */
	private ProblemSolver solver = new ProblemSolver();

	public IlpSelectiveSampling(List<ExecVar> vars, List<double[]> initVals) {
		this.vars = vars;
		this.initValues = initVals;
		samplesHashcodes = new HashSet<Integer>();
		updateSampleHashcodes(initVals);
	}

	public List<double[]> selectData(List<ExecVar> vars, OrCategoryCalculator precondition, List<Divider> divider)
			throws SavException, SAVExecutionTimeOutException {
		return selectData(vars, precondition, divider, maxSamplesPerSelect);
	}

	public List<double[]> selectData(List<ExecVar> vars, OrCategoryCalculator precondition, List<Divider> divider,
			int maxTcs) throws SavException, SAVExecutionTimeOutException {

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
			results.clear();
			updateSamples(results, samples);
			if (problem.getVariablesCount() == 1) {
				int loopTimes = 1;
				if (problems.size() == 1) {
					loopTimes = maxTcs;
				}
				results = solver.solveWithOnlyVar(problem, loopTimes);
			} else {
				results = solver.solveWithPreAssignment(problem, num);
			}
			updateSamples(results, samples);
			// problem.setObjective(problem.getObjective(), OptType.MAX);
			// results = solver.solveMultipleTimes(problem, num);
			// updateSamples(results, samples);
			//
			// problem.setObjective(problem.getObjective(), OptType.MIN);
			// results = solver.solveMultipleTimes(problem, num);
			// updateSamples(results, samples);
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
			log.debug("selected samples: {}, all selected samples: {}", samples.size(), samplesHashcodes.size());
			return samples;
		}
		log.debug("generated samples: {}", samples.size());
		List<double[]> selectedSamples = new ArrayList<double[]>(maxTcs);
		Set<Integer> selectedIdxies = Randomness.randomIdxSubList(samples, maxTcs);
		for (int i = 0; i < samples.size(); i++) {
			if (selectedIdxies.contains(i)) {
				selectedSamples.add(samples.get(i));
			} else {
				samplesHashcodes.remove(Arrays.hashCode(samples.get(i)));
			}
		}
		log.debug("selected samples: {}, all selected samples: {}", selectedSamples.size(), samplesHashcodes.size());
		return selectedSamples;
	}

	public static int iterationTime;

	public List<double[]> selectDataForModel(IDecisionNode target, List<ExecVar> originVars,
			OrCategoryCalculator preconditions, List<Divider> learnedFormulas) throws SavException {
//		List<double[]> samples = new ArrayList<double[]>();
		List<double[]> samplesOnLine = new ArrayList<>();
		List<double[]> samplesOnCorner = new ArrayList<>();
		List<double[]> samplesOnMedian = new ArrayList<>();

		/**
		 * generate data point on the border of divider
		 */
		solver.initRangeIfEmpty(originVars);
		//int selectiveSamplingDataSize = 30;
		//for (int i = 0; i < selectiveSamplingDataSize; i++) {
			System.out.println("[LIN YUN] Generate data points on lines: ");
			for (Divider learnedFormula : learnedFormulas) {
				List<Problem> problems = ProblemBuilder.buildProblemWithPreconditions(originVars, preconditions, false);
				if (!problems.isEmpty() && learnedFormula != null) {
					for (Problem problem : problems) {
						ProblemBuilder.addOnBorderConstaints(solver, problem, learnedFormula, originVars);
					}
				}

				for (Problem problem : problems) {
					solver.generateRandomObjective(problem, originVars);
					updateSampleWithProblem(problem, samplesOnLine, false);	
					
					System.out.print("[LIN YUN] " + learnedFormula + ": ");
					for(int h=0; h<samplesOnLine.size(); h++){
						double[] point = samplesOnLine.get(h);
						System.out.print("(");
						for(double d: point){
							System.out.print(d + ",");
							
						}
						System.out.print("),");
					}
					System.out.println();
				}
			}

			/**
			 * solve result that satisfy model intersection and preconditions
			 */
			System.out.println("[LIN YUN] Generate data points on models: ");
			addSamplesOnModels(originVars, preconditions, learnedFormulas, samplesOnCorner);
			addSamplesInMedians(learnedFormulas, samplesOnMedian);
			
			
		//}
		log.debug("selectiveSamplingData : " + (samplesOnLine.size()+samplesOnCorner.size()+samplesOnMedian.size()));
		List<double[]> newSamples = sampleEvolution(samplesOnLine, samplesOnCorner, samplesOnMedian, preconditions,originVars);
		
//		System.currentTimeMillis();
		
		return newSamples;
	}
	
	private void addSamplesInMedians(List<Divider> learnedFormulas, List<double[]> samples) {
		for (Divider formula : learnedFormulas) {
			System.out.println("[LIN YUN] learned formula: " + formula);
			if (formula.getDataPair() != null) {
				Pair<DataPoint, DataPoint> pair = formula.getDataPair();
				double[] d1 = pair.a.getValues();
				double[] d2 = pair.b.getValues();
				System.out.println("[LIN YUN] used data points: (" + d1[0] + ", " + d1[1] + "), ("+ d2[0] + ", " + d2[1] + ")");
				double[] median = new double[d1.length];
				for (int i = 0; i < median.length; i++) {
					median[i] = ( d1[i] + d2[i])/2;
				}
				samples.add(median);
			}
		}
		
	}

	/**
	 * Generate data points on models
	 * @param originVars
	 * @param preconditions
	 * @param learnedFormulas
	 * @param samples
	 */
	private void addSamplesOnModels(List<ExecVar> originVars, OrCategoryCalculator preconditions, List<Divider> learnedFormulas, List<double[]> samples) {
		List<Problem> problems = ProblemBuilder.buildProblemWithPreconditions(originVars, preconditions, false);
		System.currentTimeMillis();
		for (Problem problem : problems) {
			if (learnedFormulas.size() > 1) {
				for (int j = 0; j < learnedFormulas.size()-1; j++) {
					for (int j2 = j+1; j2 < learnedFormulas.size(); j2++) {
						List<Divider> dividers = new ArrayList<>(2);
						dividers.add(learnedFormulas.get(j));
						dividers.add(learnedFormulas.get(j2));
						List<Constraint> constraints = ProblemBuilder.getIntersetConstraint(originVars, dividers);
						problem.getConstraints().addAll(constraints);
						solver.generateRandomObjective(problem, originVars);
						int size = samples.size();
						updateSampleWithProblem(problem, samples, true);	
						
						System.out.print("[LIN YUN] : ");
						for(int h=size; h<samples.size(); h++){
							double[] point = samples.get(h);
							System.out.print("(");
							for(double d: point){
								System.out.print(d + ",");
								
							}
							System.out.print("),");
						}
						System.out.println();
						
						/* restore problem */
						problem.getConstraints().removeAll(constraints);	
						problem.setObjective(new Linear());
					}
				}
			} else {
				ProblemBuilder.addConstraints(originVars, learnedFormulas, problem);
				solver.generateRandomObjective(problem, originVars);
				updateSampleWithProblem(problem, samples, false);		
			}
		}
		
	}

	public void updateSampleWithProblem(Problem problem, List<double[]> samples, boolean addNearDps){
		Pair<Result, Boolean> solverResult = solver.solve(problem, solveTimeLimit);
		Result result = solverResult.first();
		if (result != null) {
			if (addNearDps) {
				updateSamplesWithNearDps(Arrays.asList(result), samples);
			}else {
				updateSamples(Arrays.asList(result), samples);
			}
		}
		if (!solverResult.second()) { /** run long time to solve this problem ,maybe means that these problems are too difficult */
			log.debug("run long time to solve this problem");
		}
	}

	public List<double[]> sampleEvolution(List<double[]> samplesOnLine, List<double[]> samplesOnCorner, List<double[]> samplesOnMedian, OrCategoryCalculator preconditions, List<ExecVar> originVars) {

		List<double[]> newSamplesOnLine = new ArrayList<>();
//		newSamplesOnLine.addAll(samplesOnLine);
		List<double[]> newSampleOnCorner = new ArrayList<>();
//		newSampleOnCorner.addAll(newSampleOnCorner);
		List<double[]> newSampleOnMedian = new ArrayList<>();
//		newSampleOnMedian.addAll(newSampleOnMedian);
		
//		List<double[]> heuList = new ArrayList<>(); 
		List<double[]> randomSamples = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			int bound = 5;
			newSamplesOnLine = selectHeuristicsSamples(samplesOnLine, originVars, maxSamplesPerSelect * 2, bound);
			newSampleOnCorner = selectHeuristicsSamples(samplesOnCorner, originVars, maxSamplesPerSelect * 2, bound);
			newSampleOnMedian = selectHeuristicsSamples(samplesOnMedian, originVars, maxSamplesPerSelect * 2, bound);
		}
		
		int originalSize = samplesOnCorner.size() + samplesOnMedian.size();
		int newSampleSize = newSamplesOnLine.size() + newSampleOnCorner.size() + newSampleOnMedian.size();
		
		/**
		 * randomly generate more data points on svm model.
		 */
		randomSamples.addAll(generateRandomPointsWithPrecondition(preconditions, originVars,
				Math.max(originalSize + newSampleSize, maxSamplesPerSelect)));

		List<double[]> samples = new ArrayList<>();
		int total = originalSize + newSampleSize + randomSamples.size();
		if (total > maxSamplesPerSelect) {
			int sampleOnCornerNum = maxSamplesPerSelect/10;
			int sampleOnMedianNum = maxSamplesPerSelect/10;
			int newSampleOnLineNum = maxSamplesPerSelect/10;
			int newSampleOnCornerNum = maxSamplesPerSelect/10;
			int newSampleOnMedianNum = maxSamplesPerSelect/10;
			int randomNum = maxSamplesPerSelect / 2;
			
//			log.debug("heulistNum : {}, randomNum : {}, sampleNum : {}", heulistNum, randomNum, orignalNum);
			samplesOnCorner = limitSamples(samplesOnCorner, sampleOnCornerNum > 0 ? sampleOnCornerNum : 1);
			samplesOnMedian = limitSamples(samplesOnMedian, sampleOnMedianNum > 0 ? sampleOnMedianNum : 1);
			newSamplesOnLine = limitSamples(newSamplesOnLine, newSampleOnLineNum > 0 ? newSampleOnLineNum : 1);
			newSampleOnCorner = limitSamples(newSampleOnCorner, newSampleOnCornerNum > 0 ? newSampleOnCornerNum : 1);
			newSampleOnMedian = limitSamples(newSampleOnMedian, newSampleOnMedianNum > 0 ? newSampleOnMedianNum : 1);
			randomSamples = limitSamples(randomSamples, randomNum > 0 ? randomNum : 1);
		}
		log.debug("samplesOnLine : {}", array2Str(samplesOnLine));
		log.debug("samplesOnCorner : {}", array2Str(samplesOnCorner));
		log.debug("samplesOnMedian : {}", array2Str(samplesOnMedian));
		log.debug("newSamplesOnLine : {}", array2Str(newSamplesOnLine));
		log.debug("newSampleOnCorner : {}", array2Str(newSampleOnCorner));
		log.debug("newSampleOnMedian : {}", array2Str(newSampleOnMedian));
		log.debug("randomPointsWithPrecondition : {}", array2Str(randomSamples));

		samples.addAll(samplesOnLine);
		samples.addAll(samplesOnCorner);
		samples.addAll(samplesOnMedian);
		samples.addAll(newSamplesOnLine);
		samples.addAll(newSampleOnCorner);
		samples.addAll(newSampleOnMedian);
		samples.addAll(randomSamples);
		return samples;
	}

	private String array2Str(List<double[]> list) {

		StringBuffer sBuffer = new StringBuffer();
		for (double[] ds : list) {
			sBuffer.append("[");
			for (double d : ds) {
				sBuffer.append(d + ",");
			}
			sBuffer.append("]");
		}
		return sBuffer.toString();

	}

	private List<double[]> generateRandomPointsWithPrecondition(OrCategoryCalculator preconditions,
			List<ExecVar> originVars, int toBeGeneratedDataNum) {
		System.currentTimeMillis();
		int trialNumThreshold = 100;
		List<double[]> samples = new ArrayList<double[]>();
		if (originVars.size() > 0) {

			for (int i = 0; samples.size() < toBeGeneratedDataNum && i < trialNumThreshold; i++) {
				List<Problem> pList = ProblemBuilder.buildProblemWithPreconditions(originVars, preconditions, false);

				if (pList.isEmpty()) {
					break;
				}

				// Problem p =
				// ProblemBuilder.buildVarBoundContraint(originVars);
				for (Problem p : pList) {
					solver.generateRandomObjective(p, originVars);
					for (int reducedVarNum = 0; samples.size() < toBeGeneratedDataNum
							&& reducedVarNum <= originVars.size(); reducedVarNum++) {
						double[] sample = generateRandomVariableAssignment(originVars, reducedVarNum);
						int bound = 5;
						int k = 0;
						while (sample == null && k < bound) {
							sample = generateRandomVariableAssignment(originVars, reducedVarNum);
							k++;
						}

						if (sample != null) {
							List<Eq<Number>> constraints = toAssignments(sample, originVars);
							ProblemBuilder.addEqualConstraints(p, constraints);
							Pair<Result, Boolean> solverResult = solver.solve(p, solveTimeLimit);
							Result res = solverResult.first();
							updateSamples(Arrays.asList(res), samples);
							p.getConstraints().clear();
							if (res != null) {
								break;
							}
						}
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
	 * 
	 * @return
	 */
	private List<double[]> selectHeuristicsSamples(List<double[]> existingSamples, List<ExecVar> originVars,
			int maxSamples, double selectiveBound) {
		Random random = new Random();
		double offset = random.nextDouble() * selectiveBound;
		List<double[]> candidates = new ArrayList<double[]>();
		for (double[] result : existingSamples) {
			double[] rightPoint = new double[originVars.size()];
			double[] leftPoint = new double[originVars.size()];
			for (int i = 0; i < originVars.size(); i++) {
				ExecVar var = originVars.get(i);
				Number value = result[i];
				switch (var.getType()) {
				case INTEGER:
					rightPoint[i] = value.intValue() + (int) offset;
					leftPoint[i] = value.intValue() - (int) offset;
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
					rightPoint[i] = value.floatValue() + (float) offset;
					leftPoint[i] = value.floatValue() - (float) offset;
					break;
				case LONG:
					rightPoint[i] = value.longValue() + (long) offset;
					leftPoint[i] = value.longValue() - (long) offset;
					break;
				case SHORT:
					rightPoint[i] = value.shortValue() + (short) offset;
					leftPoint[i] = value.shortValue() - (short) offset;
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
		if (maxSamples < fullCandidateSize) {
			candidates = Randomness.randomSubList(candidates, maxSamples);
			log.debug("heuristics samples: generated {}, selected candidates {}", fullCandidateSize, candidates.size());
		} else {
			log.debug("heuristics samples: {}", fullCandidateSize);
		}
		updateSampleHashcodes(candidates);
		return candidates;
	}

	private void updateSampleHashcodes(Collection<double[]> samples) {
		for (double[] val : samples) {
			updateSampleHashcodes(val);
		}
	}

	private void updateSampleHashcodes(double[] val) {
		samplesHashcodes.add(Arrays.hashCode(val));
	}

	/**
	 * add to candidates list but not update sampleHashcodes
	 */
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
			ExecVar var = vars.get(i);
			int bound = Settings.getBound();
			if (var.isArrayLength()) {
				bound = Settings.getArrayLengthBound();
			}
			Number value = Randomness.nextInt(-bound, bound);
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
	
	private void updateSamplesWithNearDps(List<Result> results, List<double[]> samples) {

		List<double[]> solutions = VarSolutionUtils.buildSolutionFromIlpResult(results, vars, initValues);		
		solutions = nearDps(solutions);
		for (double[] solution : solutions) {
			updateSamples(samples, solution);
		}
	}

	private List<double[]> nearDps(List<double[]> solutions) {

		List<double[]> nearDps = new ArrayList<>(solutions.size() * 5);
		for (double[] ds : solutions) {
			List<double[]> candidates = new ArrayList<>((int)Math.pow(3, ds.length));
			candidates.add(ds);
			for (int i = 0; i < ds.length; i++) {
				List<double[]> tList = new LinkedList<>();
				for (double[] d : candidates) {
					tList.add(d);
					double[] d1 = copyArray(d);
					d1[i]--;
					tList.add(d1);
					double[] d2 = copyArray(d);
					d2[i]++;
					tList.add(d2);
				}
				candidates.clear();
				candidates= tList;
			}
			candidates.remove(0);
			candidates = limitSamples(candidates, 5);
			candidates.add(0, ds);
			nearDps.addAll(candidates);
		}
		return nearDps;
	}
	
	private double[] copyArray(double[] ds) {
		double[] ds2 = new double[ds.length];
		for (int i = 0; i < ds.length; i++) {
			ds2[i] = ds[i];
		}
		return ds2;
	}

	private void updateSamples(List<double[]> samples, double[] solution) {
		boolean duplicate = isDuplicate(solution);
		updateSamples(samples, solution, duplicate);
	}

	private void updateSamples(List<double[]> samples, double[] solution, boolean duplicate) {
		if (!duplicate) {
			updateSampleHashcodes(solution);
			samples.add(solution);
		}
	}

	private boolean isDuplicate(double[] solution) {
		return samplesHashcodes.contains(Arrays.hashCode(solution));
	}

	public void setMaxSamplesPerSelect(int maxSamplesPerSelect) {
		this.maxSamplesPerSelect = maxSamplesPerSelect;
	}
}
