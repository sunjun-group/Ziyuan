package libsvm.core;

import java.util.Random;

import libsvm.extension.MultiCutMachine;
import libsvm.extension.PositiveSeparationMachine;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MachineSimpleTests {

	private static final Logger LOGGER = Logger.getLogger(MachineSimpleTests.class);

	private static final int NUMBER_OF_FEATURES = 2;
	private static final int NUMBER_OF_DATA_POINTS = 1000;
	private static final Random RANDOM = new Random();

	private Machine normalMachine;
	private Machine improvedMachine;
	private Machine positiveSeparationMachine;

	@Before
	public void prepareMachines() {
		normalMachine = setupMachine(new Machine());
		improvedMachine = setupMachine(new MultiCutMachine());
		positiveSeparationMachine = setupMachine(new PositiveSeparationMachine());
	}

	private Machine setupMachine(final Machine machine) {
		return machine.setNumberOfFeatures(NUMBER_OF_FEATURES).setParameter(
				new Parameter().setMachineType(MachineType.C_SVC).setKernelType(KernelType.LINEAR)
						.setEps(1.0).setUseShrinking(false).setPredictProbability(false));
	}

	@Test
	public void testWithRandomData() {
		LOGGER.info("===========[testWithRandomData]===========");
		for (int i = 0; i < NUMBER_OF_DATA_POINTS; i++) {
			final double[] values = { Math.random(), Math.random() };
			final Category category = Category.random();
			normalMachine.addDataPoint(category, values);
			improvedMachine.addDataPoint(category, values);
		}

		final double normalModelAccuracy = normalMachine.train().getModelAccuracy();
		LOGGER.log(Level.DEBUG, "Normal SVM:" + normalModelAccuracy);
		final double improvedModelAccuracy = improvedMachine.train().getModelAccuracy();
		LOGGER.log(Level.DEBUG, "Improved SVM:" + improvedModelAccuracy);

		Assert.assertTrue("Improved algorithm produces lower accuracy model than normal one.",
				Double.compare(normalModelAccuracy, improvedModelAccuracy) <= 0);
		
		LOGGER.info("Learned logic:");
		LOGGER.info("Normal machine: " + normalMachine.getLearnedLogic());
		LOGGER.info("Improved machine: " + improvedMachine.getLearnedLogic());
	}

	@Test
	public void testWithSingleLinearLineSeparableData() {
		LOGGER.info("===========[testWithSingleLinearLineSeparableData]===========");
		// Separator: 2x + 3y = 10
		for (int i = 0; i < NUMBER_OF_DATA_POINTS; i++) {
			final Category category = Category.random();
			double x, y, z;
			// Positive values: 2x + 3y < 10
			if (Category.POSITIVE == category) {
				// Generate a number between 0 and 10
				z = 10 * RANDOM.nextDouble();
				// Generate x
				x = 10 * RANDOM.nextDouble();
				// Calculate y
				y = (z - 2 * x) / 3;

			} else {
				// Generate a number larger than 10
				z = 10 + 100 * RANDOM.nextDouble();
				// Generate x
				x = 100 * RANDOM.nextDouble();
				// Calculate y
				y = (z - 2 * x) / 3;
			}
			Assert.assertTrue("Category and values are not consistent.",
					(Category.POSITIVE == category && (2 * x + 3 * y < 10))
							|| (Category.NEGATIVE == category && (2 * x + 3 * y > 10)));
			normalMachine.addDataPoint(category, x, y);
			improvedMachine.addDataPoint(category, x, y);
		}

		final double normalModelAccuracy = normalMachine.train().getModelAccuracy();
		LOGGER.log(Level.DEBUG, "Normal SVM:" + normalModelAccuracy);
		final double improvedModelAccuracy = improvedMachine.train().getModelAccuracy();
		LOGGER.log(Level.DEBUG, "Improved SVM:" + improvedModelAccuracy);

		// In this case, because there exists a single linear divider
		// Both machines should be able to find that single divider
		// And the two results should be the same
		Assert.assertTrue("Inconsistent results.",
				Double.compare(normalModelAccuracy, improvedModelAccuracy) == 0);
		
		LOGGER.info("Learned logic:");
		LOGGER.info("Normal machine: " + normalMachine.getLearnedLogic());
		LOGGER.info("Improved machine: " + improvedMachine.getLearnedLogic());
	}

	@Test
	public void testWithTwoLinearSeparableData() {
		LOGGER.info("===========[testWithTwoLinearSeparableData]===========");
		
		// x => 3 ^ y <= 5 are considered POSITIVE
		int countPositive = 0, countNegative = 0;
		for (int i = 0; i < NUMBER_OF_DATA_POINTS; i++) {
			double x = RANDOM.nextInt();
			double y = RANDOM.nextInt();
			final Category category = x >= 3 && y <= 5 ? Category.POSITIVE : Category.NEGATIVE;
			if (Category.POSITIVE == category) {
				countPositive++;
			} else {
				countNegative++;
			}
			normalMachine.addDataPoint(category, x, y);
			positiveSeparationMachine.addDataPoint(category, x, y);
		}

		LOGGER.log(Level.DEBUG, "Positive cases =" + countPositive);
		LOGGER.log(Level.DEBUG, "Negative cases =" + countNegative);

		final double normalModelAccuracy = normalMachine.train().getModelAccuracy();
		LOGGER.log(Level.DEBUG, "Normal SVM:" + normalModelAccuracy);
		final double improvedModelAccuracy = positiveSeparationMachine.train().getModelAccuracy();
		LOGGER.log(Level.DEBUG, "Improved SVM:" + improvedModelAccuracy);

		// We expect the improved model must perform better in this case
		Assert.assertTrue(
				"Improved algorithm does not produce higer accuracy model than normal one."
						+ " Normal:" + normalModelAccuracy + "; Improved:" + improvedModelAccuracy,
				Double.compare(normalModelAccuracy, improvedModelAccuracy) < 0);

		LOGGER.info("Learned logic:");
		LOGGER.info("Normal machine: " + normalMachine.getLearnedLogic());
		LOGGER.info("PS machine: " + positiveSeparationMachine.getLearnedLogic());
	}
}
