package libsvm.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import libsvm.core.Category;
import libsvm.core.Divider;
import libsvm.core.Machine;
import libsvm.core.Machine.DataPoint;

import org.apache.log4j.Logger;

public abstract class AbstractSimpleSelectiveSamplingImpl implements ISelectiveSampling {
	private static final Logger LOGGER = Logger
			.getLogger(AbstractSimpleSelectiveSamplingImpl.class);
	private static final Random RANDOM = new Random();

	private final int minNewPoints;

	/**
	 * @param minNewPoints
	 *            The minimum number of points to be generated at each attempt
	 */
	public AbstractSimpleSelectiveSamplingImpl(final int minNewPoints) {
		this.minNewPoints = minNewPoints;
	}

	public AbstractSimpleSelectiveSamplingImpl() {
		this(1);
	}

	@Override
	public List<DataPoint> selectData(final Machine machine) {
		// Copy current points
		List<DataPoint> points = new ArrayList<DataPoint>(machine.getDataPoints());
		List<DataPoint> newPoints = new ArrayList<Machine.DataPoint>();
		final int maxAttempt = points.size() * 2;
		int attempt = 0;
		int pointsAdded = 0;
		while (attempt < maxAttempt) {
			attempt++;
			// Choose a random sample point
			final DataPoint samplePoint = points.get(RANDOM.nextInt(points.size()));
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Using point " + samplePoint + " as sample point.");
			}
			final int availableFeatures = samplePoint.getNumberOfFeatures();

			for (int i = 0; i < availableFeatures; i++) {
				final Divider divider = machine.getModel().getExplicitDivider();
				final double[] thetas = divider.getThetas();
				double theta0 = divider.getTheta0();

				double[] generatedPoint = new double[availableFeatures];
				for (int j = 0; j < availableFeatures; j++) {
					if (j != i) {
						final double value = samplePoint.getValue(j);
						generatedPoint[j] = value;
						theta0 -= value * thetas[j];
					}
				}
				generatedPoint[i] = new Double(theta0 / thetas[i]).intValue();

				// Add the new point to the existing points
				if (addPoint(machine, generatedPoint, newPoints)) {
					pointsAdded++;
					if (pointsAdded >= minNewPoints) {
						attempt = maxAttempt;
					}
					// Also try to add near by points
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Attempt to add nearby points...");
					}
					for (int j = 0; j < availableFeatures; j++) {
						for (int k = 0; k < availableFeatures; k++) {
							for (int op = 0; op < 2; op++) {
								double[] anotherPoint = new double[availableFeatures];
								for (int x = 0; x < availableFeatures; x++) {
									if (x == k) {
										anotherPoint[x] = generatedPoint[x] + (op == 0 ? 1 : -1);
									} else {
										anotherPoint[x] = generatedPoint[x];
									}
								}
								addPoint(machine, anotherPoint, newPoints);
							}
						}
					}
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("...Done.");
					}
				}
			}
		}

		return newPoints;
	}

	private boolean addPoint(Machine machine, double[] pointValues, List<DataPoint> points) {
		final DataPoint point = machine
				.createDataPoint(evaluate(pointValues, machine), pointValues);
		if (!points.contains(point)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Point to add: " + point);
			}
			points.add(point);
			return true;
		}
		return false;
	}

	/**
	 * This method is used to determine whether the given point is consider
	 * POSITIVE or NEGATIVE in the given Machine.
	 * 
	 * @param generatedPoint
	 *            The point to be evaluated.
	 * @param machine
	 *            The machine for which the point is evaluated.
	 * @return Category.POSITIVE or Category.NEGATIVE. This method must never
	 *         return null.
	 */
	protected abstract Category evaluate(double[] generatedPoint, Machine machine);

}
