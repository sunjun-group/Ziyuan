package libsvm.core;

import java.util.Arrays;

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
		final double[] roundedLinearExpr = new CoefficientProcessing().process(this.getLinearExpr());
		final int lastIndex = roundedLinearExpr.length - 1;
		final double[] roundedThetas = Arrays.copyOf(roundedLinearExpr, roundedLinearExpr.length - 1);
		
		return new Divider(roundedThetas, roundedLinearExpr[lastIndex], true);
	}

	/**
	 * Based on the divider, we compute the category of dataPoint and compare with category
	 * However, here, if the value is zero, then we classify this datapoint as either negative or positive
	 * This function should be called during computing the accuracy of the divider
	 */
	public boolean dataPointBelongTo(DataPoint dataPoint, Category category){
		double value = computeValueOfDataPoint(dataPoint);
		//return (Double.compare(value, 0) == 0) || (Category.fromDouble(value) == category);
		return Category.fromDouble(value) == category;
	}
	
	public Category getCategory(final DataPoint dataPoint) {
		double value = computeValueOfDataPoint(dataPoint);
		return Category.fromDouble(value);
	}

	private double computeValueOfDataPoint(final DataPoint dataPoint) {
		double value = 0.0;
		for (int i = 0; i < dataPoint.getNumberOfFeatures(); i++) {
			if (i < thetas.length) {
				value += thetas[i] * dataPoint.getValue(i);
			}
		}
		value -= theta0;
		return value;
	}
	
	public CategoryCalculator getCategoryCalculator() {
		return new DividerBasedCategoryCalculator(this);
	}
	
	public double[] getLinearExpr() {
		//the last element is the theta0
		double[] linearExpr = Arrays.copyOf(thetas, thetas.length + 1);
		linearExpr[linearExpr.length - 1] = theta0;
		return linearExpr;
	}

	public boolean isRounded() {
		return isRounded;
	}
	
	
}
