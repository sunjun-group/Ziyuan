package libsvm.extension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import libsvm.svm_model;
import libsvm.core.Category;
import libsvm.core.Divider;
import libsvm.core.FormulaProcessor;
import libsvm.core.Machine;
import libsvm.core.Model;
import sav.common.core.Pair;
import sav.common.core.formula.AndFormula;
import sav.common.core.formula.Formula;
import sav.common.core.utils.CollectionUtils;
import sav.settings.SAVExecutionTimeOutException;
import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

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
	private static final Logger log = LoggerFactory.getLogger(PositiveSeparationMachine.class);

	protected List<svm_model> learnedModels = new ArrayList<svm_model>();
	protected List<Pair<DataPoint, DataPoint>> pairList = new ArrayList<>();

	protected static final int MAXIMUM_ATTEMPT_COUNT = 10;
	protected static final int MAXIMUM_DIVIDER_COUNT = 20;

	protected NegativePointSelection negativePointSelection;

	public PositiveSeparationMachine(NegativePointSelection pointSelection) {
		this.negativePointSelection = pointSelection;
	}

	protected boolean canDivideWithOneFormula(List<DataPoint> dataPoints) {
		boolean canDivideWithOneFormula = true;

		try {
			super.train(dataPoints);
		} catch (SAVExecutionTimeOutException e) {
			e.printStackTrace();
		}
		if (isValidModel(model)) {
			learnedModels.add(model);
		} else {
			canDivideWithOneFormula = false;
		}

		return canDivideWithOneFormula;
	}

	@Override
	protected Machine train(final List<DataPoint> dataPoints) throws SAVExecutionTimeOutException {

		if (canDivideWithOneFormula(dataPoints)) {
			return this;
		}
//		else {
//			return null;
//		}

		int attemptCount = 0;
		double bestAccuracy = 0.0;
		List<svm_model> bestLearnedModels = new ArrayList<svm_model>();
		while (Double.compare(bestAccuracy, 1.0) < 0
				&& (attemptCount == 0 || !this.negativePointSelection.isConsistent())
				&& attemptCount < MAXIMUM_ATTEMPT_COUNT) {
			attemptCount++;
			learnedModels = new ArrayList<svm_model>();
			pairList.clear();
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

	private boolean isModelEqual(svm_model m1, svm_model m2) {
		final int numberOfFeatures = getRandomData().getNumberOfFeatures();
		if (numberOfFeatures > 0) {
			Divider d1 = new Model(m1, numberOfFeatures).getExplicitDivider();
			double theta1[] = d1.getThetas();
			Divider d2 = new Model(m2, numberOfFeatures).getExplicitDivider();
			double theta2[] = d2.getThetas();
			if (theta1.length != theta2.length) {
				return false;
			}
			for (int i = 0; i < theta1.length; i++) {
				if (theta1[i] * theta2[i] < 0) {
					return false;
				}
			}
		}
		
		if (m1.sv_coef.length == m2.sv_coef.length) {
			for (int i = 0; i < m1.sv_coef.length; i++) {
				if (m1.sv_coef[i].length == m2.sv_coef[i].length) {
					for (int j = 0; j < m1.sv_coef[i].length; j++) {
						// ? why 0.1
//						if (Math.abs(m1.sv_coef[i][j] - m2.sv_coef[i][j]) > 0.1) {
//							return false;
//						}
						if (Math.abs(m1.sv_coef[i][j] - m2.sv_coef[i][j]) > 0) {
							return false;
						}
					}
				}
			}
		}else {
			return false;
		}

		if (m1.rho.length == m2.rho.length) {
			for (int i = 0; i < m1.rho.length; i++) {
//				if (Math.abs(m1.rho[i] - m2.rho[i]) > 0.1) {
//					return false;
//				}
				if (Math.abs(m1.rho[i] - m2.rho[i]) > 0) {
					return false;
				}
			}
		}else {
			return false;
		}

		return true;
	}

	protected boolean isContain(List<svm_model> list, svm_model m0) {
		System.currentTimeMillis();
		if (list.isEmpty()) {
			return false;
		}

		for (svm_model m : list) {
			if (isModelEqual(m, m0)) {
				return true;
			}
		}

		return false;
	}

	protected Machine attemptTraining(final List<DataPoint> dataPoints) throws SAVExecutionTimeOutException {
		final List<DataPoint> positives = new ArrayList<DataPoint>(dataPoints.size());
		final List<DataPoint> negatives = new ArrayList<DataPoint>(dataPoints.size());

		classifyNegativePositivePoints(dataPoints, positives, negatives);

		Category majorCategory = (positives.size() > negatives.size()) ? Category.POSITIVE : Category.NEGATIVE;
		List<DataPoint> trainingData = (positives.size() > negatives.size()) ? negatives : positives;
		List<DataPoint> selectionData = (positives.size() > negatives.size()) ? positives : negatives;

		List<DataPoint> allData = new ArrayList<DataPoint>();
		allData.addAll(trainingData);
		allData.addAll(selectionData);
		super.train(allData);
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

					super.train(trainingData);
					
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
					removeClassifiedNegativePoints(selectionData); 
					
//					System.currentTimeMillis();
//					learnedModels.clear();
					
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
	
	protected void svmTrain(List<DataPoint> trainingData) throws SAVExecutionTimeOutException{
		super.train(trainingData);
	}

	protected boolean isValidModel(svm_model model) {
		/* 
		 * LLT: validate model (for simple case, check if sv_coef is empty or not
		 * this is just quick fix to avoid exception due to empty sv_coef.
		 * But the problem here is that although the model.sv_coef is empty, its coverage is sometimes really high (like 1.0)
		 * -> Note here to check the reason.
		 */
		return model != null && CollectionUtils.isNotEmpty(model.sv_coef);
	}

	private List<DataPoint> select(int limit, List<DataPoint> selectionData, List<DataPoint> trainingData) {
		List<DataPoint> list = new ArrayList<DataPoint>();
		for (int i = 0; i < limit; i++) {
			DataPoint p = negativePointSelection.select(selectionData, trainingData);
			list.add(p);
			selectionData.remove(p);
		}
		return list;
	}

	/**
	 * @param dataPoints
	 * @param positives
	 * @param negatives
	 */
	protected void classifyNegativePositivePoints(final List<DataPoint> dataPoints, final List<DataPoint> positives,
			final List<DataPoint> negatives) {
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
		log.info("removeClassifiedNegativePoints : " + roundDivider);
		for (Iterator<DataPoint> it = selectionData.iterator(); it.hasNext();) {
			DataPoint dp = it.next();
			if (roundDivider.dataPointBelongTo(dp, Category.NEGATIVE)) {
				it.remove();
				log.info(dp.toString());
			}
		}
	}

	public List<svm_model> getLearnedModels() {
		return learnedModels;
	}

	@Override
	public List<DataPoint> getWrongClassifiedDataPoints(List<DataPoint> dataPoints) {
		List<Divider> roundDividers = new ArrayList<Divider>();
		for (svm_model learnModel : this.learnedModels) {
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

	@Override
	public double getModelAccuracy() {
		if (learnedModels == null || learnedModels.size() == 0) {
			return 0.0;
		}
		return 1.0 - ((double) getWrongClassifiedDataPoints(data).size() / data.size());
	}
	
	public double getModelAccuracyOnDataset(List<DataPoint> dataset) {
		if (learnedModels == null || learnedModels.size() == 0) {
			return 0.0;
		}
		return 1.0 - ((double) getWrongClassifiedDataPoints(dataset).size() / dataset.size());
	}
	
	@Override
	public String getLearnedLogic(boolean round) {
		return getLearnedLogic(round, learnedModels);
	}
	
	public String getLearnedLogic(boolean round, List<svm_model> learnedModels){
		StringBuilder str = new StringBuilder();
		final int numberOfFeatures = getRandomData().getNumberOfFeatures();
		if (numberOfFeatures > 0) {
			for (svm_model svmModel : learnedModels) {
				if (svmModel != null) {
					final Divider explicitDivider = new Model(svmModel, numberOfFeatures).getExplicitDivider();
					if (str.length() != 0) {
						str.append("\n");
					}
					str.append(getLearnedLogic(explicitDivider, round));
				}
			}
		}
		
		return str.toString();		
	}

//	@Deprecated
	public List<Divider> getLearnedDividers() {
		List<Divider> roundDividers = new ArrayList<Divider>();
		for (int i = 0; i < this.learnedModels.size(); i++) {
			svm_model learnModel = learnedModels.get(i);
			if (learnModel != null) {
				Divider divider = new Model(learnModel, getNumberOfFeatures()).getExplicitDivider().round();
				if(pairList.size()>i){
					divider.setDataPair(pairList.get(i));					
				}
				roundDividers.add(divider);
			}
		}
		return roundDividers;
	}

	public Formula getLearnedMultiFormula(List<ExecVar> vars, List<String> dataLabels){		
		Formula formula = null;
		List<svm_model> models = getLearnedModels();
		final int numberOfFeatures = getNumberOfFeatures();
		if (models != null && numberOfFeatures > 0) {
			for (svm_model svmModel : models) {
				if (svmModel != null) {
					Model model = new Model(svmModel, numberOfFeatures);
					final Divider explicitDivider = model.getExplicitDivider();
					Formula current = new FormulaProcessor<ExecVar>(vars).process(explicitDivider, dataLabels, true);
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

	/**
	 * targetVars is subset of originalVars, because Divider is reused by Precondition,
	 * complete Divider of targetVars  
	 * 
	 * @param targetVars
	 * @param originalVars
	 * @return
	 */
	public List<Divider> getFullLearnedDividers(List<String> targetLabels, List<ExecVar> originalVars) {
		List<ExecVar> learningVars = new LinkedList<ExecVar>();
		learningVars.addAll(originalVars);
//		learningVars.addAll(createPolyClassifierVars(originalVars));
		
		List<Divider> dividers = getLearnedDividers();		
		if (targetLabels.size() == learningVars.size()) {
			return dividers;
		}
		
		// get full dividers
		List<Divider> completeDividers = new ArrayList<Divider>(dividers.size());
		for (Divider divider : dividers) {
			double[] thetas = new double[learningVars.size()];
			double[] targetThetas = divider.getThetas();

			Pair<DataPoint, DataPoint> pairOld = divider.getDataPair();
			int indexOld = 0;
			for (int i = 0; i < thetas.length; i++) {
				if (indexOld<targetLabels.size() && learningVars.get(i).getLabel().equals(targetLabels.get(indexOld))) {
					thetas[i] = targetThetas[indexOld];
					indexOld++;
				}else {
					thetas[i] = 0;
				}
			}
			
			Divider divider2 = new Divider(thetas, divider.getTheta0(), divider.isRounded());
			setFullDataPair(pairOld, divider2, learningVars, targetLabels);
			completeDividers.add(divider2);
		}
		return completeDividers;
	}

	private void setFullDataPair(Pair<DataPoint, DataPoint> pairOld, Divider divider2, List<ExecVar> learningVars, List<String> targetLabels) {
		if (pairOld != null) {
			double[] aValueOld = pairOld.a.getValues();
			double[] aValue = new double[learningVars.size()];
			double[] bValueOld = pairOld.b.getValues();
			double[] bValue = new double[learningVars.size()];

			int indexOld = 0;
			for (int i = 0; i < learningVars.size(); i++) {
				if (indexOld<targetLabels.size() && learningVars.get(i).getLabel().equals(targetLabels.get(indexOld))) {
					aValue[i] = aValueOld[indexOld];
					bValue[i] = bValueOld[indexOld];
					indexOld++;
				}else {
					aValue[i] = 0;
					bValue[i] = 0;
				}
			}
			DataPoint a = new DataPoint(learningVars.size());
			a.setCategory(pairOld.a.getCategory());
			a.setValues(aValue);
			DataPoint b = new DataPoint(learningVars.size());
			b.setCategory(pairOld.b.getCategory());
			b.setValues(bValue);
			Pair<DataPoint, DataPoint> pair = new Pair<Machine.DataPoint, Machine.DataPoint>(a, b);
			divider2.setDataPair(pair);
		}
		
	}

	private Collection<? extends ExecVar> createPolyClassifierVars(List<ExecVar> orgVars) {
		List<ExecVar> polyClassifierVars = new ArrayList<ExecVar>(orgVars);
		int size = orgVars.size();
		for (int i = 0; i < size; i++) {
			ExecVar var = orgVars.get(i);
			for (int j = i; j < size; j++) {
				polyClassifierVars
						.add(new ExecVar(var.getLabel() + " * " + orgVars.get(j).getLabel(), ExecVarType.DOUBLE));
			}
		}
		return polyClassifierVars;
	}

}
