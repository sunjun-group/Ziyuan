package learntest.svm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import libsvm.svm_model;
import libsvm.core.Category;
import libsvm.core.Divider;
import libsvm.core.Machine;
import libsvm.core.Model;
import libsvm.extension.MultiDividerBasedCategoryCalculator;
import libsvm.extension.NegativePointSelection;

public class MyPositiveSeparationMachine extends Machine {

	protected static final Logger LOGGER = LoggerFactory.getLogger(MyPositiveSeparationMachine.class);

	private List<svm_model> learnedModels;
	
	private NegativePointSelection negativePointSelection;
	
	public MyPositiveSeparationMachine(NegativePointSelection negativePointSelection) {
		this.negativePointSelection = negativePointSelection;
	}

	@Override
	protected Machine train(List<DataPoint> dataPoints) {
		learnedModels = new ArrayList<svm_model>();
		super.train(dataPoints);
		if(model != null && getModelAccuracy() == 1.0) {
			learnedModels.add(model);
			return this;
		}
		
		List<DataPoint> positives = new ArrayList<DataPoint>(dataPoints.size());
		List<DataPoint> negatives = new ArrayList<DataPoint>(dataPoints.size());
		classifyNegativePositivePoints(dataPoints, positives, negatives);
		List<DataPoint> trainingData = positives;
		while (!negatives.isEmpty()) {
			trainingData.add(negativePointSelection.select(negatives, positives));
			super.train(trainingData);
			if (model != null) {
				learnedModels.add(model);
			}

			trainingData.remove(trainingData.size() - 1);
			removeClassifiedNegativePoints(negatives);
		}
		
		return this;
	}
	
	private void classifyNegativePositivePoints(List<DataPoint> dataPoints, List<DataPoint> positives, 
			List<DataPoint> negatives) {
		for (DataPoint point : dataPoints) {
			if (Category.POSITIVE == point.getCategory()) {
				positives.add(point);
			} else {
				negatives.add(point);
			}
		}
	}
	
	private void removeClassifiedNegativePoints(final List<DataPoint> negatives) {
		if (model == null) {
			return;
		}
		Divider roundDivider = new Model(model, getNumberOfFeatures()).getExplicitDivider().round();
		for (Iterator<DataPoint> it = negatives.iterator(); it.hasNext();) {
			DataPoint dp = it.next();
			if (roundDivider.dataPointBelongTo(dp, Category.NEGATIVE)) {
				it.remove();
			}
		}
	}
	
	@Override
	protected List<DataPoint> getWrongClassifiedDataPoints(List<DataPoint> dataPoints) {
		List<Divider> roundDividers = new ArrayList<Divider>();
		for (svm_model learnModel : this.learnedModels) {
			if (learnModel != null) {
				roundDividers.add(new Model(learnModel, getNumberOfFeatures()).getExplicitDivider()
						.round());
			}
		}

		return getWrongClassifiedDataPoints(dataPoints, new MultiDividerBasedCategoryCalculator(
				roundDividers));
	}
	
	public List<svm_model> getLearnedModels() {
		return learnedModels;
	}
	
}
