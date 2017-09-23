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

	public Parameter() {
		// Default cache size
		// Higher value is recommended if more RAM is available
		param.cache_size = 200.0;
		// Default C is 1
		// If there are a lot of noisy observations this should be decreased
		// It corresponds to regularize more the estimation.
		param.C = 1.0;
		// By default do not use weights
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];
	}
	
	public Parameter(final svm_parameter param) {
		this.param = param;
	}

	public Parameter setMachineType(final MachineType type) {
		param.svm_type = type.index();
		return this;
	}

	public MachineType getMachineType() {
		return MachineType.of(param.svm_type);
	}

	private boolean isMachineTypeIn(final MachineType... types) {
		for (MachineType type : types) {
			if (param.svm_type == type.index()) {
//			if (param.kernel_type == type.index()) {
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

	/**
	 * Set degree used in Polynomial kernel function.
	 * 
	 * @param degree
	 *            The degree to set
	 * @return The current parameter object
	 * @see KernelType
	 */
	public Parameter setDegree(final int degree) {
		ensureKernelTypeIn(KernelType.POLY);
		param.degree = degree;
		return this;
	}

	public int getDegree() {
		return param.degree;
	}

	/**
	 * Set value for the parameter gamma for Polynomial/RBF/Sigmoid kernel
	 * function, which defines how much influence a single training example has.
	 * The larger gamma is, the closer other examples must be to be affected.
	 * 
	 * @param gamma
	 *            Value for the gamma in SVM algorithm
	 * @return The current parameter object
	 * @see KernelType
	 */
	public Parameter setGamma(final double gamma) {
		ensureKernelTypeIn(KernelType.POLY, KernelType.RBF, KernelType.SIGMOID);
		param.gamma = gamma;
		return this;
	}

	public double getGamma() {
		return param.gamma;
	}

	/**
	 * Set value r for Polynomial/Sigmoid kernel function.
	 * 
	 * @param coef0
	 *            Value of r to set
	 * @return The current parameter object
	 * @see KernelType
	 */
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
	 * Set stopping criteria, the larger this value is, the more loosely the
	 * optimization problem will be solved.
	 */
	public Parameter setEps(final double eps) {
		param.eps = eps;
		return this;
	}

	public double getEps() {
		return param.eps;
	}

	/**
	 * Set value for C, the parameter defines the trades off misclassification
	 * of training examples against simplicity of the decision surface. A low C
	 * makes the decision surface smooth, while a high C aims at classifying all
	 * training examples correctly.
	 * 
	 * @param c
	 *            value of C for the SVM algorithm
	 * @return The current parameter object
	 */
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

	/**
	 * Set value for the parameter nu in NuSVC/OneClassSVM/NuSVR, which
	 * approximates the fraction of training errors and support vectors.
	 * 
	 * @param nu
	 *            Value for NU
	 * @return Current parameter object
	 */
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
	 * Specify to use the shrinking heuristics or not. <br/>
	 * If the number of iterations is large, shrinking can shorten the training
	 * time. However, if we <i>loosely solve the optimization problem</i> (i.e.:
	 * by setting large stopping tolerance eps), it may be faster <i>not to
	 * use</i> shrinking. <br/>
	 * That is, because of the small number of iterations, the time spent on all
	 * decomposition iterations can be even less than one single gradient
	 * reconstruction.
	 */
	public Parameter setUseShrinking(final boolean doShrinking) {
		param.shrinking = doShrinking ? 1 : 0;
		return this;
	}

	public boolean isUseShrinking() {
		return param.shrinking != 0;
	}

	/**
	 * Specify whether probability estimates should be performed
	 */
	public Parameter setPredictProbability(final boolean probability) {
		param.probability = probability ? 1 : 0;
		return this;
	}

	public boolean isPredictProbability() {
		return param.probability != 1;
	}
}
