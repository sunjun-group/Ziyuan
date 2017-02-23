package libsvm.core;

import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;

import org.jblas.DoubleMatrix;

import sav.common.core.utils.Assert;

/**
 * This class represents the result of a learning process
 * 
 * @author Nguyen Phuoc Nguong Phuc (npn)
 * 
 */
public class Model {

	private static final int NUMBER_OF_CLASSES_IN_BINARY_MODEL = 2;

	private final svm_model model;
	private final int numberOfFeatures;

	public Model(final svm_model model, final int numberOfFeatures) {
		Assert.assertNotNull(model, "SVM model cannot be null");
		this.model = model;
		this.numberOfFeatures = numberOfFeatures;
	}

	public int getNumberOfClasses() {
		return model.nr_class;
	}

	public boolean isBinary() {
		return NUMBER_OF_CLASSES_IN_BINARY_MODEL == getNumberOfClasses();
	}

	public int getNumberOfSupportVectors() {
		return model.l;
	}

	public double[] getWeights() {
		Assert.assertNotNull(model, "SVM model is not available yet.");

		// Weights are only available for linear SVMs
		if (model.param.svm_type != svm_parameter.C_SVC) {
			return null;
		}

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

	/**
	 * Get the {@link Divider} for this {@link Model}.<br/>
	 * The linear function is <code>wT * x = b </code>. <br/>
	 * In which:
	 * <ul>
	 * <li>the weight matrix <code>W = model.SV * model.coef</code></li>
	 * <li><code>wT</code> is the transition of <code>W</code></li>
	 * <li>the bias matrix<code>b = mode.rho</code></li>
	 * </ul>
	 * We assume that the bias matrix contains only 1 element. I.e.: double[1].
	 * 
	 * @return The divider which can be use to categorize the learned data.
	 */
	public Divider getExplicitDivider() {
		Assert.assertNotNull(model, "SVM model is not available yet.");

		// coef = [x][number of SVs]
		DoubleMatrix coefficientMatrix = null;
		try{
			coefficientMatrix = new DoubleMatrix(model.sv_coef);
		}
		catch(Exception e){}
		
		if(coefficientMatrix == null){
			System.currentTimeMillis();
			return null;
		}
		
		// (!) NOTE: We assert that x is always equal to 1
		Assert.assertTrue(coefficientMatrix.getRows() == 1, "Unexpected size of matrices.");

		double[][] supportVectors = new double[model.SV.length][];
		{
			int i = 0;
			for (svm_node[] row : model.SV) {
				supportVectors[i] = new double[row.length];
				int j = 0;
				for (svm_node node : row) {
					supportVectors[i][j++] = node.value;
				}
				i++;
			}
		}

		// sv = [number of SVs][number of features]
		DoubleMatrix supportVectorMatrix = new DoubleMatrix(supportVectors);

		Assert.assertTrue(coefficientMatrix.multipliesWith(supportVectorMatrix),
				"Cannot multiply coefficient matrix [" + coefficientMatrix.rows + "]["
				+ coefficientMatrix.columns + "]" + " with support vector matrix ["
				+ supportVectorMatrix.rows + "][" + supportVectorMatrix.columns + "].");

		// w = [x][number of features]
		// wT = [number of features][x]
		// (!) NOTE: we don't explicitly transpose the matrix
		final DoubleMatrix weightMatrix = coefficientMatrix.mmul(supportVectorMatrix);

		double[] result = new double[weightMatrix.getColumns()];
		for (int i = 0; i < weightMatrix.getColumns(); i++) {
			result[i] = weightMatrix.get(0, i);
		}

		// b = [x] = [model.rho]
		// The function is wT * x = b
		return new Divider(result, model.rho[0]);
	}
	
	public CategoryCalculator getCategoryCalculator() {
		return new ModelBasedCategoryCalculator(model);
	}
	
	public static CategoryCalculator getCategoryCalculator(final svm_model rawModel) {
		return new ModelBasedCategoryCalculator(rawModel);
	}

}
