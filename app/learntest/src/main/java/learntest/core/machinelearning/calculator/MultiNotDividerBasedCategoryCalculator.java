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
		//if satisfy all divider return negative
		return Category.NEGATIVE;
	}

	public List<Divider> getDividers() {
		return dividers;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("not divider:");
		for (Divider divider : dividers) {
			sb.append(divider+",");
		}
		return sb.toString();
	}

}
