package libsvm.core;

import libsvm.core.Machine.DataPoint;

/**
 * Represents a way to divide a set of {@link DataPoint} into two separated sets
 * with two different characteristics. It is a 'line' specified by: <br/>
 * thetas[0]*x0 + thetas[1]*x1 + ... + thetas[n]*xn = theta0
 * 
 * @author Nguyen Phuoc Nguong Phuc (npn)
 * 
 */
public class Divider {
	private final double[] thetas;
	private final double theta0;
	// prevent rounding algorithm to be called multiple times
	private final boolean isRounded;

	public Divider(double[] thetas, double theta0) {
		this(thetas, theta0, false);
	}

	public Divider(double[] thetas, double theta0, boolean isRounded) {
		this.thetas = thetas;
		this.theta0 = theta0;
		this.isRounded = isRounded;
	}

	public String toString() {
		// If the function is a*x0 + b*x1 + c*x2 = d
		// Then the string representation will be [a b c : d]
		final StringBuilder dsb = new StringBuilder();
		dsb.append("[");
		for (double theta : thetas) {
			dsb.append("  ").append(theta);
		}
		dsb.append(" : ").append(theta0).append("]");
		return dsb.toString();
	}

	public double[] getThetas() {
		return thetas;
	}

	public double getTheta0() {
		return theta0;
	}

	public double valueOf(DataPoint dataPoint) {
		double result = 0;
		for (int i = 0; i < thetas.length; i++) {
			result += thetas[i] * dataPoint.getValue(i);
		}
		return result;
	}

	public Divider round() {
		if (this.isRounded) {
			return this;
		}
		final double[] roundedAllThetas = new CoefficientProcessing().process(this);
		final int lastIndex = roundedAllThetas.length - 1;
		final double[] roundedThetas = new double[lastIndex];
		for (int i = 0; i < lastIndex; i++) {
			roundedThetas[i] = roundedAllThetas[i];
		}
		return new Divider(roundedThetas, roundedAllThetas[lastIndex], true);
	}

	public Category getCategory(final DataPoint dataPoint) {
		double value = 0.0;
		for (int i = 0; i < dataPoint.getNumberOfFeatures(); i++) {
			if (i < thetas.length) {
				value += thetas[i] * dataPoint.getValue(i);
			}
		}
		value -= theta0;
		return Category.fromDouble(value);
	}
	
	public CategoryCalculator getCategoryCalculator() {
		return new DividerBasedCategoryCalculator(this);
	}
}
