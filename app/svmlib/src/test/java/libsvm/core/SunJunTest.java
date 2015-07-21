package libsvm.core;

import java.util.ArrayList;
import java.util.List;

import libsvm.extension.FeatureSelectionMachine;
import libsvm.extension.PositiveSeparationMachine;
import libsvm.extension.RandomNegativePointSelection;

import org.junit.Before;
import org.junit.Test;

public class SunJunTest extends TestUltility {

	private Machine machine;
	private Machine machine2;

	@Before
	public void setup() {
		machine = setupMachine(new FeatureSelectionMachine(), 7);
		machine2 = setupMachine(new PositiveSeparationMachine(new RandomNegativePointSelection()),
				7);
	}

	private final double[][] positivePoints = new double[][] { { 3, 75, 2, 90, 2, 80, 90 },
			{ 99, -33, -10, 12, 2, 0, 12 }, { 3, 75, 2, 90, 1, 80, 90 },
			{ 99, -33, -10, 12, 0, 0, 12 } };
	private final double[][] negativePoints = new double[][] { { 1, 85, 2, 60, 2, 94, 85 },
			{ 1, 85, 2, 60, 3, 94, 85 } };

	@Test
	public void test1() {
		for (double[] point : positivePoints) {
			machine.addDataPoint(Category.POSITIVE, point);
		}
		for (double[] point : negativePoints) {
			machine.addDataPoint(Category.NEGATIVE, point);
		}

		machine.train();

		System.out.println("Learned logic: " + machine.getLearnedLogic());
		System.out.println("Accuracy: " + machine.getModelAccuracy());
	}

	private final double[][] SAMPLE_POINTS = new double[][] { { 1, 85, 2, 60, 3, 94, 85 },
			{ 3, 75, 2, 90, 1, 80, 90 }, { 99, -33, -10, 12, 0, 0, 12 } };

	private double[][] generatePoints() {
		int[] index = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		List<double[]> points = new ArrayList<double[]>();
		boolean stop = false;
		while (!stop) {
			// Use current index
			double[] point = new double[7];
			for (int i = 0; i < 7; i++) {
				point[i] = SAMPLE_POINTS[index[i]][i];
			}
			points.add(point);
			// Check if reach the final index
			{
				int i = 0;
				while (i < 7 && index[i] == 2) {
					i++;
				}
				if (i == 7) {
					stop = true;
					break;
				}
			}
			// Generate next index
			for (int i = 0; i < 7; i++) {
				if (index[i] < 2) {
					index[i]++;
					break;
				} else {
					index[i] = 0;
				}
			}
		}
		return points.toArray(new double[points.size()][7]);
	}

	@Test
	public void test2() {
		for (double[] point : generatePoints()) {
			machine.addDataPoint(point[5] <= point[6] ? Category.POSITIVE : Category.NEGATIVE,
					point);
			machine2.addDataPoint(point[5] <= point[6] ? Category.POSITIVE : Category.NEGATIVE,
					point);
		}
		machine.train();
		machine2.train();
		System.out.println(">>> SVM RESULTS >>> ");
		System.out.println("Learned logic: " + machine.getLearnedLogic());
		System.out.println("Accuracy: " + machine.getModelAccuracy());
		System.out.println(">>> ENHANCED SVM RESULTS >>> ");
		System.out.println("Learned logic: " + machine2.getLearnedLogic());
		System.out.println("Accuracy: " + machine2.getModelAccuracy());
	}

}
