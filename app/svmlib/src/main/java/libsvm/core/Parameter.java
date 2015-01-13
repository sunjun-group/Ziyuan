package libsvm.core;

import libsvm.svm_parameter;

/**
 * Wrapper for svm_paramter class for easier usage.
 * 
 * @author Nguyen Phuoc Nguong Phuc (npn)
 * 
 */
public class Parameter {

	private svm_parameter param = new svm_parameter();

	public void setMachineType(final MachineType type) {
		param.svm_type = type.index();
	}

	public MachineType getMachineType() {
		return MachineType.of(param.svm_type);
	}

	private boolean isMachineTypeIn(final MachineType... types) {
		for (MachineType type : types) {
			if (param.kernel_type == type.index()) {
				return true;
			}
		}
		return false;
	}

	private void ensureMachineTypeIn(final MachineType... types) {
		if (!isMachineTypeIn(types)) {
			throw new UnsupportedOperationException(
					"This operation is not supported for kernel type "
							+ MachineType.of(param.kernel_type));
		}
	}

	public void setKernelType(final KernelType type) {
		param.kernel_type = type.index();
	}

	public KernelType getKernelType() {
		return KernelType.of(param.kernel_type);
	}

	private boolean isKernelTypeIn(final KernelType... types) {
		for (KernelType type : types) {
			if (param.kernel_type == type.index()) {
				return true;
			}
		}
		return false;
	}

	private void ensureKernelTypeIn(final KernelType... types) {
		if (!isKernelTypeIn(types)) {
			throw new UnsupportedOperationException(
					"This operation is not supported for kernel type "
							+ KernelType.of(param.kernel_type));
		}
	}

	public void setDegree(final int degree) {
		ensureKernelTypeIn(KernelType.POLY);
		param.degree = degree;
	}

	public int getDegree() {
		return param.degree;
	}

	public void setGamma(final double gamma) {
		ensureKernelTypeIn(KernelType.POLY, KernelType.RBF, KernelType.SIGMOID);
		param.gamma = gamma;
	}

	public double getGamma() {
		return param.gamma;
	}

	public void setCoef0(final double coef0) {
		ensureKernelTypeIn(KernelType.POLY, KernelType.SIGMOID);
		param.coef0 = coef0;
	}

	public double getCoef0() {
		return param.coef0;
	}

	/**
	 * @param cacheSize
	 *            Cache size in MB
	 */
	public void setCacheSize(final double cacheSize) {
		param.cache_size = cacheSize;
	}

	public double getCacheSize() {
		return param.cache_size;
	}

	/**
	 * Stopping criteria
	 */
	public void setEps(final double eps) {
		param.eps = eps;
	}

	public double getEps() {
		return param.eps;
	}

	public void setC(final double c) {
		ensureMachineTypeIn(MachineType.C_SVC, MachineType.EPSILON_SVR, MachineType.NU_SVR);
		param.C = c;
	}

	public double getC() {
		return param.C;
	}

	public void setNrWeight(final int nrWeight) {
		ensureMachineTypeIn(MachineType.C_SVC);
		param.nr_weight = nrWeight;
	}

	public int getNrWeight() {
		return param.nr_weight;
	}

	public void setWeightLabel(final int[] weightLabel) {
		ensureMachineTypeIn(MachineType.C_SVC);
		param.weight_label = weightLabel;
	}

	public int[] getWeightLabel() {
		return param.weight_label;
	}

	public void setWeight(final double[] weight) {
		ensureMachineTypeIn(MachineType.C_SVC);
		param.weight = weight;
	}

	public double[] getWeight() {
		return param.weight;
	}

	public void setNU(final double nu) {
		ensureMachineTypeIn(MachineType.NU_SVC, MachineType.ONE_CLASS, MachineType.NU_SVR);
		param.nu = nu;
	}

	public double getNU() {
		return param.nu;
	}

	public void setP(final double p) {
		ensureMachineTypeIn(MachineType.EPSILON_SVR);
		param.p = p;
	}

	public double getP() {
		return param.p;
	}

	/**
	 * Use the shrinking heuristics
	 */
	public void setShrinking(final int shrinking) {
		param.shrinking = shrinking;
	}

	public int getShrinking() {
		return param.shrinking;
	}

	/**
	 * Do probability estimates
	 */
	public void setProbability(final int probability) {
		param.probability = probability;
	}

	public int getProbability() {
		return param.probability;
	}
}
