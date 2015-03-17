package libsvm.core;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.core.Machine.DataPoint;

import org.junit.Assert;

public class ModelBasedCategoryCalculator implements CategoryCalculator {
	private final svm_model rawModel;

	public ModelBasedCategoryCalculator(final svm_model model) {
		this.rawModel = model;
	}

	@Override
	public Category getCategory(DataPoint dataPoint) {
		Assert.assertNotNull("Data point cannot be null.", dataPoint);
		Assert.assertNotNull("SVM model is not ready yet.", rawModel);
		final double predictValue = svm.svm_predict(rawModel, dataPoint.getSvmNode());
		return Category.fromDouble(predictValue);
	}

}
