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
		Assert.assertTrue("There must be exactly 2 categories defined for a BinaryMachine.",
				2 == countAvailableCategories());

		List<DataPoint> trainingData = dataPoints;
		boolean stop = false;
		while (!stop) {
			super.train(trainingData);
			final List<DataPoint> wrongClassifications = getWrongClassifiedDataPoints();

			LearnedData learnedData = new LearnedData();
			learnedData.model = model;
			for (DataPoint dp : wrongClassifications) {
				learnedData.wrongSides.add(dp.getCategory());
			}
			learnedDatas.add(learnedData);

			int wrongCategories = learnedData.wrongSides.size();
			if (wrongCategories == 1) {
				// Try to improve the result
				trainingData = wrongClassifications;
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
