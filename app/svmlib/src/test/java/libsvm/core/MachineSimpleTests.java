package libsvm.core;

import java.io.InputStream;
import org.junit.Test;

public class MachineSimpleTests extends TestUltility{

	@Test
	public void whenThereAreTwoFeatures() {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("TwoFeatures.txt");
		runTest(new Machine(), 2, new double[]{-2, -3, -15}, inputStream);
	}

	@Test
	public void whenThereAreThreeFeatures() {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ThreeFeatures.txt");
		runTest(new Machine(), 3, new double[]{3, 7, 19, 80}, inputStream);
	}
	
	@Test
	public void whenRequireManyDividersInPositiveMachine() {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ManyDuplicatedCuts.txt");
		runTest(new Machine(), 3, new double[]{3, 7, 19, 80}, inputStream);
	}
	
//	@Test
//	public void testWithRandomData() {
//		LOGGER.info("===========[testWithRandomData]===========");
//		for (int i = 0; i < NUMBER_OF_DATA_POINTS; i++) {
//			final double[] values = { Math.random(), Math.random() };
//			final Category category = Category.random();
//			machine.addDataPoint(category, values);
//			improvedMachine.addDataPoint(category, values);
//		}
//
//		final double normalModelAccuracy = machine.train().getModelAccuracy();
//		LOGGER.log(Level.DEBUG, "Normal SVM:" + normalModelAccuracy);
//		final double improvedModelAccuracy = improvedMachine.train().getModelAccuracy();
//		LOGGER.log(Level.DEBUG, "Improved SVM:" + improvedModelAccuracy);
//
//		Assert.assertTrue("Improved algorithm produces lower accuracy model than normal one.",
//				Double.compare(normalModelAccuracy, improvedModelAccuracy) <= 0);
//		
//		LOGGER.info("Learned logic:");
//		LOGGER.info("Normal machine: " + machine.getLearnedLogic());
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
//			machine.addDataPoint(category, x, y);
//			positiveSeparationMachine.addDataPoint(category, x, y);
//		}
//
//		LOGGER.log(Level.DEBUG, "Positive cases =" + countPositive);
//		LOGGER.log(Level.DEBUG, "Negative cases =" + countNegative);
//
//		final double normalModelAccuracy = machine.train().getModelAccuracy();
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
//		LOGGER.info("Normal machine: " + machine.getLearnedLogic());
//		LOGGER.info("PS machine: " + positiveSeparationMachine.getLearnedLogic());
//	}
	

}
