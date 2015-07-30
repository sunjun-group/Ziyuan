package icsetlv.sampling;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import libsvm.core.Category;
import libsvm.core.Divider;
import libsvm.core.Machine;
import libsvm.core.Machine.DataPoint;
import libsvm.extension.ISelectiveSampling;

import org.junit.Assert;

public class SimpleSelectiveSamplingImpl implements ISelectiveSampling {

	private static final Random RANDOM = new Random();

	@Override
	public List<DataPoint> selectData(final Machine machine) {
		// Copy current points
		List<DataPoint> points = new ArrayList<DataPoint>(machine.getDataPoints());
		// Select a random existing point
		final DataPoint samplePoint = points.get(RANDOM.nextInt(points.size()));
		// Create a new point by copying the existing point
		// And generate value for 1 of its features
		final int availableFeatures = samplePoint.getNumberOfFeatures();
		final int selectedFeature = RANDOM.nextInt(availableFeatures);
		double[] generatedPoint = new double[availableFeatures];
		for (int i = 0; i < availableFeatures; i++) {
			if (i == selectedFeature) {
				generatedPoint[i] = RANDOM.nextDouble();
			} else {
				generatedPoint[i] = samplePoint.getValue(i);
			}
		}

		// Evaluate the category of the new point
		final Category category = evaluate(generatedPoint, machine);

		// Add the new point to the existing points
		points.add(machine.createDataPoint(category, generatedPoint));

		return points;
	}

	protected Category evaluate(double[] generatedPoint, Machine machine) {
		final Divider divider = machine.getModel().getExplicitDivider();
		final double[] thetas = divider.getThetas();
		final double theta0 = divider.getTheta0();
		double value = 0.0;
		Assert.assertTrue("Generated points and existing divider are not matched.",
				thetas.length == generatedPoint.length);
		for (int i = 0; i < thetas.length; i++) {
			value += thetas[i] * generatedPoint[i];
		}

		// The divider is theta[0]*x0 + theta[1]*x1 + ... + theta[n]*xn = theta0
		// If theta[0]*x0 + theta[1]*x1 + ... + theta[n]*xn >= theta0
		// Then the point is positive

		return Double.compare(value, theta0) >= 0 ? Category.POSITIVE : Category.NEGATIVE;
	}

}
