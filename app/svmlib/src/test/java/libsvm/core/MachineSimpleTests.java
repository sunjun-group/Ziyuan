package libsvm.core;

import java.util.Random;

import libsvm.binary.MultiCutMachine;
import libsvm.core.Machine.Category;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MachineSimpleTests {

	private static final Logger LOGGER = Logger.getLogger(MachineSimpleTests.class);

	private static final int NUMBER_OF_FEATURES = 2;
	private static final int NUMBER_OF_DATA_POINTS = 1000;
	private static final String POSITIVE = "POSITIVE";
	private static final String NEGATIVE = "NEGATIVE";
	private static final String[] CATEGORIES = { POSITIVE, NEGATIVE };
	private static final Random RANDOM = new Random();

	private Machine normalMachine;
	private Machine improvedMachine;

	@Before
	public void prepareMachines() {
		normalMachine = setupMachine(new Machine());
		improvedMachine = setupMachine(new MultiCutMachine());
	}

	private Machine setupMachine(final Machine machine) {
		return machine.setParameter(new Parameter().setMachineType(MachineType.C_SVC)
				.setKernelType(KernelType.LINEAR).setC(1.0).setCacheSize(100.0).setEps(1e-3)
				.setShrinking(1).setProbability(1).setNrWeight(0).setWeight(new double[0])
				.setWeightLabel(new int[0]));
	}

	@Test
	public void testWithRandomData() {
		for (int i = 0; i < NUMBER_OF_DATA_POINTS; i++) {
			final String categoryString = CATEGORIES[RANDOM.nextInt(CATEGORIES.length)];
			final double[] values = { Math.random(), Math.random() };

			normalMachine.addDataPoint(randomDataPoint(normalMachine.getCategory(categoryString),
					values));
			improvedMachine.addDataPoint(randomDataPoint(
					improvedMachine.getCategory(categoryString), values));
		}

		final double normalModelAccuracy = normalMachine.train().getModelAccuracy();
		LOGGER.log(Level.DEBUG, "Normal SVM:" + normalModelAccuracy);
		final double improvedModelAccuracy = improvedMachine.train().getModelAccuracy();
		LOGGER.log(Level.DEBUG, "Improved SVM:" + improvedModelAccuracy);

		Assert.assertTrue("Improved algorithm produces lower accuracy model than normal one.",
				improvedModelAccuracy >= normalModelAccuracy);
	}

	private DataPoint randomDataPoint(final Category category, final double... values) {
		final DataPoint dp = new DataPoint(NUMBER_OF_FEATURES);
		dp.setValues(values);
		dp.setCategory(category);
		return dp;
	}

	@Test
	public void testWithSingleLinearLineSeparableData() {
		// Separator: 2x + 3y = 10
		for (int i = 0; i < NUMBER_OF_DATA_POINTS; i++) {
			final String categoryString = CATEGORIES[RANDOM.nextInt(CATEGORIES.length)];
			double x, y, z;
			// Positive values: 2x + 3y < 10
			if (POSITIVE.equals(categoryString)) {
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
			Assert.assertTrue(
					"Category and values are not consistent.",
					(POSITIVE.equals(categoryString) && (2 * x + 3 * y < 10))
							|| (NEGATIVE.equals(categoryString) && (2 * x + 3 * y > 10)));
			normalMachine.addDataPoint(randomDataPoint(normalMachine.getCategory(categoryString),
					x, y));
			improvedMachine.addDataPoint(randomDataPoint(
					improvedMachine.getCategory(categoryString), x, y));
		}

		Assert.assertTrue("Incompleted generated input data.",
				normalMachine.countAvailableCategories() > 1);
		Assert.assertTrue("Incompleted generated input data.",
				improvedMachine.countAvailableCategories() > 1);

		final double normalModelAccuracy = normalMachine.train().getModelAccuracy();
		LOGGER.log(Level.DEBUG, "Normal SVM:" + normalModelAccuracy);
		final double improvedModelAccuracy = improvedMachine.train().getModelAccuracy();
		LOGGER.log(Level.DEBUG, "Improved SVM:" + improvedModelAccuracy);

		// In this case, because there exists a single linear divider
		// Both machines should be able to find that single divider
		// And the two results should be the same
		Assert.assertTrue("Inconsistent results.",
				Double.compare(normalModelAccuracy, improvedModelAccuracy) == 0);
	}
}
