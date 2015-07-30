package libsvm.extension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import libsvm.core.Category;
import libsvm.core.Divider;
import libsvm.core.Machine;
import libsvm.core.Machine.DataPoint;

import org.junit.Assert;

public class SimpleSelectiveSamplingImpl implements ISelectiveSampling {

	private static final Random RANDOM = new Random();

	private final int numberOfNewPoints;

	public SimpleSelectiveSamplingImpl(final int numberOfNewPoints) {
		this.numberOfNewPoints = numberOfNewPoints;
	}

	@Override
	public List<DataPoint> selectData(final Machine machine) {
		// Copy current points
		List<DataPoint> points = new ArrayList<DataPoint>(machine.getDataPoints());

		List<DataPoint> pointsToAdd = new ArrayList<DataPoint>(numberOfNewPoints);
		while (pointsToAdd.size() < numberOfNewPoints) {
			// Choose a random sample point
			final DataPoint samplePoint = points.get(RANDOM.nextInt(points.size()));

			// Choose a feature
			final int availableFeatures = samplePoint.getNumberOfFeatures();
			final int selectedFeature = RANDOM.nextInt(availableFeatures);

			// Create a pool for existing values of the feature
			final double samplePointFeatureValue = samplePoint.getValue(selectedFeature);
			final Set<Double> valueSet = new HashSet<Double>();
			for (DataPoint point : points) {
				final double featureValue = point.getValue(selectedFeature);
				if (featureValue != samplePointFeatureValue) {
					valueSet.add(featureValue);
				}
			}

			if (valueSet.isEmpty()) {
				// Cannot create a new point
				// TODO try to improve this
				continue;
			}

			// Generate new point by using its existing values
			// But with a new one from the pool
			double[] generatedPoint = new double[availableFeatures];
			for (int i = 0; i < availableFeatures; i++) {
				if (i == selectedFeature) {
					generatedPoint[i] = new ArrayList<Double>(valueSet).get(
							RANDOM.nextInt(valueSet.size())).doubleValue();
				} else {
					generatedPoint[i] = samplePoint.getValue(i);
				}
			}

			// Evaluate the category of the new point
			final Category category = evaluate(generatedPoint, machine);

			// Add the new point to the existing points
			pointsToAdd.add(machine.createDataPoint(category, generatedPoint));
		}

		points.addAll(pointsToAdd);

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
