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
		train(data);
		return this;
	}

	private Machine train(final List<DataPoint> dataPoints) {
		Assert.assertNotNull("SVM parameters is not set.", parameter);
		Assert.assertTrue("SVM training data is empty.", !dataPoints.isEmpty());

		final svm_problem problem = new svm_problem();
		final int length = dataPoints.size();
		problem.l = length;
		problem.y = new double[length];
		problem.x = new svm_node[length][];

		for (int i = 0; i < length; i++) {
			final DataPoint point = dataPoints.get(i);
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

	/**
	 * Get the learned model.
	 * 
	 * @return The learned model or <code>null</code> if the learning process
	 *         was not completed.
	 */
	public Model getModel() {
		return model == null || data == null || data.size() <= 0 ? null : new Model(model, data
				.get(0).getNumberOfFeatures());
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

	/**
	 * Calculate the category of the given data point, based on the learned
	 * model and defined categories.
	 * 
	 * @param dataPoint
	 *            The data point to check.
	 * @return The category of the data point, or <code>null</code> if such
	 *         category is not available.
	 */
	public Category calculateCategory(final DataPoint dataPoint) {
		Assert.assertNotNull("SVM model is not ready yet.", model);
		final double predictValue = svm.svm_predict(model, getSvmNode(dataPoint));
		return categoryMap.get(new Double(predictValue).intValue());
	}

	private List<DataPoint> getWrongClassifiedDataPoints() {
		final List<DataPoint> wrong = new ArrayList<DataPoint>();
		for (DataPoint dp : data) {
			if (!dp.getCategory().equals(calculateCategory(dp))) {
				wrong.add(dp);
			}
		}
		return wrong;
	}

	public double getModelAccuracy() {
		Assert.assertNotNull("SVM model is not available yet.", model);
		return 1.0 - ((double) getWrongClassifiedDataPoints().size() / data.size());
	}

}
