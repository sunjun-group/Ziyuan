package libsvm.core;

import libsvm.extension.AbstractSimpleSelectiveSamplingImpl;
import libsvm.extension.FeatureSelectionMachine;
import sav.settings.SAVExecutionTimeOutException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SunJunPaperTest extends TestUltility {
	private static final Logger LOGGER = LoggerFactory.getLogger(SunJunPaperTest.class);

	@Test
	public void test3() throws SAVExecutionTimeOutException {
		machine = setupMachine(new FeatureSelectionMachine(), 2);

		machine.addDataPoint(Category.NEGATIVE, 94, 0);
		machine.addDataPoint(Category.POSITIVE, 90, 0);
		machine.addDataPoint(Category.POSITIVE, 0, 0);
		machine.addDataPoint(Category.POSITIVE, 12, 0);

		machine.addDataPoint(Category.NEGATIVE, 90, 0);
		machine.addDataPoint(Category.NEGATIVE, 12, 0);
		machine.addDataPoint(Category.POSITIVE, 94, 0);
		machine.addDataPoint(Category.NEGATIVE, 12, 0);
		machine.addDataPoint(Category.POSITIVE, 94, 0);
		machine.addDataPoint(Category.POSITIVE, 80, 0);

		machine.train();
		System.out.println(">>> SVM RESULTS >>> ");
		System.out.println("Learned logic: " + machine.getLearnedLogic(false));
		System.out.println("Accuracy: " + machine.getModelAccuracy());
	}

	@Test
	public void test4() throws SAVExecutionTimeOutException {
		machine = setupMachine(new FeatureSelectionMachine(), 7);

		machine.addDataPoint(Category.NEGATIVE, 94, 1, 94, 2, 60, 3, 100);
		machine.addDataPoint(Category.POSITIVE, 90, 3, 75, 2, 90, 1, 80);
		machine.addDataPoint(Category.POSITIVE, 12, 99, -33, -10, 12, 0, 0);

		machine.addDataPoint(Category.NEGATIVE, 90, 1, 100, 2, 60, 3, 94);
		machine.addDataPoint(Category.NEGATIVE, 12, 1, 100, 2, 60, 3, 94);
		machine.addDataPoint(Category.POSITIVE, 94, 3, 75, 2, 90, 1, 80);
		machine.addDataPoint(Category.NEGATIVE, 12, 3, 75, 2, 90, 1, 80);
		machine.addDataPoint(Category.POSITIVE, 94, 99, -33, -10, 12, 0, 0);
		machine.addDataPoint(Category.POSITIVE, 80, 99, -33, -10, 12, 0, 0);

		machine.train();
		System.out.println(">>> SVM RESULTS >>> ");
		System.out.println("Learned logic: " + machine.getLearnedLogic(false));
		System.out.println("Accuracy: " + machine.getModelAccuracy());
	}

	@Test
	public void test5() throws SAVExecutionTimeOutException {
		machine = setupMachine(new FeatureSelectionMachine(), 2);
		machine.setSelectiveSamplingHandler(new AbstractSimpleSelectiveSamplingImpl(10) {
			@Override
			protected Category evaluate(double[] generatedPoint, Machine machine) {
				return Double.compare(generatedPoint[0], generatedPoint[1]) >= 0 ? Category.POSITIVE
						: Category.NEGATIVE;
			}
		});

		machine.addDataPoint(Category.NEGATIVE, 94, 100);
		machine.addDataPoint(Category.POSITIVE, 90, 80);
		machine.addDataPoint(Category.POSITIVE, 12, 0);

		machine.addDataPoint(Category.NEGATIVE, 90, 94);
		machine.addDataPoint(Category.NEGATIVE, 12, 94);
		machine.addDataPoint(Category.POSITIVE, 94, 80);
		machine.addDataPoint(Category.NEGATIVE, 12, 80);
		machine.addDataPoint(Category.POSITIVE, 94, 0);
		machine.addDataPoint(Category.POSITIVE, 80, 0);

		machine.addDataPoint(Category.NEGATIVE, 90, 94);
		machine.addDataPoint(Category.POSITIVE, 128, 94);
		machine.addDataPoint(Category.POSITIVE, 90, 80);
		machine.addDataPoint(Category.POSITIVE, 128, 80);
		machine.addDataPoint(Category.POSITIVE, 90, 0);
		machine.addDataPoint(Category.POSITIVE, 128, 0);

		machine.train();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(">>> SVM RESULTS >>> ");
			LOGGER.debug("Learned logic: " + machine.getLearnedLogic(false));
			LOGGER.debug("Accuracy: " + machine.getModelAccuracy());
		}
	}
}
