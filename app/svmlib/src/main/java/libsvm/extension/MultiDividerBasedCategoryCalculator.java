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
			if (Category.NEGATIVE == divider.getCategory(dataPoint)) {
				return Category.NEGATIVE;
			}
		}
		// The point satisfies all dividers
		return Category.POSITIVE;
	}

}
