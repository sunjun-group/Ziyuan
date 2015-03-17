package libsvm.core;

import libsvm.core.Machine.DataPoint;

import org.junit.Assert;

public class DividerBasedCategoryCalculator implements CategoryCalculator {

	private final Divider divider;
	
	public DividerBasedCategoryCalculator(final Divider divider) {
		Assert.assertNotNull("Divider cannot be null.", divider);
		this.divider = divider;
	}
	
	@Override
	public Category getCategory(DataPoint dataPoint) {
		Assert.assertNotNull("Data point cannot be null.", dataPoint);
		// Calculate the category based on the existing Divider
		return divider.getCategory(dataPoint);
	}

}
