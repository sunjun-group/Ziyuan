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

	public Parameter setMachineType(final MachineType type) {
		param.svm_type = type.index();
		return this;
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

	public Parameter setKernelType(final KernelType type) {
		param.kernel_type = type.index();
		return this;
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

	public Parameter setDegree(final int degree) {
		ensureKernelTypeIn(KernelType.POLY);
		param.degree = degree;
		return this;
	}

	public int getDegree() {
		return param.degree;
	}

	public Parameter setGamma(final double gamma) {
		ensureKernelTypeIn(KernelType.POLY, KernelType.RBF, KernelType.SIGMOID);
		param.gamma = gamma;
		return this;
	}

	public double getGamma() {
		return param.gamma;
	}

	public Parameter setCoef0(final double coef0) {
		ensureKernelTypeIn(KernelType.POLY, KernelType.SIGMOID);
		param.coef0 = coef0;
		return this;
	}

	public double getCoef0() {
		return param.coef0;
	}

	/**
	 * @param cacheSize
	 *            Cache size in MB
	 */
	public Parameter setCacheSize(final double cacheSize) {
		param.cache_size = cacheSize;
		return this;
	}

	public double getCacheSize() {
		return param.cache_size;
	}

	/**
	 * Stopping criteria
	 */
	public Parameter setEps(final double eps) {
		param.eps = eps;
		return this;
	}

	public double getEps() {
		return param.eps;
	}

	public Parameter setC(final double c) {
		ensureMachineTypeIn(MachineType.C_SVC, MachineType.EPSILON_SVR, MachineType.NU_SVR);
		param.C = c;
		return this;
	}

	public double getC() {
		return param.C;
	}

	public Parameter setNrWeight(final int nrWeight) {
		ensureMachineTypeIn(MachineType.C_SVC);
		param.nr_weight = nrWeight;
		return this;
	}

	public int getNrWeight() {
		return param.nr_weight;
	}

	public Parameter setWeightLabel(final int[] weightLabel) {
		ensureMachineTypeIn(MachineType.C_SVC);
		param.weight_label = weightLabel;
		return this;
	}

	public int[] getWeightLabel() {
		return param.weight_label;
	}

	public Parameter setWeight(final double[] weight) {
		ensureMachineTypeIn(MachineType.C_SVC);
		param.weight = weight;
		return this;
	}

	public double[] getWeight() {
		return param.weight;
	}

	public Parameter setNU(final double nu) {
		ensureMachineTypeIn(MachineType.NU_SVC, MachineType.ONE_CLASS, MachineType.NU_SVR);
		param.nu = nu;
		return this;
	}

	public double getNU() {
		return param.nu;
	}

	public Parameter setP(final double p) {
		ensureMachineTypeIn(MachineType.EPSILON_SVR);
		param.p = p;
		return this;
	}

	public double getP() {
		return param.p;
	}

	/**
	 * Use the shrinking heuristics
	 */
	public Parameter setShrinking(final int shrinking) {
		param.shrinking = shrinking;
		return this;
	}

	public int getShrinking() {
		return param.shrinking;
	}

	/**
	 * Do probability estimates
	 */
	public Parameter setProbability(final int probability) {
		param.probability = probability;
		return this;
	}

	public int getProbability() {
		return param.probability;
	}
}
