package libsvm.extension;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import libsvm.svm_model;
import libsvm.core.Category;
import libsvm.core.Divider;
import libsvm.core.Machine;
import libsvm.core.Model;

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

	private List<svm_model> learnedModels = new ArrayList<svm_model>();
	
	private static final int MAXIMUM_ATTEMPT_COUNT = 100;
	private static final int MAXIMUM_DIVIDER_COUNT = 3;
	
	private NegativePointSelection negativePointSelection;

	public PositiveSeparationMachine(NegativePointSelection pointSelection) {
		this.negativePointSelection = pointSelection;
	}

	@Override
	protected Machine train(final List<DataPoint> dataPoints) {
		int attemptCount = 0;
		double bestAccuracy = 0.0;
		List<svm_model> bestLearnedModels = null;
		while (Double.compare(bestAccuracy, 1.0) < 0 && attemptCount < MAXIMUM_ATTEMPT_COUNT) {
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
	
	private Machine attemptTraining(final List<DataPoint> dataPoints) {
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

			learnedModels.add(model);

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
	private void classifyNegativePositivePoints(
			final List<DataPoint> dataPoints, final List<DataPoint> positives,
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
	 * @param negatives
	 */
	private void removeClassifiedNegativePoints(final List<DataPoint> negatives) {
		// Remove all negatives which are correctly separated
		for (Iterator<DataPoint> it = negatives.iterator(); it.hasNext();) {
			DataPoint dp = it.next();
			if (Category.NEGATIVE == calculateCategory(dp, model, null)) {
				it.remove();
			}
		}
	}

	@Override
	protected List<DataPoint> getWrongClassifiedDataPoints(List<DataPoint> dataPoints) {
		return getWrongClassifiedDataPoints(dataPoints, new CategoryCalculator() {
			@Override
			public Category getCategory(final DataPoint dataPoint) {
				// Loop on learned models
				// If the category is NEGATIVE, return it
				// Else keep looping
				// If the point passed all learned model, return POSITIVE
				for (svm_model model : learnedModels) {
					if (Category.NEGATIVE == calculateCategory(dataPoint, model, null)) {
						return Category.NEGATIVE;
					}
				}
				// The point satisfies all dividers
				return Category.POSITIVE;
			}
		});
	}

	@Override
	public String getLearnedLogic() {
		StringBuilder str = new StringBuilder();

		DataPoint randomData = getRandomData();
		for (svm_model svmModel : learnedModels) {
			final Divider explicitDivider = new Model(svmModel, randomData.getNumberOfFeatures())
					.getExplicitDivider();
			if (str.length() != 0) {
				str.append("\n");
			}
			str.append(getLearnedLogic(explicitDivider, randomData));
		}

		return str.toString();
	}

}
