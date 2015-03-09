package libsvm.core;

import java.io.InputStream;
import java.util.Scanner;
import org.apache.log4j.Level;
import org.junit.Test;

public class MachineSimpleTests extends TestUltility{

	private Machine normalMachine;
	
	@Test
	public void whenThereAreTwoFeatures() {
		normalMachine = setupMachine(new Machine(), 2);
		
		// Separator: ax + by = c
		int x, y;
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("TwoFeatures.txt");
		Scanner scanner = new Scanner(inputStream);
		int numberOfPositivePoints = scanner.nextInt();
		
		for (int i = 0; i < numberOfPositivePoints; i++) {
			Category category = Category.POSITIVE;
			x = scanner.nextInt();
			y = scanner.nextInt();
			normalMachine.addDataPoint(category, x, y);
		}

		int numberOfNegativePoints = scanner.nextInt();
		for (int i = 0; i < numberOfNegativePoints; i++) {
			Category category = Category.NEGATIVE;
			x = scanner.nextInt();
			y = scanner.nextInt();
			normalMachine.addDataPoint(category, x, y);
		}

		final double normalModelAccuracy = normalMachine.train().getModelAccuracy();
		LOGGER.log(Level.DEBUG, "Normal SVM:" + normalModelAccuracy);
		
		LOGGER.info("Learned logic:");
		LOGGER.info("Normal machine:\n" + normalMachine.getLearnedLogic());
		
		Divider divider = normalMachine.getModel().getExplicitDivider();
		double[] expectedCoefficients = new double[]{-2, -3, -15};
		
		double[] coefficients = new CoefficientProcessing().process(divider);
		compareCoefficients(expectedCoefficients, coefficients);
	}

	@Test
	public void whenThereAreThreeFeatures() {
		normalMachine = setupMachine(new Machine(), 3);
		
		// Separator: ax + by + cz = d
		int x, y, z;
		
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ThreeFeatures.txt");
		Scanner scanner = new Scanner(inputStream);
		int numberOfPositivePoints = scanner.nextInt();
		
		for (int i = 0; i < numberOfPositivePoints; i++) {
			Category category = Category.POSITIVE;
			x = scanner.nextInt();
			y = scanner.nextInt();
			z = scanner.nextInt();

			normalMachine.addDataPoint(category, x, y, z);
		}
		
		int numberOfNegativePoints = scanner.nextInt();
		for (int i = 0; i < numberOfNegativePoints; i++) {
			Category category = Category.NEGATIVE;
			x = scanner.nextInt();
			y = scanner.nextInt();
			z = scanner.nextInt();

			normalMachine.addDataPoint(category, x, y, z);
		}

		final double normalModelAccuracy = normalMachine.train().getModelAccuracy();
		LOGGER.log(Level.DEBUG, "Normal SVM:" + normalModelAccuracy);
		
		LOGGER.info("Learned logic:");
		LOGGER.info("Normal machine:\n" + normalMachine.getLearnedLogic());		
		
		Divider divider = normalMachine.getModel().getExplicitDivider();
		double[] expectedCoefficients = new double[]{3, 7, 19, 80};
		
		double[] coefficients = new CoefficientProcessing().process(divider);
		compareCoefficients(expectedCoefficients, coefficients);
	}
	
//	@Test
//	public void testWithRandomData() {
//		LOGGER.info("===========[testWithRandomData]===========");
//		for (int i = 0; i < NUMBER_OF_DATA_POINTS; i++) {
//			final double[] values = { Math.random(), Math.random() };
//			final Category category = Category.random();
//			normalMachine.addDataPoint(category, values);
//			improvedMachine.addDataPoint(category, values);
//		}
//
//		final double normalModelAccuracy = normalMachine.train().getModelAccuracy();
//		LOGGER.log(Level.DEBUG, "Normal SVM:" + normalModelAccuracy);
//		final double improvedModelAccuracy = improvedMachine.train().getModelAccuracy();
//		LOGGER.log(Level.DEBUG, "Improved SVM:" + improvedModelAccuracy);
//
//		Assert.assertTrue("Improved algorithm produces lower accuracy model than normal one.",
//				Double.compare(normalModelAccuracy, improvedModelAccuracy) <= 0);
//		
//		LOGGER.info("Learned logic:");
//		LOGGER.info("Normal machine: " + normalMachine.getLearnedLogic());
//		LOGGER.info("Improved machine: " + improvedMachine.getLearnedLogic());
//	}
//	
//	@Test
//	public void testWithTwoLinearSeparableData() {
//		LOGGER.info("===========[testWithTwoLinearSeparableData]===========");
//		
//		// x => 3 ^ y <= 5 are considered POSITIVE
//		int countPositive = 0, countNegative = 0;
//		for (int i = 0; i < NUMBER_OF_DATA_POINTS; i++) {
//			double x = RANDOM.nextInt();
//			double y = RANDOM.nextInt();
//			final Category category = x >= 3 && y <= 5 ? Category.POSITIVE : Category.NEGATIVE;
//			if (Category.POSITIVE == category) {
//				countPositive++;
//			} else {
//				countNegative++;
//			}
//			normalMachine.addDataPoint(category, x, y);
//			positiveSeparationMachine.addDataPoint(category, x, y);
//		}
//
//		LOGGER.log(Level.DEBUG, "Positive cases =" + countPositive);
//		LOGGER.log(Level.DEBUG, "Negative cases =" + countNegative);
//
//		final double normalModelAccuracy = normalMachine.train().getModelAccuracy();
//		LOGGER.log(Level.DEBUG, "Normal SVM:" + normalModelAccuracy);
//		final double improvedModelAccuracy = positiveSeparationMachine.train().getModelAccuracy();
//		LOGGER.log(Level.DEBUG, "Improved SVM:" + improvedModelAccuracy);
//
//		// We expect the improved model must perform better in this case
//		Assert.assertTrue(
//				"Improved algorithm does not produce higer accuracy model than normal one."
//						+ " Normal:" + normalModelAccuracy + "; Improved:" + improvedModelAccuracy,
//				Double.compare(normalModelAccuracy, improvedModelAccuracy) < 0);
//
//		LOGGER.info("Learned logic:");
//		LOGGER.info("Normal machine: " + normalMachine.getLearnedLogic());
//		LOGGER.info("PS machine: " + positiveSeparationMachine.getLearnedLogic());
//	}
	

}
