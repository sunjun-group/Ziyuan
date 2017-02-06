package libsvm.core;

import java.util.Random;

import libsvm.core.Machine.DataPoint;
import libsvm.extension.AbstractSimpleSelectiveSamplingImpl;
import libsvm.extension.FeatureSelectionMachine;
import sav.settings.SAVExecutionTimeOutException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeatureSelectionMachineTest extends TestUltility {
	private static final Logger LOGGER = LoggerFactory.getLogger(FeatureSelectionMachineTest.class);
	private static final int NUMBER_OF_FEATURES = 10;
	private static final Random RAND = new Random();

	@Test
	public void test() throws SAVExecutionTimeOutException {
		// Initialize an instance of the SVM
		machine = setupMachine(new FeatureSelectionMachine(), NUMBER_OF_FEATURES);
		// This handler simulates the behavior of re-run the test cases
		machine.setSelectiveSamplingHandler(new AbstractSimpleSelectiveSamplingImpl(10) {
			@Override
			protected Category evaluate(double[] generatedPoint, Machine machine) {
				return FeatureSelectionMachineTest.evaluate(generatedPoint);
			}
		});

		for (int i = 0; i < 100; i++) {
			machine.addDataPoint(generateDataPoint());
		}

		machine.train();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(">>> SVM RESULTS >>> ");
			LOGGER.debug("Learned logic: " + machine.getLearnedLogic(false));
			LOGGER.debug("Accuracy: " + machine.getModelAccuracy());
		}
	}

	public static Category evaluate(double[] generatedPoint) {
		return RAND.nextBoolean() ? Category.POSITIVE : Category.NEGATIVE;
	}

	private DataPoint generateDataPoint() {
		double[] values = new double[NUMBER_OF_FEATURES];
		for (int i = 0; i < NUMBER_OF_FEATURES; i++) {
			values[i] = RAND.nextDouble();
		}
		return machine.createDataPoint(FeatureSelectionMachineTest.evaluate(values), values);
	}

}
