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

import org.apache.poi.hssf.record.IterationRecord;
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
			if (problem.getVariablesCount()==1) {
				int loopTimes = 1;
				if (problems.size()==1) {
					loopTimes = maxTcs;
				}
				results = solver.solveWithOnlyVar(problem, loopTimes);
			}else {
				results = solver.solveWithPreAssignment(problem, num);
			}
			updateSamples(results, samples);
//			problem.setObjective(problem.getObjective(), OptType.MAX);
//			results = solver.solveMultipleTimes(problem, num);
//			updateSamples(results, samples);
//			
//			problem.setObjective(problem.getObjective(), OptType.MIN);
//			results = solver.solveMultipleTimes(problem, num);
//			updateSamples(results, samples);
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
		List<double[]> samples = new ArrayList<double[]>();

		if (originVars.size() < 60) { /** too many variables which will cost much time because we will construct a linear with all variables */

			/**
			 * generate data point on the border of divider
			 */
			int selectiveSamplingDataSize = 30;
			int complexTimes = 0; /** break iteration if there occurs too many complex problems */
			iteration: for(int i=0; i<selectiveSamplingDataSize; i++){
				int complexProblem = 0;
				outer : for (Divider learnedFormula : learnedFormulas) {
					List<Problem> problems = ProblemBuilder.buildProblemWithPreconditions(originVars, preconditions, false);
					if (!problems.isEmpty() && learnedFormula != null) {
						for (Problem problem : problems) {
							ProblemBuilder.addOnBorderConstaints(problem, learnedFormula, originVars);
						}
					}

					for (Problem problem : problems) {
						solver.generateRandomObjective(problem, originVars);
						Pair<Result, Boolean> solverResult = solver.solve(problem, solveTimeLimit);
						Result result = solverResult.first();
						if (result != null) {
							updateSamples(Arrays.asList(result), samples);
						}
						if (!solverResult.second()) { /** run long time to solve this problem ,maybe means that these problems are too difficult*/
							log.debug("run long time to solve this problem");
							complexProblem++;
							complexTimes++;
							if (complexTimes>=4) {
								log.debug("break selectiveSamplingData iteration");
								break iteration;
							}
							if (complexProblem >= 2) { /** separate the chances of running long time problem into different iteration */
								break outer;
							}else
								break ;
						}
					}
				}
				if (complexProblem>0) {
					i = i+ Math.max(selectiveSamplingDataSize/6, learnedFormulas.size());
				}
			}
		}
		log.debug("selectiveSamplingData : " + samples.size());
		List<double[]> heuList = new LinkedList<>(), randomSamples = new LinkedList<>();
		for(int i=0; i<2; i++){
			int bound = 10-(2*iterationTime--);
			heuList.addAll(selectHeuristicsSamples(samples, originVars, maxSamplesPerSelect * 2, bound));
		}
		/**
		 * randomly generate more data points on svm model.
		 */
		randomSamples.addAll(generateRandomPointsWithPrecondition(preconditions, originVars, 
				Math.max(samples.size() , maxSamplesPerSelect)));
		
		int total = heuList.size() + samples.size() + randomSamples.size();
		if (total > maxSamplesPerSelect) {
			int heulistNum = heuList.size()* maxSamplesPerSelect/total ,
					randomNum = randomSamples.size()  * maxSamplesPerSelect/ total,
					orignalNum = samples.size() * maxSamplesPerSelect/total; 
			samples = limitSamples(samples, orignalNum > 0 ? orignalNum : 1);
			heuList = limitSamples(heuList, heulistNum > 0 ? heulistNum : 1);
			randomSamples = limitSamples(randomSamples, randomNum > 0 ? randomNum : 1);
		}
		
		samples.addAll(heuList);
		samples.addAll(randomSamples);
		return samples;
	}
	
	private List<double[]> generateRandomPointsWithPrecondition(OrCategoryCalculator preconditions,
			List<ExecVar> originVars, int toBeGeneratedDataNum) {
		
		int trialNumThreshold = 100;
		List<double[]> samples = new ArrayList<double[]>();
		if (originVars.size() > 0) {
			
			for (int i = 0; samples.size() < toBeGeneratedDataNum && i < trialNumThreshold; i++) {
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
						Pair<Result, Boolean> solverResult = solver.solve(p, solveTimeLimit);
						Result res = solverResult.first();
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
	private List<double[]> selectHeuristicsSamples(List<double[]> existingSamples, List<ExecVar> originVars, int maxSamples, double selectiveBound) {
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
			Number value = Randomness.nextInt(-Settings.getBound(), Settings.getBound());
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
