package libsvm.extension;

import java.util.List;

import libsvm.core.Category;
import libsvm.core.CategoryCalculator;
import libsvm.core.Divider;
import libsvm.core.Machine.DataPoint;

public class MultiDividerBasedCategoryCalculator implements CategoryCalculator {

	private List<Divider> dividers;

	public MultiDividerBasedCategoryCalculator(final List<Divider> dividers) {
		this.dividers = dividers;
	}

	@Override
	public Category getCategory(final DataPoint dataPoint) {
		for (Divider divider : dividers) {
			if(divider.dataPointBelongTo(dataPoint, Category.NEGATIVE)){
				return Category.NEGATIVE;
			}
		}
		// The point satisfies all dividers
		return Category.POSITIVE;
	}

	public List<Divider> getDividers() {
		return dividers;
	}
	
	@Override
	public String toString() {
		return dividers.toString();
	}

}
