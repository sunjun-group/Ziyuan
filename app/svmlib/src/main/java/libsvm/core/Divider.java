package libsvm.core;

public class Divider {
	private final double[] thetas;
	private final double theta0;

	public Divider(double[] thetas, double theta0) {
		this.thetas = thetas;
		this.theta0 = theta0;
	}

	public String toString() {
		final StringBuilder dsb = new StringBuilder();
		dsb.append("[");
		for (double theta : thetas) {
			dsb.append("  ").append(theta);
		}
		dsb.append(", ").append(theta0).append("; ]");
		return dsb.toString();
	}
}
