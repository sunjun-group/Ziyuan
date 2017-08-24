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
	private Map<String, SingleFeatureVarModel> potentialSingleFeatureModels;
	private double singleFeatureAccMin = 0.8;
	
	public LearningMachine(NegativePointSelection pointSelection) {
		super(pointSelection);
		keepPotentialModel = true;
		potentialSingleFeatureModels = new HashMap<String, SingleFeatureVarModel>();
	}
	
	@Override
	public Formula getLearnedMultiFormula(List<ExecVar> vars, List<String> dataLabels) {
		return getLearnedMultiFormula(vars, dataLabels, Settings.formulaAccThreshold);
	}

	@Override
	protected Machine train(List<DataPoint> dataPoints) throws SAVExecutionTimeOutException {
		if (!potentialSingleFeatureModels.isEmpty()) {
			for (Iterator<Entry<String, SingleFeatureVarModel>> it = potentialSingleFeatureModels.entrySet().iterator();
					it.hasNext(); ) {
				Entry<String, SingleFeatureVarModel> entry = it.next();
				LearnedModel learnedModel = entry.getValue().learnedModel;
				log.debug("potentialSingleFeatureModel: {}", learnedModel.formula);
				Machine machine = createNewMachine(CollectionUtils.listOf(entry.getKey(), 1));
				try {
					machine.addDataPoints(dataPoints);
					machine.train();
					double acc = machine.getModelAccuracy();
					log.debug("new model accuracy: {}", acc);
					if (acc < singleFeatureAccMin) {
						double newAcc = getModelAccuracy(CollectionUtils.listOf(learnedModel.model.getModel(), 1));
						if (newAcc > singleFeatureAccMin) {
							learnedModel.accuracy = acc;
							// the potential model still valid
						} else {
							it.remove();
						}
					} else {
						// update
						learnedModel.accuracy = acc;
						learnedModel.model = machine.getModel();
					}
				} catch (SAVExecutionTimeOutException e) {
					it.remove();
				}
			}
		}
		super.train(dataPoints);
		for (SingleFeatureVarModel model : potentialSingleFeatureModels.values()) {
			getLearnedModels().add(model.learnedModel.model.getModel());
		}
		return this;
	}
	
	private Machine createNewMachine(final List<String> labels) {
		Machine machine = new Machine().setDataLabels(labels).setParameter(getParameter());
		return machine;
	}
	
	public Formula getLearnedMultiFormula(List<ExecVar> vars, List<String> dataLabels, double accuracyFilter) {
		potentialSingleFeatureModels.clear();
		List<svm_model> models = getLearnedModels();
		final int numberOfFeatures = getNumberOfFeatures();
		if (models != null && numberOfFeatures > 0) {
			List<LearnedModel> formulaList = new ArrayList<LearnedModel>();
			for (Iterator<svm_model> it = models.iterator(); it.hasNext();) {
				svm_model svmModel = it.next();
				if (svmModel != null) {
					Model model = new Model(svmModel, numberOfFeatures);
					final Divider explicitDivider = model.getExplicitDivider();
					Formula current = new FormulaProcessor<ExecVar>(vars).process(explicitDivider, dataLabels, true);
					double accuracy = getModelAccuracy(CollectionUtils.listOf(svmModel, 1));
					if (accuracyFilter > 0 && accuracy < accuracyFilter) {
						LOGGER.debug("{}, accuracy: {} [removed]", current, accuracy);
						it.remove();
						continue;
					} else {
						LOGGER.debug("{}, accuracy: {}", current, accuracy);
					}
					updatePotentialSingleFeatureModel(current, accuracy, model);
					formulaList.add(new LearnedModel(model, current, accuracy));
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

	private void updatePotentialSingleFeatureModel(Formula current, double accuracy, Model model) {
		if (keepPotentialModel) {
			ExecVar singleFeatureVar = getSingleFeatureVar(current);
			if (singleFeatureVar != null && accuracy >= singleFeatureAccMin) {
				SingleFeatureVarModel singleFeatureModel = potentialSingleFeatureModels.get(singleFeatureVar.getLabel());
				if (singleFeatureModel == null) {
					singleFeatureModel = new SingleFeatureVarModel();
					singleFeatureModel.var = singleFeatureVar;
					potentialSingleFeatureModels.put(singleFeatureVar.getLabel(), singleFeatureModel);
				} 
				if (singleFeatureModel.learnedModel.accuracy <= accuracy) {
					singleFeatureModel.learnedModel.accuracy = accuracy;
					singleFeatureModel.learnedModel.model = model;
					singleFeatureModel.learnedModel.formula = current;
				}
			}
		}
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
