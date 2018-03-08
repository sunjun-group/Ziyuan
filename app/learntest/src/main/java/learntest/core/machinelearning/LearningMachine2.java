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
import libsvm.core.Category;
import libsvm.core.CategoryCalculator;
import libsvm.core.Divider;
import libsvm.core.FormulaProcessor;
import libsvm.core.Machine;
import libsvm.core.Model;
import libsvm.core.Machine.DataPoint;
import libsvm.extension.MultiDividerBasedCategoryCalculator;
import libsvm.extension.MultiOrDividerBasedCategoryCalculator;
import libsvm.extension.NegativePointSelection;
import libsvm.extension.PositiveSeparationMachine;
import sav.common.core.Pair;
import sav.common.core.formula.Formula;
import sav.common.core.formula.LIAAtom;
import sav.common.core.formula.LIATerm;
import sav.common.core.formula.utils.FormulaConjunction;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.TextFormatUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 * temporary class for some extend functions from PositiveSeparationMachine.
 * just temporary to workaround. The best way I think is merging FeatureSelectionMachine to PositiveSeparationMachine.
 */
public class LearningMachine2 extends PositiveSeparationMachine {
	private static final Logger log = LoggerFactory.getLogger(LearningMachine2.class);
	private boolean keepPotentialModel;
	private List<List<svm_model>> previousModels;
	private Category majorCategory; // indicate postive machine rather than negative
	
	public LearningMachine2(NegativePointSelection pointSelection) {
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

		if (canDivideWithOneFormula(dataPoints)) {
			return this;
		}

		int attemptCount = 0;
		double bestAccuracy = 0.0;
		List<svm_model> bestLearnedModels = new ArrayList<svm_model>();
		while (Double.compare(bestAccuracy, 1.0) < 0
				&& (attemptCount == 0 || !this.negativePointSelection.isConsistent())
				&& attemptCount < MAXIMUM_ATTEMPT_COUNT) {
			attemptCount++;
			learnedModels = new ArrayList<svm_model>();
			pairList.clear();
			majorCategory = null;
			attemptTraining(dataPoints);
			double currentAccuracy = getModelAccuracy();
			if (bestAccuracy <= currentAccuracy) {
				bestAccuracy = currentAccuracy;
				bestLearnedModels = learnedModels;
			}
		}
		learnedModels = bestLearnedModels;
		updatePreviousModel();
		return this;
	}
	
	@Override
	protected Machine attemptTraining(final List<DataPoint> dataPoints) throws SAVExecutionTimeOutException {
		System.currentTimeMillis();
		final List<DataPoint> positives = new ArrayList<DataPoint>(dataPoints.size());
		final List<DataPoint> negatives = new ArrayList<DataPoint>(dataPoints.size());

		classifyNegativePositivePoints(dataPoints, positives, negatives);

		majorCategory = (positives.size() > negatives.size()) ? Category.POSITIVE : Category.NEGATIVE;
		List<DataPoint> trainingData = (positives.size() > negatives.size()) ? negatives : positives;
		List<DataPoint> selectionData = (positives.size() > negatives.size()) ? positives : negatives;

		List<DataPoint> allData = new ArrayList<DataPoint>();
		allData.addAll(trainingData);
		allData.addAll(selectionData);
		svmTrain(allData);
		if (model == null) { /** learn model with partial data */
			/** Training set = all positives + limited size of negative */
			int limit = 10;
			int selectionSize = (selectionData.size() < limit) ? selectionData.size() : limit;
			int modelSize = 0, modelLimit = 2;

			learnLoop: while (selectionSize > 0 && selectionData.size() > 0) {
				int trialSize = 2;
				for (int k = 0; k < trialSize; k++) {
//					int selectNum = 1;
					
//					List<DataPoint> selectedPoints = select(selectNum, selectionData, trainingData);					
					List<DataPoint> list = new ArrayList<DataPoint>();

					int index = (int)(Math.random() * trainingData.size());
					DataPoint randomPositive = trainingData.get(index);
					List<DataPoint> referenceDatas = new LinkedList<>();
					referenceDatas.add(randomPositive);
					
					if(selectionData.isEmpty()){
						break learnLoop;
					}
					
					DataPoint nearestDp = negativePointSelection.select(selectionData, referenceDatas);
					list.add(nearestDp);
					selectionData.remove(nearestDp); // when selectNum > 1, selectionData should remove those selected, otherwise always get that one
					List<DataPoint> selectedPoints = list;
					
					trainingData.addAll(selectedPoints);

					svmTrain(trainingData);
					
					log.info("selected points to learn : ");
					for (int i = 0; i < selectedPoints.size(); i++) {
						DataPoint p = trainingData.remove(trainingData.size() - 1);
						selectionData.add(p);  // restore removed
						log.info(p.toString());
					}
					// in general, it should remove all classified true points, but here only remove points with negative category, 
					// because machine only support "And models". If positive should be a>10 || a <-10, this machine will only
					// get one formula like a>10 or a<-10, because when it generate one formula, it will discard points in the other side, 
					// thus it will never generate the other formula. If we remove all classified true points, the machine will get a>10 and a<-10,
					// but it will think model is a>10 && a <-10.
					// Example : org.jscience.mathematics.number.LargeInteger.isPowerOfTwo.470
					removeClassifiedTruePoints(selectionData, majorCategory); 
					
					/** record model and loop until the number of models is greater than modelLimit */
					if (isValidModel(model)) { 
						if (!isContain(learnedModels, model)) {
							learnedModels.add(model);
							String str = getLearnedLogic(true);
							log.info("Lin Yun: learn " + str);
							
							pairList.add(new Pair<DataPoint, DataPoint>(referenceDatas.get(0), nearestDp));
							modelSize++;
							if (modelSize > modelLimit) {
								break learnLoop;
							}
						}
					}
				}
				selectionSize -= 2;
			}

		} else {
			if (isValidModel(model)) {
				learnedModels.add(model);
			}
		}

		return this;
	}
	
	private void removeClassifiedTruePoints(final List<DataPoint> selectionData, Category majorCategory) {
		if (model == null) {
			return;
		}
		// Remove all negatives which are correctly separated
		Divider roundDivider = new Model(model, getNumberOfFeatures()).getExplicitDivider().round();
		log.info("removeClassifiedNegativePoints : " + roundDivider);
		for (Iterator<DataPoint> it = selectionData.iterator(); it.hasNext();) {
			DataPoint dp = it.next();
			if (roundDivider.dataPointBelongTo(dp, majorCategory)) {
				it.remove();
				log.info(dp.toString());
			}
		}
	}
	
	public Formula getLearnedMultiFormula(List<ExecVar> vars, List<String> dataLabels, double accuracyFilter) {
		List<svm_model> models = getLearnedModels();
		final int numberOfFeatures = getNumberOfFeatures();
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
			if (majorCategory == Category.POSITIVE) {
				return FormulaConjunction.or(formulas);
			}
			return FormulaConjunction.and(formulas);
		}

		return null;
	}
	
	@Override
	public double getModelAccuracy() {
		if (learnedModels == null || learnedModels.size() == 0) {
			return 0.0;
		}
		return getModelAccuracy(learnedModels);
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
				try {
					Divider divider = new Model(learnModel, getNumberOfFeatures()).getExplicitDivider();
					if (divider != null) {
						divider = divider.round();
						roundDividers.add(divider);
					}
				}catch (Exception e){
					log.debug(TextFormatUtils.printObj(e.getStackTrace()));
				}
			}
		}

		CategoryCalculator calculator = majorCategory == Category.NEGATIVE ?
				new MultiDividerBasedCategoryCalculator(roundDividers) : new MultiOrDividerBasedCategoryCalculator(roundDividers);
		return getWrongClassifiedDataPoints(dataPoints, calculator);
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
		Pair<Double, ExecVar> aPair = getSingleFeatureVar(fa),
				bPair = getSingleFeatureVar(fb);
		if (aPair != null && bPair != null) {
			ExecVar aVar = aPair.b;
			ExecVar bVar = bPair.b;
			return aVar.equals(bVar) && aPair.a == bPair.a;
		}
		return false;
	}
	
	private Pair<Double, ExecVar> getSingleFeatureVar(Formula formula) {
		if (!(formula instanceof LIAAtom)) {
			return null;
		}
		LIAAtom a = (LIAAtom) formula;
		LIATerm aTerm = a.getSingleTerm();
		if (aTerm != null) {
			return new Pair(aTerm.getCoefficient(), (ExecVar)aTerm.getVariable());
		}
		return null;
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
