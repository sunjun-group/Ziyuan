package libsvm.extension;

import java.util.ArrayList;
import java.util.List;

import libsvm.svm_model;
import libsvm.core.Category;
import libsvm.core.CategoryCalculator;
import libsvm.core.Model;
import libsvm.core.Machine.DataPoint;

public class MultiModelBasedCategoryCalculator implements CategoryCalculator {

	private List<svm_model> learnedModels = new ArrayList<svm_model>();
	
	public MultiModelBasedCategoryCalculator(List<svm_model> models) {
		this.learnedModels = models;
	}
	
	@Override
	public Category getCategory(final DataPoint dataPoint) {
		// Loop on learned models
		// If the category is NEGATIVE, return it
		// Else keep looping
		// If the point passed all learned model, return POSITIVE
		for (svm_model model : learnedModels) {
			if (Category.NEGATIVE == Model.getCategoryCalculator(model).getCategory(dataPoint)) {
				return Category.NEGATIVE;
			}
		}
		// The point satisfies all dividers
		return Category.POSITIVE;
	}

}
