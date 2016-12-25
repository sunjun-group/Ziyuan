package libsvm.core;

import libsvm.core.Machine.DataPoint;
import sav.common.core.utils.Assert;

public class DividerBasedCategoryCalculator implements CategoryCalculator {

	private final Divider divider;
	
	public DividerBasedCategoryCalculator(final Divider divider) {
		Assert.assertNotNull(divider, "Divider cannot be null.");
		this.divider = divider;
	}
	
	@Override
	public Category getCategory(DataPoint dataPoint) {
		Assert.assertNotNull(dataPoint, "Data point cannot be null.");
		// Calculate the category based on the existing Divider
		return divider.getCategory(dataPoint);
	}

}
