package learntest.core.machinelearning.calculator;

import java.util.List;

import libsvm.core.Category;
import libsvm.core.CategoryCalculator;
import libsvm.core.Divider;
import libsvm.core.Machine.DataPoint;

public class MultiNotDividerBasedCategoryCalculator implements CategoryCalculator{

	private List<Divider> dividers;
	
	public MultiNotDividerBasedCategoryCalculator(List<Divider> dividers) {
		this.dividers = dividers;
	}
	
	@Override
	public Category getCategory(DataPoint dataPoint) {
		for (Divider divider : dividers) {
			if (divider.dataPointBelongTo(dataPoint, Category.NEGATIVE)) {
				return Category.POSITIVE;
			}
		}
		return Category.NEGATIVE;
	}

	public List<Divider> getDividers() {
		return dividers;
	}

}
