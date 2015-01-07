package icsetlv.svm;

public class divider {
	double[] thetas;
	double theta0;

	public divider(double[] tts, double tt0) {
		this.thetas = tts;
		this.theta0 = tt0;
	}

	public String toString() {
		StringBuilder dsb = new StringBuilder();
		dsb.append("[");
		for (double theta : thetas) {
			dsb.append("  ").append(theta);
		}
		dsb.append(", ").append(theta0).append("; ]");
		return dsb.toString();
	}
}
