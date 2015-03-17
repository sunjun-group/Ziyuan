package libsvm.core;

import libsvm.core.Machine.DataPoint;


public interface CategoryCalculator {

	/**
	 * Calculate then return the Category of the given data point.
	 * 
	 * @param dataPoint
	 *            Data point to calculate
	 * @return Either Category.POSITIVE or Category.NEGATIVE. This method should
	 *         never return null.
	 */
	Category getCategory(DataPoint dataPoint);

}
