package libsvm.core;

import java.security.InvalidParameterException;

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
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof DataPoint) {
			DataPoint other = (DataPoint) obj;
			if (other.numberOfFeatures != numberOfFeatures || !other.category.equals(category)) {
				return false;
			} else {
				for (int i = 0; i < values.length; i++) {
					if (values[i] != other.values[i]) {
						return false;
					}
				}
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + numberOfFeatures;
		hash = 31 * hash + (category == null ? 0 : category.hashCode());
		hash = 31 * hash + (values == null ? 0 : values.hashCode());
		return hash;
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
