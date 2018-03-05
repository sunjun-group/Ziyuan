package libsvm.extension;

import java.util.List;

import libsvm.core.Category;
import libsvm.core.CategoryCalculator;
import libsvm.core.Divider;
import libsvm.core.Machine.DataPoint;

public class MultiOrDividerBasedCategoryCalculator implements CategoryCalculator {

	private List<Divider> dividers;

	public MultiOrDividerBasedCategoryCalculator(final List<Divider> dividers) {
		this.dividers = dividers;
	}

	@Override
	public Category getCategory(final DataPoint dataPoint) {
		for (Divider divider : dividers) {
			if(divider.dataPointBelongTo(dataPoint, Category.POSITIVE)){
				return Category.POSITIVE; // if satify one divider, return positive
			}
		}
		return Category.NEGATIVE;
	}

	public List<Divider> getDividers() {
		return dividers;
	}
	
	@Override
	public String toString() {
		return dividers.toString();
	}

}
