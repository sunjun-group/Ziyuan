package libsvm.core;

import java.security.InvalidParameterException;

import libsvm.core.Machine.Category;

/**
 * This class represents a data point to be used in SVM machine. It consists the
 * values of that data point and its classification/category.
 * 
 * @author Nguyen Phuoc Nguong Phuc (npn)
 * 
 */
public class DataPoint {

	private final int numberOfFeatures;
	private final double[] values;
	private Category category;

	public DataPoint(final int numberOfFeatures) {
		// Hide the constructor
		// I.e.: can only be created using Machine's factory method
		this.numberOfFeatures = numberOfFeatures;
		this.values = new double[this.numberOfFeatures];
	}

	public int getNumberOfFeatures() {
		return numberOfFeatures;
	}

	public void setCategory(final Category category) {
		this.category = category;
	}

	public Category getCategory() {
		return category;
	}

	public void setValues(final double... values) {
		for (int i = 0; i < values.length; i++) {
			if (i == numberOfFeatures) {
				break;
			}
			this.values[i] = values[i];
		}
	}

	public double getValue(final int index) {
		if (index >= numberOfFeatures) {
			throw new InvalidParameterException("Index must be less than " + numberOfFeatures);
		}
		return values[index];
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("[");
		boolean first = true;
		for (double val : values) {
			if (!first) {
				sb.append(", ");
				first = false;
			}
			sb.append(val);
		}
		sb.append("] : ").append(category);
		return sb.toString();
	}
}
