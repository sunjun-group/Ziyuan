package libsvm.extension;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import libsvm.svm_model;
import libsvm.core.Category;
import libsvm.core.Divider;
import libsvm.core.FormulaProcessor;
import libsvm.core.Machine;
import libsvm.core.Model;
import sav.common.core.formula.AndFormula;
import sav.common.core.formula.Formula;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecVar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This machine tries to separate the positive points from negative points by
 * gradually learning from the set of all positive points and one of the
 * negative points. <br/>
 * The learning result is a collection of dividers which separate the positive
 * set with the negative set.<br/>
 * As a result, for a point to be identified as POSITIVE, it must satisfy the
 * conjunction of the conditions represented by ALL of the dividers.
 * 
 * @author Nguyen Phuoc Nguong Phuc (npn)
 * 
 */
public class PositiveSeparationMachine extends Machine {
	protected static final Logger LOGGER = LoggerFactory.getLogger(PositiveSeparationMachine.class);

	private List<svm_model> learnedModels = new ArrayList<svm_model>();

	private static final int MAXIMUM_ATTEMPT_COUNT = 10;
	private static final int MAXIMUM_DIVIDER_COUNT = 20;

	private NegativePointSelection negativePointSelection;

	public PositiveSeparationMachine(NegativePointSelection pointSelection) {
		this.negativePointSelection = pointSelection;
	}

	private boolean canDivideWithOneFormula(List<DataPoint> dataPoints){
		boolean canDivideWithOneFormula = true;
		
		try {
			super.train(dataPoints);
		} catch (SAVExecutionTimeOutException e) {
			e.printStackTrace();
		}
		if (model != null) {
			learnedModels.add(model);
		}
		else{
			canDivideWithOneFormula = false;
		}
		
		return canDivideWithOneFormula;
	}
	
	@Override
	protected Machine train(final List<DataPoint> dataPoints) throws SAVExecutionTimeOutException {
		if(canDivideWithOneFormula(dataPoints)){
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
			attemptTraining(dataPoints);
			double currentAccuracy = getModelAccuracy();
			if (bestAccuracy <= currentAccuracy) {
				bestAccuracy = currentAccuracy;
				bestLearnedModels = learnedModels;
			}
		}
		learnedModels = bestLearnedModels;
		
		return this;
	}
	
	private boolean isModelEqual(svm_model m1, svm_model m2){
		if(m1.sv_coef.length==m2.sv_coef.length){
			for(int i=0; i<m1.sv_coef.length; i++){
				if(m1.sv_coef[i].length == m2.sv_coef[i].length){
					for(int j=0; j<m1.sv_coef[i].length; j++){
						if(Math.abs(m1.sv_coef[i][j]-m2.sv_coef[i][j])>0.1){
							return false;
						}
					}
				}
			}
		}
		
		if(m1.rho.length==m2.rho.length){
			for(int i=0; i<m1.rho.length; i++){
				if(Math.abs(m1.rho[i]-m2.rho[i])>0.1){
					return false;
				}
			}
		}
		
		return true;
	}
	
	private boolean isContain(List<svm_model> list, svm_model m0){
		if(list.isEmpty()){
			return false;
		}
		
		for(svm_model m: list){
			if(isModelEqual(m, m0)){
				return true;
			}
		}
		
		return false;
	}

	private Machine attemptTraining(final List<DataPoint> dataPoints) throws SAVExecutionTimeOutException {
		final List<DataPoint> positives = new ArrayList<DataPoint>(dataPoints.size());
		final List<DataPoint> negatives = new ArrayList<DataPoint>(dataPoints.size());

		classifyNegativePositivePoints(dataPoints, positives, negatives);

		List<DataPoint> trainingData = (positives.size()>negatives.size()) ? negatives : positives;
		List<DataPoint> selectionData = (positives.size()>negatives.size()) ? positives : negatives;
		
		int loopCount = 0;
		while (!selectionData.isEmpty() && loopCount < 3) {
			loopCount++;
			// Training set = all positives + 1 negative
			DataPoint p = negativePointSelection.select(selectionData, trainingData);
			trainingData.add(p);
			super.train(trainingData);

			if (model != null) {
				if(!isContain(learnedModels,model)){
					learnedModels.add(model);					
				}
			}

			trainingData.remove(trainingData.size() - 1);
			selectionData.remove(p);
			
			removeClassifiedNegativePoints(selectionData);
		}

		return this;
	}

	/**
	 * @param dataPoints
	 * @param positives
	 * @param negatives
	 */
	private void classifyNegativePositivePoints(final List<DataPoint> dataPoints,
			final List<DataPoint> positives, final List<DataPoint> negatives) {
		for (DataPoint point : dataPoints) {
			if (Category.POSITIVE == point.getCategory()) {
				positives.add(point);
			} else {
				negatives.add(point);
			}
		}
	}

	/**
	 * @param selectionData
	 */
	private void removeClassifiedNegativePoints(final List<DataPoint> selectionData) {
		if (model == null) {
			return;
		}
		// Remove all negatives which are correctly separated
		Divider roundDivider = new Model(model, getNumberOfFeatures()).getExplicitDivider().round();
		for (Iterator<DataPoint> it = selectionData.iterator(); it.hasNext();) {
			DataPoint dp = it.next();
			if (roundDivider.dataPointBelongTo(dp, Category.NEGATIVE)) {
				it.remove();
			}
		}
	}
	
	public List<svm_model> getLearnedModels() {
		return learnedModels;
	}

	@Override
	protected List<DataPoint> getWrongClassifiedDataPoints(List<DataPoint> dataPoints) {
		List<Divider> roundDividers = new ArrayList<Divider>();
		for (svm_model learnModel : this.learnedModels) {
			if (learnModel != null) {
				Divider divider = new Model(learnModel, getNumberOfFeatures()).getExplicitDivider();
				if(divider != null){
					divider = divider.round();
					roundDividers.add(divider);					
				}
			}
		}

		return getWrongClassifiedDataPoints(dataPoints, new MultiDividerBasedCategoryCalculator(roundDividers));
	}

	@Override
	public String getLearnedLogic(boolean round) {
		StringBuilder str = new StringBuilder();

		final int numberOfFeatures = getRandomData().getNumberOfFeatures();
		if (numberOfFeatures > 0) {			
			for (svm_model svmModel : learnedModels) {
				if (svmModel != null) {				
					final Divider explicitDivider = new Model(svmModel, numberOfFeatures)
					.getExplicitDivider();
					if (str.length() != 0) {
						str.append("\n");
					}
					str.append(getLearnedLogic(explicitDivider, round));
				}
			}
		}

		return str.toString();
	}
	
	public List<Divider> getLearnedDividers() {
		List<Divider> roundDividers = new ArrayList<Divider>();
		for (svm_model learnModel : this.learnedModels) {
			if (learnModel != null) {
				roundDividers.add(new Model(learnModel, getNumberOfFeatures()).getExplicitDivider()
						.round());
			}
		}
		return roundDividers;
	}
	
	public Formula getLearnedMultiFormula(List<ExecVar> vars, List<String> dataLabels) {
		Formula formula = null;
		List<svm_model> models = getLearnedModels();
		final int numberOfFeatures = getNumberOfFeatures();
		if (models != null && numberOfFeatures > 0) {			
			for (svm_model svmModel : models) {
				if (svmModel != null) {				
					Model model = new Model(svmModel, numberOfFeatures);
					final Divider explicitDivider = model.getExplicitDivider();
					Formula current = new FormulaProcessor<ExecVar>(vars).
							process(explicitDivider, dataLabels, true);
					if (formula == null) {
						formula = current;
					} else {
						formula = new AndFormula(formula, current);
					}
				}
			}
		}
		
		return formula;
	}

}
