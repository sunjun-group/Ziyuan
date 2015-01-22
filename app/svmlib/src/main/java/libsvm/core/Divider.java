package libsvm.core;

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

	public Divider(double[] thetas, double theta0) {
		this.thetas = thetas;
		this.theta0 = theta0;
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
}
