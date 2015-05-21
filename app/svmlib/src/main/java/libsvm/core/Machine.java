package libsvm.core;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

import org.apache.log4j.Logger;
import org.junit.Assert;

/**
 * This class represents an SVM machine. After initialization, it is possible to
 * set the parameters of the machine and add data points to it so that the
 * machine can learn from the data points later.
 * 
 * @author Nguyen Phuoc Nguong Phuc (npn)
 * 
 */
public class Machine {
	private static final Logger LOGGER = Logger.getLogger(Machine.class);
	private static final String DEFAULT_FEATURE_PREFIX = "x";

	private svm_parameter parameter = null;
	private List<DataPoint> data = new ArrayList<DataPoint>();
	protected svm_model model = null;
	private List<String> dataLabels = new ArrayList<String>();
	private boolean isGeneratedDataLabel = false;
	private boolean isDataClean = false;

	public Machine setParameter(final Parameter parameter) {
		this.parameter = new svm_parameter();
		if (parameter.getMachineType() != null) {
			this.parameter.svm_type = parameter.getMachineType().index();
		}
		if (parameter.getKernelType() != null) {
			this.parameter.kernel_type = parameter.getKernelType().index();
		}
		this.parameter.degree = parameter.getDegree();
		this.parameter.gamma = parameter.getGamma();
		this.parameter.coef0 = parameter.getCoef0();
		this.parameter.cache_size = parameter.getCacheSize();
		this.parameter.eps = parameter.getEps();
		this.parameter.C = parameter.getC();
		this.parameter.nr_weight = parameter.getNrWeight();
		this.parameter.weight_label = parameter.getWeightLabel();
		this.parameter.weight = parameter.getWeight();
		this.parameter.nu = parameter.getNU();
		this.parameter.p = parameter.getP();
		this.parameter.shrinking = parameter.isUseShrinking() ? 1 : 0;
		this.parameter.probability = parameter.isPredictProbability() ? 1 : 0;
		return this;
	}

	/**
	 * Set the labels for the features. This will also determine the number of
	 * features of the {@link DataPoint} to be produced by this Machine.
	 * 
	 * @param dataLabels
	 *            List of labels for the features to be watched.
	 * @return The configured Machine.
	 */
	public Machine setDataLabels(final List<String> dataLabels) {
		this.dataLabels = dataLabels;
		this.isGeneratedDataLabel = false;
		return this;
	}

	public boolean isGeneratedDataLabels() {
		return this.isGeneratedDataLabel;
	}

	public List<String> getDataLabels() {
		return dataLabels;
	}

	/**
	 * Set the number of features to be watched. This will also set the labels
	 * for the features as ["x0", "x1", ..., "x{NumberOfFeature-1}"].
	 * 
	 * @param numberOfFeatures
	 *            The number of features to set.
	 * @return The configured Machine.
	 */
	public Machine setNumberOfFeatures(final int numberOfFeatures) {
		this.dataLabels = new ArrayList<String>(numberOfFeatures);
		for (int i = 0; i < numberOfFeatures; i++) {
			this.dataLabels.add(DEFAULT_FEATURE_PREFIX + i);
		}
		this.isGeneratedDataLabel = true;
		return this;
	}

	public int getNumberOfFeatures() {
		return this.dataLabels.size();
	}

	public Machine resetData() {
		data = new ArrayList<DataPoint>();
		return this;
	}

	public Machine addDataPoints(final List<DataPoint> dataPoints) {
		for (DataPoint point : dataPoints) {
			addDataPoint(point);
		}
		return this;
	}

	public Machine addDataPoint(final DataPoint dataPoint) {
		// TODO NPN should we deep copy here?
		data.add(dataPoint);
		return this;
	}

	public Machine addDataPoint(final Category category, final double... values) {
		final int numberOfFeatures = getNumberOfFeatures();
		Assert.assertTrue("Must specify " + numberOfFeatures + " items as values.", values != null
				&& values.length == numberOfFeatures);
		final DataPoint dp = new DataPoint(numberOfFeatures);
		dp.setCategory(category);
		dp.setValues(values);
		this.addDataPoint(dp);
		return this;
	}

	/**
	 * Train the current machine using the preset parameters and data.
	 * <p>
	 * <b>Preconditions</b>: The parameters and data are set.
	 * </p>
	 * 
	 * @return The instance of the current machine after training completed.
	 */
	public final Machine train() {
		Assert.assertNotNull("SVM parameters is not set.", parameter);
		Assert.assertTrue("SVM training data is empty.", !data.isEmpty());

		this.data = cleanUp(data);
		if (getNumberOfFeatures() <= 0) {
			LOGGER.warn("The feature list is empty. SVM will not run.");
			return this;
		}

		train(data);
		return this;
	}

	/**
	 * Train the current machine using the preset parameters and the given data.
	 * <p>
	 * <b>Preconditions</b>: The parameters are set.
	 * </p>
	 * 
	 * @param dataPoints
	 *            The data used to learn.
	 * @return The instance of the current machine after training completed.
	 */
	protected Machine train(final List<DataPoint> dataPoints) {
		Assert.assertNotNull("SVM parameters is not set.", parameter);
		Assert.assertTrue("SVM training data is empty.", !dataPoints.isEmpty());

		final svm_problem problem = new svm_problem();
		final int length = dataPoints.size();
		problem.l = length;
		problem.y = new double[length];
		problem.x = new svm_node[length][];

		for (int i = 0; i < length; i++) {
			final DataPoint point = dataPoints.get(i);
			problem.y[i] = point.getCategory().intValue();
			problem.x[i] = point.getSvmNode();
		}

		model = svm.svm_train(problem, parameter);
		return this;
	}

	/**
	 * Remove duplicated features. I.e.: to remove the features which have same
	 * value for all data points. Such features cannot provide any useful
	 * information.
	 * 
	 */
	private List<DataPoint> cleanUp(final List<DataPoint> dataPoints) {
		if (this.isDataClean) {
			return dataPoints;
		}

		// Find the redundant features list
		final int originalSize = getNumberOfFeatures();
		final List<Integer> indexesToRemove = new ArrayList<Integer>(originalSize);
		for (int i = 0; i < originalSize; i++) {
			if (isRedundantFeature(i, dataPoints)) {
				indexesToRemove.add(i);
			}
		}

		final int cleanedSize = originalSize - indexesToRemove.size();
		if (originalSize != cleanedSize) {
			// Clean up data labels, this will also correct getNumberOfFeatures
			final List<String> cleanedDataLabel = new ArrayList<String>(cleanedSize);
			for (int i = 0; i < originalSize; i++) {
				if (!indexesToRemove.contains(Integer.valueOf(i))) {
					cleanedDataLabel.add(this.dataLabels.get(i));
				}
			}
			// NOTE: I don't call setDataLabels here to avoid messing with the
			// property isGeneratedDataLabel
			this.dataLabels = cleanedDataLabel;

			// Clean up data points
			for (DataPoint dp : dataPoints) {
				dp.numberOfFeatures = cleanedSize;
				final double[] cleanedValues = new double[cleanedSize];
				int index = 0;
				for (int i = 0; i < originalSize; i++) {
					if (!indexesToRemove.contains(Integer.valueOf(i))) {
						cleanedValues[index++] = dp.values[i];
					}
				}
				dp.values = cleanedValues;
			}

			LOGGER.info("Reduced feature size from " + originalSize + " to " + cleanedSize);
		}

		this.isDataClean = true;

		return dataPoints;
	}

	private boolean isRedundantFeature(final int featureIndex, final List<DataPoint> dataPoints) {
		double value = dataPoints.get(0).getValue(featureIndex);
		for (int i = 1; i < dataPoints.size(); i++) {
			if (dataPoints.get(i).getValue(featureIndex) != value) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Get the learned model.
	 * 
	 * @return The learned model or <code>null</code> if the learning process
	 *         was not completed.
	 */
	protected Model getModel() {
		return model == null || data == null || data.size() <= 0 ? null : new Model(model, data
				.get(0).getNumberOfFeatures());
	}

	protected List<DataPoint> getWrongClassifiedDataPoints(final List<DataPoint> dataPoints) {
		return getWrongClassifiedDataPoints(dataPoints, new ModelBasedCategoryCalculator(model));
	}

	protected List<DataPoint> getWrongClassifiedDataPoints(final List<DataPoint> dataPoints,
			final CategoryCalculator calculator) {
		final List<DataPoint> wrong = new ArrayList<DataPoint>();
		for (DataPoint dp : dataPoints) {
			if (!dp.getCategory().equals(calculator.getCategory(dp))) {
				wrong.add(dp);
			}
		}
		return wrong;
	}

	public double getModelAccuracy() {
		if (model == null) {
			return 0.0;
		}
		return 1.0 - ((double) getWrongClassifiedDataPoints(data).size() / data.size());
	}

	public String getLearnedLogic() {
		// Print out the learned logic
		// I.e.: the predicate about the POSITIVE points
		// This basic machine only produces 1 divider so the logic is in the
		// form of a1*x1 + a2*x2 + ... + an*xn >= b
		StringBuilder str = new StringBuilder();

		Model currentModel = getModel();
		if (currentModel != null) {
			str.append(getLearnedLogic(currentModel.getExplicitDivider(), getRandomData()));
		}

		return str.toString();
	}

	protected String getLearnedLogic(final Divider divider, final DataPoint sampleDataPoint) {
		StringBuilder str = new StringBuilder();
		CoefficientProcessing coefficientProcessing = new CoefficientProcessing();
		double[] thetas = coefficientProcessing.process(divider.getLinearExpr());

		for (int i = 0; i < thetas.length - 1; i++) {
			if (Double.compare(thetas[i], 0) == 0) {
				continue;
			}

			if (str.length() > 0 && thetas[i] >= 0) {
				str.append(" + ");
			}

			if (thetas[i] < 0) {
				str.append(" ");
			}

			str.append(thetas[i]);
			str.append("*");
			str.append(dataLabels.get(i));
		}
		str.append(" >= ");
		str.append(thetas[thetas.length - 1]);

		return str.toString();
	}

	protected DataPoint getRandomData() {
		return data.get(0);
	}

	/**
	 * This class represents a data point to be used in SVM machine. It consists
	 * the values of that data point and its classification/category.
	 * 
	 * @author Nguyen Phuoc Nguong Phuc (npn)
	 * 
	 */
	public class DataPoint {

		private int numberOfFeatures;
		private double[] values;
		private Category category;

		private DataPoint(final int numberOfFeatures) {
			// Hide the constructor
			// I.e.: can only be created using Machine's factory method
			this.numberOfFeatures = numberOfFeatures;
			this.values = new double[this.numberOfFeatures];
		}

		public int getNumberOfFeatures() {
			return numberOfFeatures;
		}

		public void setCategory(final Category category) {
			this.category = category;
		}

		public Category getCategory() {
			return category;
		}

		public void setValues(final double... values) {
			if (values.length != numberOfFeatures) {
				throw new InvalidParameterException("The array values must have exactly "
						+ numberOfFeatures + " number of elements");
			}
			for (int i = 0; i < values.length; i++) {
				this.values[i] = values[i];
			}
		}

		public double getValue(final int index) {
			if (index >= numberOfFeatures) {
				throw new InvalidParameterException("Index must be less than " + numberOfFeatures);
			}
			return values[index];
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}

			if (obj instanceof DataPoint) {
				DataPoint other = (DataPoint) obj;
				if (other.numberOfFeatures != numberOfFeatures || !other.category.equals(category)) {
					return false;
				} else {
					for (int i = 0; i < values.length; i++) {
						if (values[i] != other.values[i]) {
							return false;
						}
					}
					return true;
				}
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 31 * hash + numberOfFeatures;
			hash = 31 * hash + (category == null ? 0 : category.hashCode());
			hash = 31 * hash + (values == null ? 0 : values.hashCode());
			return hash;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append(Arrays.toString(values));
			sb.append(" : ").append(category);
			return sb.toString();
		}

		public svm_node[] getSvmNode() {
			final int numberOfFeatures = this.getNumberOfFeatures();
			final svm_node[] node = new svm_node[numberOfFeatures];
			for (int i = 0; i < numberOfFeatures; i++) {
				final svm_node svmNode = new svm_node();
				svmNode.index = i;
				svmNode.value = this.getValue(i);
				node[i] = svmNode;
			}
			return node;
		}
	}

}
