package libsvm.extension;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import libsvm.svm_model;
import libsvm.core.Category;
import libsvm.core.DataPoint;
import libsvm.core.Machine;

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
	private int maxLoopCounter = 100;

	public PositiveSeparationMachine() {
		// Nothing special
	}

	public PositiveSeparationMachine(final int maxLoopCounter) {
		this.maxLoopCounter = maxLoopCounter;
	}

	@Override
	protected Machine train(final List<DataPoint> dataPoints) {
		final List<DataPoint> positives = new ArrayList<DataPoint>(dataPoints.size());
		final List<DataPoint> negatives = new ArrayList<DataPoint>(dataPoints.size());
		for (DataPoint point : dataPoints) {
			if (Category.POSITIVE == point.getCategory()) {
				positives.add(point);
			} else {
				negatives.add(point);
			}
		}

		int loopCount = 0;
		while (!negatives.isEmpty() && loopCount < maxLoopCounter) {
			loopCount++;
			// Training set = all positives + 1 negative
			final List<DataPoint> trainingData = new ArrayList<DataPoint>(dataPoints.size());
			trainingData.addAll(positives);
			trainingData.add(negatives.get(0));
			super.train(trainingData);

			learnedModels.add(model);

			// Remove all negatives which are correctly separated
			for (Iterator<DataPoint> it = negatives.iterator(); it.hasNext();) {
				DataPoint dp = it.next();
				if (Category.NEGATIVE == calculateCategory(dp, model, null)) {
					it.remove();
				}
			}
		}

		return this;
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

}
