package libsvm.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import libsvm.core.Category;
import libsvm.core.DataPoint;

/**
 * The training algorithm for this Machine is as follows:<br/>
 * <ul>
 * <li>Run SVM algorithm on the current data set to find a divider</li>
 * <li>Find the collection of all points which are (A) and are not (B)
 * classified correctly using the divider</li>
 * <li>While (B) is not empty, do the following steps:</li>
 * <ul>
 * <li>Randomly select 1 point from (B), mix it with (A), then run SVM again on
 * this new data set.</li>
 * <li>Remove from (B) all points which are correctly classified using this new
 * divider</li>
 * <li>Merge this new divider with the existing divider</li>
 * </ul>
 * </ul> <br/>
 * Because it is not guaranteed that the algorithm will be able to find correct
 * divider for all cases, we limit the maximum number of the while loops by
 * using <code>maximumLoopCount</code>, which has the value of <code>10</code>
 * by default that can be altered using the constructor.
 * 
 * @author Nguyen Phuoc Nguong Phuc (npn)
 * 
 */
public class MultiAttemptMachine extends MultiCutMachine {

	private final int maximumLoopCount;
	private int loopCount = 0;
	private List<DataPoint> rightPoints = new ArrayList<DataPoint>();
	private List<DataPoint> wrongPoints = new ArrayList<DataPoint>();

	public MultiAttemptMachine() {
		this.maximumLoopCount = 10;
	}

	public MultiAttemptMachine(final int maximumLoopCount) {
		this.maximumLoopCount = maximumLoopCount;
	}

	@Override
	protected boolean keepTraining(List<DataPoint> trainingData, int wrongCategories) {
		return loopCount < maximumLoopCount && super.keepTraining(trainingData, wrongCategories);
	}

	@Override
	protected List<DataPoint> buildNextTrainingData(List<DataPoint> currentTrainingData,
			LearnedData learnedData, Category wrongCategory) {
		loopCount++;
		if (1 == loopCount) {
			// Only build list of right/wrong points in the 1st loop
			for (DataPoint dp : currentTrainingData) {
				if (calculateCategory(dp, learnedData.getModel(), null).equals(dp.getCategory())) {
					rightPoints.add(dp);
				} else {
					wrongPoints.add(dp);
				}
			}
		} else {
			// Remove from wrongPoints the points which can be categorized
			// correctly using the current divider
			for (Iterator<DataPoint> it = wrongPoints.iterator(); it.hasNext();) {
				final DataPoint dp = it.next();
				if (calculateCategory(dp, learnedData.getModel(), null).equals(dp.getCategory())) {
					it.remove();
				}
			}
		}

		// Stop if all points can be categorized correctly
		if (wrongPoints.isEmpty()) {
			return Collections.emptyList();
		}

		final List<DataPoint> newTrainingData = new ArrayList<DataPoint>();
		// Add all correctly classified points + 1 wrong point
		newTrainingData.addAll(rightPoints);
		newTrainingData.add(wrongPoints.get(0));

		return newTrainingData;
	}

	@Override
	protected List<DataPoint> getWrongClassifiedDataPoints(List<DataPoint> dataPoints) {
		// If no wrong point then 100% accuracy
		return wrongPoints;
	}

}
