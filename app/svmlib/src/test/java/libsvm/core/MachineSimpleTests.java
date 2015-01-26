package libsvm.core;

import java.util.Random;

import libsvm.binary.MultiCutMachine;
import libsvm.core.Machine.Category;

import org.junit.Test;

public class MachineSimpleTests {

	private static final int NUMBER_OF_FEATURES = 2;
	private static final int NUMBER_OF_DATA_POINTS = 100;
	private static final String[] CATEGORIES = { "POSSITIVE", "NEGATIVE" };
	private static final Random RANDOM = new Random();

	@Test
	public void testAccuracy() {
		Machine machine = new Machine().setParameter(new Parameter()
				.setMachineType(MachineType.C_SVC).setKernelType(KernelType.LINEAR).setC(1.0)
				.setCacheSize(100.0).setEps(1e-3).setShrinking(1).setProbability(1).setNrWeight(0)
				.setWeight(new double[0]).setWeightLabel(new int[0]));

		Machine m_machine = new MultiCutMachine().setParameter(new Parameter()
				.setMachineType(MachineType.C_SVC).setKernelType(KernelType.LINEAR).setC(1.0)
				.setCacheSize(100.0).setEps(1e-3).setShrinking(1).setProbability(1).setNrWeight(0)
				.setWeight(new double[0]).setWeightLabel(new int[0]));

		for (int i = 0; i < NUMBER_OF_DATA_POINTS; i++) {
			final String categoryString = CATEGORIES[RANDOM.nextInt(2)];
			final double[] values = { Math.random(), Math.random() };

			machine.addDataPoint(randomDataPoint(machine.getCategory(categoryString), values));
			m_machine.addDataPoint(randomDataPoint(m_machine.getCategory(categoryString), values));
		}

		System.out.println("********************");
		System.out.println("Normal SVM:" + machine.train().getModelAccuracy());
		System.out.println("********************");
		System.out.println("Improved SVM:" + m_machine.train().getModelAccuracy());
	}

	private DataPoint randomDataPoint(final Category category, final double... values) {
		final DataPoint dp = new DataPoint(NUMBER_OF_FEATURES);
		dp.setValues(values);
		dp.setCategory(category);
		return dp;
	}

}
