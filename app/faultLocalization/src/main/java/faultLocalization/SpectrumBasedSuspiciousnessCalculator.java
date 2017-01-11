package faultLocalization;

/**
 * @author Nguyen Phuoc Nguong Phuc (npn)
 *
 */
public class SpectrumBasedSuspiciousnessCalculator{

	public enum SpectrumAlgorithm {
		JACCARD, TARANTULA, OCHIAI
	};

	private double numberOfFailCoverTests = 0.0; // a11
	private double numberOfFailTest = 0.0; // a11 + a01
	private double numberOfPassCoverTests = 0.0; // a10
	private double numberOfPassTests = 0.0; // a10 + a00
	private SpectrumAlgorithm algorithm;
	
	public SpectrumBasedSuspiciousnessCalculator(int numberOfPassTests, int numberOfFailTest,
			int numberOfPassCoverTests, int numberOfFailCoverTests, SpectrumAlgorithm algorithm){
		this.numberOfPassTests = numberOfPassTests;
		this.numberOfFailTest = numberOfFailTest;
		this.numberOfPassCoverTests = numberOfPassCoverTests;
		this.numberOfFailCoverTests = numberOfFailCoverTests;
		this.algorithm = algorithm;
	}
	
	public double compute() {
		double result = 0.0;
		if (SpectrumAlgorithm.JACCARD == algorithm) {
			// sJ = a11 / (a11 + a01 + a10)
			result = numberOfFailCoverTests / (numberOfFailTest + numberOfPassCoverTests);
		} else if (SpectrumAlgorithm.OCHIAI == algorithm) {
			// sO = a11 / sqrl[(a11 + a01) * (a11 + a10)]
			result = numberOfFailCoverTests / Math.sqrt(numberOfFailTest * (numberOfFailCoverTests + numberOfPassCoverTests));
		} else {
			// TARANTULA
			// sT = [a11/(a11+a01)] / {[a11/(a11+a01)] + [a10/(a10+a00)]}
			if(numberOfFailTest == 0){
				return 1;
			}
			double failedRate = numberOfFailCoverTests / numberOfFailTest;
			
			double passRate = (numberOfPassTests == 0)? 0: numberOfPassCoverTests / numberOfPassTests;

			result = failedRate / (passRate + failedRate);
		}
		return result;
	}
}
