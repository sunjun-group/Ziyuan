/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.machinelearning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.plugin.utils.Settings;
import libsvm.svm_model;
import libsvm.core.Divider;
import libsvm.core.FormulaProcessor;
import libsvm.core.Machine;
import libsvm.core.Model;
import libsvm.extension.MultiDividerBasedCategoryCalculator;
import libsvm.extension.NegativePointSelection;
import libsvm.extension.PositiveSeparationMachine;
import sav.common.core.formula.Formula;
import sav.common.core.formula.LIAAtom;
import sav.common.core.formula.LIATerm;
import sav.common.core.formula.utils.FormulaConjunction;
import sav.common.core.utils.CollectionUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 * temporary class for some extend functions from PositiveSeparationMachine.
 * just temporary to workaround. The best way I think is merging FeatureSelectionMachine to PositiveSeparationMachine.
 */
public class LearningMachine extends PositiveSeparationMachine {
	private static final Logger log = LoggerFactory.getLogger(LearningMachine.class);
	private boolean keepPotentialModel;
	private List<List<svm_model>> previousModels;
	
	public LearningMachine(NegativePointSelection pointSelection) {
		super(pointSelection);
		keepPotentialModel = true;
		previousModels = new LinkedList<>();
	}
	
	@Override
	public Formula getLearnedMultiFormula(List<ExecVar> vars, List<String> dataLabels) {
		return getLearnedMultiFormula(vars, dataLabels, Settings.formulaAccThreshold);
	}

	@Override
	protected Machine train(List<DataPoint> dataPoints) throws SAVExecutionTimeOutException {
		super.train(dataPoints);
		updatePreviousModel();
		return this;
	}
	
	private Machine createNewMachine(final List<String> labels) {
		Machine machine = new Machine().setDataLabels(labels).setParameter(getParameter());
		return machine;
	}
	
	public Formula getLearnedMultiFormula(List<ExecVar> vars, List<String> dataLabels, double accuracyFilter) {
		System.currentTimeMillis();
		List<svm_model> models = getLearnedModels();
		final int numberOfFeatures = getNumberOfFeatures();
//		if (models != null && numberOfFeatures > 0) {
//			List<LearnedModel> formulaList = new ArrayList<LearnedModel>();
//			for (Iterator<svm_model> it = models.iterator(); it.hasNext();) {
//				svm_model svmModel = it.next();
//				if (svmModel != null) {
//					Model model = new Model(svmModel, numberOfFeatures);
//					final Divider explicitDivider = model.getExplicitDivider();
//					Formula current = new FormulaProcessor<ExecVar>(vars).process(explicitDivider, dataLabels, true);
//					double accuracy = getModelAccuracy(CollectionUtils.listOf(svmModel, 1));
//					if (accuracyFilter > 0 && accuracy < accuracyFilter) {
//						log.debug("{}, accuracy: {} [removed]", current, accuracy);
//						it.remove();
//						continue;
//					} else {
//						log.debug("{}, accuracy: {}", current, accuracy);
//					}
//					updatePotentialSingleFeatureModel(current, accuracy, model);
//					formulaList.add(new LearnedModel(model, current, accuracy));
//				}
//			}
//			if (formulaList.isEmpty()) {
//				return null;
//			}
//			List<Formula> formulas = subsetFilter(formulaList);
//			return FormulaConjunction.and(formulas);
//		}
		if (models != null && numberOfFeatures > 0) {
			List<LearnedModel> formulaList = new ArrayList<LearnedModel>();
			double accuracy = getModelAccuracy(models);
			if (accuracyFilter > 0 && accuracy < accuracyFilter) {
				log.debug("{}, accuracy: {} [< accuracyFilter]", models, accuracy);
			} else {
				log.debug("{}, accuracy: {}", models, accuracy);
			}
			for (Iterator<svm_model> it = models.iterator(); it.hasNext();) {
				svm_model svmModel = it.next();
				if (svmModel != null) {
					Model model = new Model(svmModel, numberOfFeatures);
					final Divider explicitDivider = model.getExplicitDivider();
					Formula current = new FormulaProcessor<ExecVar>(vars).process(explicitDivider, dataLabels, true);
					double singleAccuracy = getModelAccuracy(CollectionUtils.listOf(svmModel, 1));
					formulaList.add(new LearnedModel(model, current, singleAccuracy));
				}
			}
			if (formulaList.isEmpty()) {
				return null;
			}
			List<Formula> formulas = subsetFilter(formulaList);
			return FormulaConjunction.and(formulas);
		}

		return null;
	}
	
	public double getModelAccuracy(List<svm_model> models) {
		if (models == null || models.size() == 0) {
			return 0.0;
		}
		return 1.0 - ((double) getWrongClassifiedDataPoints(data, models).size() / data.size());
	}
	
	protected List<DataPoint> getWrongClassifiedDataPoints(List<DataPoint> dataPoints, List<svm_model> models) {
		List<Divider> roundDividers = new ArrayList<Divider>();
		for (svm_model learnModel : models) {
			if (learnModel != null) {
				Divider divider = new Model(learnModel, getNumberOfFeatures()).getExplicitDivider();
				if (divider != null) {
					divider = divider.round();
					roundDividers.add(divider);
				}
			}
		}

		return getWrongClassifiedDataPoints(dataPoints, new MultiDividerBasedCategoryCalculator(roundDividers));
	}

	private void updatePreviousModel() {
		List<svm_model> curModels = learnedModels;
		if (!previousModels.isEmpty()) {
			double maxAcc = getModelAccuracy();
			List<svm_model> bestModels = learnedModels;
			for (List<svm_model> preModels : previousModels ) {
				double preAcc = getModelAccuracy(preModels);				
				if (preAcc <= maxAcc) {
					log.debug("previous model: \n{}, \nacc:{} <= new acc:{}", getLearnedLogic(true, preModels),
							preAcc, maxAcc);
				} else {
					bestModels = preModels;
					maxAcc = preAcc;
					log.debug("previous model: \n{}, \nacc:{} > new acc:{}, mark!!! ",  getLearnedLogic(true, preModels),
							preAcc, maxAcc);
				}
			}
//			learnedModels = bestModels;
			log.debug("best model : \n{}, \nacc:{}", getLearnedLogic(true, bestModels), maxAcc);
			log.debug("learned model : \n{}, \nacc:{}", getLearnedLogic(true, learnedModels), maxAcc);
		}
		if (keepPotentialModel) {
			if (!isContain(previousModels, curModels)) {
				if (curModels != null) {
					List<svm_model> models = new LinkedList<>();
					models.addAll(curModels);
					previousModels.add(models);
				}
			}
		}
	}

	private boolean isContain(List<List<svm_model>> previousModels, List<svm_model> models) {
		boolean contain = false;
		for (List<svm_model> pModels : previousModels) {
			if (modelEqual(pModels,models)) {
				return true;
			}
		}
		return contain;
	}

	private boolean modelEqual(List<svm_model> pModels, List<svm_model> models) {
		if (pModels.size() != models.size()) {
			return false;
		}
		for (svm_model m0 : models) {
			if (!isContain(pModels, m0)) {
				return false;
			}
		}
		return true;
	}

	private List<Formula> subsetFilter(List<LearnedModel> formulaList) {
		List<Formula> filteredList = new ArrayList<Formula>();
		List<Integer> redundantIdx = new ArrayList<Integer>();
		for (int i = 0; i < formulaList.size(); i++) {
			LearnedModel fi = formulaList.get(i);
			if (redundantIdx.contains(i)) {
				continue;
			}
			for (int j = i + 1; j < formulaList.size(); j++) {
				LearnedModel fj = formulaList.get(j);
				if (sameFormat(fi.formula, fj.formula)) {
					if (fi.accuracy > fj.accuracy) {
						redundantIdx.add(j);
					} else {
						redundantIdx.add(i);
					}
				}
			}
		}
		for (int i = 0; i < formulaList.size(); i++) {
			LearnedModel learnedModel = formulaList.get(i);
			if (redundantIdx.contains(i)) {
				getLearnedModels().remove(learnedModel.model.getModel());
				continue;
			}
			filteredList.add(learnedModel.formula);
		}
		return filteredList;
	}

	private boolean sameFormat(Formula fa, Formula fb) {
		ExecVar aVar = getSingleFeatureVar(fa);
		if (aVar != null) {
			return aVar.equals(getSingleFeatureVar(fb));
		}
		return false;
	}
	
	private ExecVar getSingleFeatureVar(Formula formula) {
		if (!(formula instanceof LIAAtom)) {
			return null;
		}
		LIAAtom a = (LIAAtom) formula;
		LIATerm aTerm = a.getSingleTerm();
		if (aTerm != null) {
			return (ExecVar)aTerm.getVariable();
		}
		return null;
	}
	
	private static class SingleFeatureVarModel {
		ExecVar var;
		LearnedModel learnedModel = new LearnedModel();
	}
	
	private static class LearnedModel {
		Formula formula;
		Model model;
		double accuracy;
		public LearnedModel() {
			
		}
		
		public LearnedModel(Model model, Formula formula, double accuracy) {
			this.formula = formula;
			this.model = model;
			this.accuracy = accuracy;
		}
	}
}
