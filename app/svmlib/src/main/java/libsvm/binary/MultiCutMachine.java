package libsvm.binary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import libsvm.svm_model;
import libsvm.core.DataPoint;
import libsvm.core.Machine;

import org.junit.Assert;

/**
 * SVM machine which will try to divide the given data points many times if
 * possible to increase accuracy.
 * 
 * @author Nguyen Phuoc Nguong Phuc (npn)
 * 
 */
public class MultiCutMachine extends Machine {

	private List<LearnedData> learnedDatas;

	public MultiCutMachine() {
		learnedDatas = new ArrayList<LearnedData>();
	}

	private class LearnedData {
		private svm_model model;
		private Set<Category> wrongSides = new HashSet<Category>();
	}

	@Override
	protected Machine train(final List<DataPoint> dataPoints) {
		List<DataPoint> trainingData = dataPoints;
		boolean stop = false;
		while (!stop) {
			super.train(trainingData);
			final List<DataPoint> wrongClassifications = getWrongClassifiedDataPoints(trainingData);

			LearnedData learnedData = new LearnedData();
			learnedData.model = model;
			Category wrongCategory = null; // Only valid if wrongCategories == 1
			for (DataPoint dp : wrongClassifications) {
				wrongCategory = dp.getCategory();
				learnedData.wrongSides.add(wrongCategory);
			}
			learnedDatas.add(learnedData);

			int wrongCategories = learnedData.wrongSides.size();
			if (wrongCategories == 1) {
				// Try to improve the result
				List<DataPoint> newTraninigData = new ArrayList<DataPoint>();
				for (DataPoint dp : trainingData) {
					// Points on the same side of the wrong classification
					final Category calculatedCategory = calculateCategory(dp, learnedData.model);
					if (calculatedCategory.equals(wrongCategory)) {
						newTraninigData.add(dp);
					}
				}
				trainingData = newTraninigData;
			} else {
				// There may still be inaccuracy but we cannot do anything more
				stop = true;
			}
		}

		return this;
	}

	@Override
	public Category calculateCategory(DataPoint dataPoint) {
		Assert.assertTrue("There is no learned data.", !learnedDatas.isEmpty());
		Category result = null;
		for (LearnedData data : learnedDatas) {
			result = calculateCategory(dataPoint, data.model);
			if (!data.wrongSides.contains(result)) {
				// Correct data found
				break;
			}
		}
		return result; // Can be null or incorrect
	}

}
