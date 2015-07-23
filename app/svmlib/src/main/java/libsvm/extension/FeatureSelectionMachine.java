package libsvm.extension;

import java.util.ArrayList;
import java.util.List;

import libsvm.core.Machine;

import org.apache.log4j.Logger;

public class FeatureSelectionMachine extends Machine {
	private static final Logger LOGGER = Logger.getLogger(FeatureSelectionMachine.class);
	private static final int MAX_FEATURE = 3;
	private Machine machine; // The machine used for learning, can be null

	@Override
	protected Machine train(final List<DataPoint> dataPoints) {
		int toSelect = 0;

		outerLoop: while (toSelect <= MAX_FEATURE) {
			toSelect++;

			// Select the features to train
			final int features = getNumberOfFeatures();
			final int[] selection = new int[toSelect];
			for (int i = 0; i < toSelect; i++) {
				selection[i] = i;
			}
			boolean selectionDone = false;
			while (!selectionDone) {
				// work with the current selection

				// copy selected labels
				final List<String> labels = new ArrayList<String>(toSelect);
				for (int i = 0; i < toSelect; i++) {
					labels.add(getDataLabels().get(selection[i]));
				}
				final Machine machine = createNewMachine(labels);

				// copy selected features
				for (DataPoint point : dataPoints) {
					double[] pointValue = new double[toSelect];
					for (int i = 0; i < toSelect; i++) {
						pointValue[i] = point.getValue(selection[i]);
					}
					machine.addDataPoint(point.getCategory(), pointValue);
				}

				// train SVM
				machine.train();

				// check SVM results
				final String learnedLogic = machine.getLearnedLogic(false);
				final double accuracy = machine.getModelAccuracy();

				if (LOGGER.isDebugEnabled()) {
					final StringBuilder str = new StringBuilder();
					for (String label : labels) {
						if (str.length() > 0) {
							str.append(", ");
						}
						str.append(label);
					}
					LOGGER.debug("Training with features: [" + str.toString() + "].");
					LOGGER.debug("Learned logic: " + learnedLogic);
					LOGGER.debug("Accuracy: " + accuracy);
				}

				if (Double.compare(accuracy, 1.0) >= 0) {
					if (machine.selectiveSampling()) {
						this.machine = machine;
						break outerLoop;
					}
				}

				// check if the current selection is the final one
				int i = toSelect - 1;
				while (i >= 0 && selection[i] == features - toSelect + i) {
					i--;
				}
				if (i < 0) {
					selectionDone = true;
					break;
				} else {
					// generate the next selection
					selection[i]++;
					for (int j = i + 1; j < toSelect; j++) {
						selection[j] = selection[j - 1] + 1;
					}
				}
			}
		}

		return this;
	}

	private Machine createNewMachine(final List<String> labels) {
		return new Machine().setDataLabels(labels).setParameter(getParameter());
	}

	@Override
	public String getLearnedLogic(boolean round) {
		if (this.machine == null) {
			return "";
		}
		return this.machine.getLearnedLogic(false);
	}

	@Override
	public double getModelAccuracy() {
		if (this.machine == null) {
			return 0.0;
		}
		return this.machine.getModelAccuracy();
	}

}
