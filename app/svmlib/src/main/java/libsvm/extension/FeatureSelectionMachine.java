package libsvm.extension;

import java.util.ArrayList;
import java.util.List;

import libsvm.core.IDividerProcessor;
import libsvm.core.Machine;
import libsvm.core.Model;

import org.apache.log4j.Logger;

/**
 * This implementation iterates on all possible subsets of 1, 2 and 3 features
 * got from the existing features. It then uses an instance of the default SVM
 * Machine implementation to learn on the subset.
 * <p>
 * If a predicate with accuracy of 1 is learned, it tries to tune the logic
 * using selective sampling. After the process, if a logic with the accuracy of
 * 1 is returned, the process stops. In that case, the instance of the inner SVM
 * machine used for training is stored inside the <code>machine</code> property.
 * Otherwise, this property is <code>null</code>.
 * 
 * @author Nguyen Phuoc Nguong Phuc (npn)
 * 
 */
public class FeatureSelectionMachine extends Machine {
	private static final Logger LOGGER = Logger.getLogger(FeatureSelectionMachine.class);
	private static final int MAX_FEATURE = 3;
	private Machine machine; // The machine used for learning, can be null

	@Override
	public Machine resetData() {
		this.machine = null;
		return super.resetData();
	}

	@Override
	protected Machine train(final List<DataPoint> dataPoints) {
		int toSelect = 0;
		final int features = getNumberOfFeatures();

		outerLoop: while (toSelect < MAX_FEATURE && toSelect < features) {
			toSelect++;

			// Select the features to train
			debug("Select " + toSelect + " feature(s) to train.");
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

				final StringBuilder str = new StringBuilder();
				for (String label : labels) {
					if (str.length() > 0) {
						str.append(", ");
					}
					str.append(label);
				}
				debug("Training with features: [" + str.toString() + "].");
				debug("Learned logic: " + learnedLogic);
				debug("Accuracy: " + accuracy);
				// We only run selective sampling after we found an
				// "interesting" logic
				// I.e.: we will try to "tune" the logic then
				if (Double.compare(accuracy, 1.0) >= 0) {
					debug("Trying to improve the learned logic using Selective Sampling.");
					if (machine.selectiveSampling()) {
						debug("Selective Sampling finished successfully. The learning process will stop.");
						this.machine = machine;
						break outerLoop;
					} else {
						debug("Selective Sampling failed to improve the learned logic.");
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

	private static void debug(final String debugInfo) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(debugInfo);
		}
	}

	private Machine createNewMachine(final List<String> labels) {
		Machine machine = new Machine().setDataLabels(labels).setParameter(getParameter());
		machine.setSelectiveSamplingHandler(this.getSelectiveSamplingHandler());
		return machine;
	}

	@Override
	public Model getModel() {
		return this.machine == null ? super.getModel() : this.machine.getModel();
	}

	@Override
	public <R> R getLearnedLogic(IDividerProcessor<R> processor, boolean round) {
		if (this.machine == null) {
			return super.getLearnedLogic(processor, round);
		}
		return this.machine.getLearnedLogic(processor, round);
	}

	@Override
	public double getModelAccuracy() {
		if (this.machine == null) {
			return 0.0;
		}
		return this.machine.getModelAccuracy();
	}

}
