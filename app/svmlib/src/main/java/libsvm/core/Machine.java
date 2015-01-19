package libsvm.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

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

	private svm_parameter parameter = null;
	private List<DataPoint> data = new ArrayList<DataPoint>();
	private svm_model model = null;
	private Map<Integer, Category> categoryMap = new HashMap<Integer, Category>();

	public Machine() {
		parameter = null;
		data = new ArrayList<DataPoint>();
		model = null;
		categoryMap = new HashMap<Integer, Category>();
	}

	public Machine reset() {
		return new Machine();
	}

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
		this.parameter.shrinking = parameter.getShrinking();
		this.parameter.probability = parameter.getProbability();
		return this;
	}

	public Machine addDataPoints(final List<DataPoint> dataPoints) {
		for (DataPoint point : dataPoints) {
			data.add(point);
			// TODO NPN should we deep copy here?
		}
		return this;
	}

	public Machine train() {
		Assert.assertNotNull("SVM parameters is not set.", parameter);
		Assert.assertTrue("SVM training data is empty.", !data.isEmpty());

		final svm_problem problem = new svm_problem();
		final int length = data.size();
		problem.l = length;
		problem.y = new double[length];
		problem.x = new svm_node[length][];

		for (int i = 0; i < length; i++) {
			final DataPoint point = data.get(i);
			problem.y[i] = getCategoryIndex(point.getCategory());
			problem.x[i] = getSvmNode(point);
		}

		model = svm.svm_train(problem, parameter);
		return this;
	}

	private svm_node[] getSvmNode(final DataPoint dp) {
		final int numberOfFeatures = dp.getNumberOfFeatures();
		final svm_node[] node = new svm_node[numberOfFeatures];
		for (int i = 0; i < numberOfFeatures; i++) {
			final svm_node svmNode = new svm_node();
			svmNode.index = i;
			svmNode.value = dp.getValue(i);
			node[i] = svmNode;
		}
		return node;
	}

	public Model getModel() {
		return new Model(model);
	}

	/**
	 * Get the Category instance for this Machine which is identified by the
	 * given categoryString from the Machine's cache. If such object was not
	 * defined yet, it will be added to the cache.
	 * 
	 * @param categoryString
	 *            The string to identify a Category
	 * @return A Category object identified by the given String. This method
	 *         never return null.
	 */
	public Category getCategory(final String categoryString) {
		// If the category does not exist yet, add it to the category map
		// Or return the instance otherwise
		for (Entry<Integer, Category> entry : categoryMap.entrySet()) {
			final Category existingCategory = entry.getValue();
			if (existingCategory.category.equals(categoryString)) {
				return existingCategory;
			}
		}
		final Category newCategory = new Category(categoryString);
		categoryMap.put(categoryMap.size() + 1, newCategory);
		return newCategory;
	}

	/**
	 * Get the index of the given Category in this Machine.
	 * 
	 * @param category
	 *            The category to check
	 * @return Index of the category or -1 if the category was not defined on
	 *         this machine
	 */
	public int getCategoryIndex(final Category category) {
		for (Entry<Integer, Category> entry : categoryMap.entrySet()) {
			if (entry.getValue().category.equals(category.category)) {
				return entry.getKey();
			}
		}
		return -1;
	}

	public class Category {
		private final String category;

		private Category(final String category) {
			this.category = category;
		}
	}

	public double[] getWeights() {
		Assert.assertNotNull("SVM parameters is not set.", parameter);
		Assert.assertNotNull("SVM model is not available yet.", model);

		// Weights are only available for linear SVMs
		if (parameter.svm_type != svm_parameter.C_SVC) {
			return null;
		}

		final int numberOfFeatures = data.get(0).getNumberOfFeatures();

		double[][] prob = new double[model.SV.length][numberOfFeatures];
		for (int i = 0; i < model.SV.length; i++) {
			for (int j = 0; j < numberOfFeatures; j++) {
				prob[i][j] = 0;
			}
		}
		for (int i = 0; i < model.SV.length; i++) {
			for (int j = 0; j < model.SV[i].length; j++) {
				prob[i][model.SV[i][j].index] = model.SV[i][j].value;
			}
		}

		double w_list[][][] = new double[model.nr_class][model.nr_class - 1][numberOfFeatures];

		for (int i = 0; i < numberOfFeatures; ++i) {
			for (int j = 0; j < model.nr_class - 1; ++j) {
				int index = 0;
				int end = 0;
				double acc;
				for (int k = 0; k < model.nr_class; ++k) {
					acc = 0.0;
					index += (k == 0) ? 0 : model.nSV[k - 1];
					end = index + model.nSV[k];
					for (int m = index; m < end; ++m) {
						acc += model.sv_coef[j][m] * prob[m][i];
					}
					w_list[k][j][i] = acc;
				}
			}
		}

		double[] weights = new double[numberOfFeatures];
		for (int i = 0; i < model.nr_class - 1; ++i) {
			for (int j = i + 1, k = i; j < model.nr_class; ++j, ++k) {
				for (int m = 0; m < numberOfFeatures; ++m) {
					weights[m] = (w_list[i][k][m] + w_list[j][i][m]);

				}
			}
		}
		return weights;
	}

	public Divider getExplicitDivider() {
		Assert.assertTrue("SVM training data is empty.", !data.isEmpty());
		Assert.assertNotNull("SVM model is not available yet.", model);

		int pSize = data.get(0).getNumberOfFeatures();
		double bias = 0;
		double[] wVec = new double[pSize];

		int[] sIndex = new int[model.nr_class];
		for (int i = 1; i < model.nr_class; i++) {
			sIndex[i] = sIndex[i - 1] + model.nSV[i - 1];
		}

		int p = 0;
		for (int i = 0; i < model.nr_class; i++) {
			for (int j = i + 1; j < model.nr_class; j++) {
				int si = sIndex[i];
				int sj = sIndex[j];
				double[] coef1 = model.sv_coef[j - 1];
				double[] coef2 = model.sv_coef[i];

				for (int k = 0; k < model.nSV[i]; k++) {
					for (int m = 0; m < pSize; m++) {
						wVec[m] += coef1[si + k] * model.SV[si + k][m].value;
					}
				}

				for (int k = 0; k < model.nSV[j]; k++) {
					for (int m = 0; m < pSize; m++) {
						wVec[m] += coef2[sj + k] * model.SV[sj + k][m].value;
					}
				}

				bias = model.rho[p];
				p++;
			}
		}

		return new Divider(wVec, bias);
	}

	public double getModelAccuracy() {
		Assert.assertNotNull("SVM model is not available yet.", model);
		int rightClassification = 0, wrongClassification = 0;
		for (DataPoint dp : data) {
			final double predictValue = svm.svm_predict(model, getSvmNode(dp));
			final int realValue = getCategoryIndex(dp.getCategory());
			if (predictValue > Integer.MAX_VALUE || predictValue < Integer.MIN_VALUE
					|| realValue != (double) predictValue) {
				wrongClassification++;
			} else {
				rightClassification++;
			}
		}
		Assert.assertTrue(rightClassification + wrongClassification == data.size());
		return (double) rightClassification / (double) data.size();
	}

}
