package libsvm.extension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import libsvm.svm_model;
import libsvm.core.Category;
import libsvm.core.CategoryCalculator;
import libsvm.core.Divider;
import libsvm.core.Machine;
import libsvm.core.Model;
import libsvm.core.ModelBasedCategoryCalculator;

import org.junit.Assert;

/**
 * This SVM machine will try to divide the given data points many times if
 * possible to increase accuracy.<br/>
 * The training algorithm for this Machine is as follows:<br/>
 * <ul>
 * <li>Run SVM algorithm on the current data set to find a divider</li>
 * <li>Find the collection of all points which are (A) and are not (B)
 * classified correctly using the divider</li>
 * <li>While (B) is not empty, <b>and only if <i>all points</i> in (B) resides
 * on 1 side of the divider</b>, do the following steps:</li>
 * <ul>
 * <li>Use (B) as the new training data set.</li>
 * <li>Run SVM algorithm on (B).</li>
 * <li>Use the new divider in conjunction with the old divider.</li>
 * </ul>
 * </ul> <br/>
 * The limit of this algorithm is that it <b>depends on the ability of SVM</b>
 * to give out a divider which can separate data points into a state in which
 * there is only 1 side contains wrong data. I.e.: the algorithm may stop
 * without being able to improve the learning result at all.
 * 
 * @author Nguyen Phuoc Nguong Phuc (npn)
 * 
 */
public class MultiCutMachine extends Machine {

	private List<LearnedData> learnedDatas;

	public MultiCutMachine() {
		learnedDatas = new ArrayList<LearnedData>();
	}

	protected class LearnedData {
		private svm_model model;
		private Set<Category> wrongSides = new HashSet<Category>();

		public svm_model getModel() {
			return model;
		}
	}

	protected boolean keepTraining(final List<DataPoint> trainingData, final int wrongCategories) {
		// Keep training if there's data to learn from
		// and there's only 1 wrong category
		return trainingData != null && !trainingData.isEmpty() && wrongCategories == 1;
	}

	@Override
	protected Machine train(final List<DataPoint> dataPoints) {
		List<DataPoint> trainingData = dataPoints;
		// We learned nothing, so all points are wrong
		int wrongCategories = Category.getValues().size();
		do {
			super.train(trainingData);
			final LearnedData learnedData = getLearnedData(trainingData, model);
			learnedDatas.add(learnedData);

			wrongCategories = learnedData.wrongSides.size();
			if (wrongCategories == 1) {
				final Category wrongCategory = getWrongCategory(learnedData);
				Assert.assertNotNull(wrongCategory);
				// Try to improve the result
				trainingData = buildNextTrainingData(trainingData, learnedData, wrongCategory);
			}
		} while (keepTraining(trainingData, wrongCategories));

		return this;
	}

	private LearnedData getLearnedData(final List<DataPoint> trainingData,
			final svm_model learnedModel) {
		final List<DataPoint> wrongClassifications = getWrongClassifiedDataPoints(trainingData,
				null);

		LearnedData learnedData = new LearnedData();
		learnedData.model = learnedModel;
		for (DataPoint dp : wrongClassifications) {
			learnedData.wrongSides.add(dp.getCategory());
		}
		return learnedData;
	}

	private Category getWrongCategory(final LearnedData learnedData) {
		Category category = null;
		for (Category cat : learnedData.wrongSides) {
			if (category == null) {
				category = cat;
			} else {
				return null; // Multiple values found
			}
		}
		return category;
	}

	protected List<DataPoint> buildNextTrainingData(final List<DataPoint> currentTrainingData,
			final LearnedData learnedData, final Category wrongCategory) {
		final List<DataPoint> newTrainingData = new ArrayList<DataPoint>();
		final svm_model rawModel = learnedData.getModel();
		final CategoryCalculator categoryCalculator = Model.getCategoryCalculator(rawModel);
		for (DataPoint dp : currentTrainingData) {
			// Points on the same side of the wrong classification
			final Category calculatedCategory = categoryCalculator.getCategory(dp);
			if (calculatedCategory.equals(wrongCategory)) {
				newTrainingData.add(dp);
			}
		}
		return newTrainingData;
	}

	@Override
	protected List<DataPoint> getWrongClassifiedDataPoints(List<DataPoint> dataPoints) {
		return getWrongClassifiedDataPoints(dataPoints, new LearnedDataBasedCategoryCalculator(
				learnedDatas));
	}

	private class LearnedDataBasedCategoryCalculator implements CategoryCalculator {
		private List<LearnedData> learnedDatas;

		public LearnedDataBasedCategoryCalculator(final List<LearnedData> learnedDatas) {
			this.learnedDatas = learnedDatas;
		}

		public Category getCategory(DataPoint dataPoint) {
			Assert.assertTrue("There is no learned data.", !learnedDatas.isEmpty());
			Category result = null;
			for (LearnedData data : learnedDatas) {
				result = new ModelBasedCategoryCalculator(data.model).getCategory(dataPoint);
				if (!data.wrongSides.contains(result)) {
					// Correct data found
					break;
				}
			}
			return result; // Can be null or incorrect
		}
	}
	
	@Override
	public String getLearnedLogic() {
		StringBuilder str = new StringBuilder();

		final int numberOfFeatures = getRandomData().getNumberOfFeatures();
		for (LearnedData data : learnedDatas) {
			if (data.model != null) {				
				final Divider explicitDivider = new Model(data.model, numberOfFeatures)
				.getExplicitDivider();
				if (str.length() != 0) {
					str.append(" ^ ");
				}
				str.append(getLearnedLogic(explicitDivider));
			}
		}

		return str.toString();
	}

}
