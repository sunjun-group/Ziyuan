package libsvm.extension;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import libsvm.svm_model;
import libsvm.core.Category;
import libsvm.core.Divider;
import libsvm.core.Machine;
import libsvm.core.Model;
import sav.settings.SAVExecutionTimeOutException;

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

	@Override
	protected Machine train(final List<DataPoint> dataPoints) throws SAVExecutionTimeOutException {
		int attemptCount = 0;
		double bestAccuracy = 0.0;
		List<svm_model> bestLearnedModels = null;
		while (Double.compare(bestAccuracy, 1.0) < 0
				&& (attemptCount == 0 || !this.negativePointSelection.isConsistent())
				&& attemptCount < MAXIMUM_ATTEMPT_COUNT) {
			attemptCount++;
			learnedModels = new ArrayList<svm_model>();
			attemptTraining(dataPoints);
			double currentAccuracy = getModelAccuracy();
			if (bestAccuracy < currentAccuracy) {
				bestAccuracy = currentAccuracy;
				bestLearnedModels = learnedModels;
			}
		}
		learnedModels = bestLearnedModels;

		return this;
	}

	private Machine attemptTraining(final List<DataPoint> dataPoints) throws SAVExecutionTimeOutException {
		final List<DataPoint> positives = new ArrayList<DataPoint>(dataPoints.size());
		final List<DataPoint> negatives = new ArrayList<DataPoint>(dataPoints.size());

		classifyNegativePositivePoints(dataPoints, positives, negatives);

		List<DataPoint> trainingData = positives;
		int loopCount = 0;
		while (!negatives.isEmpty() && loopCount < MAXIMUM_DIVIDER_COUNT) {
			loopCount++;
			// Training set = all positives + 1 negative
			trainingData.add(negativePointSelection.select(negatives, positives));
			super.train(trainingData);

			if (model != null) learnedModels.add(model);

			trainingData.remove(trainingData.size() - 1);
			removeClassifiedNegativePoints(negatives);
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
	 * @param negatives
	 */
	private void removeClassifiedNegativePoints(final List<DataPoint> negatives) {
		if (model == null) {
			return;
		}
		// Remove all negatives which are correctly separated
		Divider roundDivider = new Model(model, getNumberOfFeatures()).getExplicitDivider().round();
		for (Iterator<DataPoint> it = negatives.iterator(); it.hasNext();) {
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
				roundDividers.add(new Model(learnModel, getNumberOfFeatures()).getExplicitDivider()
						.round());
			}
		}

		return getWrongClassifiedDataPoints(dataPoints, new MultiDividerBasedCategoryCalculator(
				roundDividers));
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

}
