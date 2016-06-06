package learntest.calculator;

import java.util.List;

import libsvm.core.Category;
import libsvm.core.CategoryCalculator;
import libsvm.core.Machine.DataPoint;

public class OrCategoryCalculator implements CategoryCalculator {
	
	private List<List<CategoryCalculator>> calculators;
	
	public OrCategoryCalculator(List<List<CategoryCalculator>> calculators) {
		this.calculators = calculators;
	}

	@Override
	public Category getCategory(DataPoint dataPoint) {
		for (List<CategoryCalculator> list : calculators) {
			if (getCategory(dataPoint, list) == Category.POSITIVE) {
				return Category.POSITIVE;
			}
		}
		return Category.NEGATIVE;
	}
	
	private Category getCategory(DataPoint dataPoint, List<CategoryCalculator> list) {
		for (CategoryCalculator calculator : list) {
			if (calculator.getCategory(dataPoint) == Category.NEGATIVE) {
				return Category.NEGATIVE;
			}
		}
		return Category.POSITIVE;
	}

}
