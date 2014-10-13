package faultLocalization;

/**
 * @author Nguyen Phuoc Nguong Phuc (npn)
 *
 */
public class SuspiciousnessCalculator {

	public enum SuspiciousnessCalculationAlgorithm {
		JACCARD, TARANTULA, OCHIAI
	};

	private double coveredAndFailed = 0.0; // a11
	private double failed = 0.0; // a11 + a01
	private double coveredAndPassed = 0.0; // a10
	private double passed = 0.0; // a10 + a00

	public double getSuspiciousness(final SuspiciousnessCalculationAlgorithm algorithm) {
		double result = 0.0;
		if (SuspiciousnessCalculationAlgorithm.JACCARD == algorithm) {
			// sJ = a11 / (a11 + a01 + a10)
			result = coveredAndFailed / (failed + coveredAndPassed);
		} else if (SuspiciousnessCalculationAlgorithm.OCHIAI == algorithm) {
			// sO = a11 / sqrl[(a11 + a01) * (a11 + a10)]
			result = coveredAndFailed / Math.sqrt(failed * (coveredAndFailed + coveredAndPassed));
		} else {
			// TARANTULA
			// sT = [a11/(a11+a01)] / {[a11/(a11+a01)] + [a10/(a10+a00)]}
			double failedRate = coveredAndFailed / failed;
			double passRate = coveredAndPassed / passed;

			result = failedRate / (passRate + failedRate);
		}
		return result;
	}

	public void setCoveredAndFailed(double coveredAndFailed) {
		this.coveredAndFailed = coveredAndFailed;
	}

	public void setFailed(double failed) {
		this.failed = failed;
	}

	public void setCoveredAndPassed(double coveredAndPassed) {
		this.coveredAndPassed = coveredAndPassed;
	}

	public void setPassed(double passed) {
		this.passed = passed;
	}

}
