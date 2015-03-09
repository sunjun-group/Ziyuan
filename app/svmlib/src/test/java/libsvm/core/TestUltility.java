/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package libsvm.core;

import org.apache.log4j.Logger;
import org.junit.Assert;

/**
 * @author khanh
 *
 */
public class TestUltility {

	protected static final Logger LOGGER = Logger.getLogger(MachineSimpleTests.class);

	protected static final int NUMBER_OF_FEATURES = 2;
	protected static final int NUMBER_OF_DATA_POINTS = 1000;
	
	protected Machine setupMachine(final Machine machine, int numberOfFeatures) {
		return machine.setNumberOfFeatures(numberOfFeatures).setParameter(
				new Parameter().setMachineType(MachineType.C_SVC).setKernelType(KernelType.LINEAR)
						.setEps(1.0).setUseShrinking(false).setPredictProbability(false));
	}
	
	/**
	 * @param expectedCoefficients
	 * @param coefficients
	 */
	protected void compareCoefficients(double[] expectedCoefficients, double[] coefficients) {
		double EPSILON = 0.2;
		for(int i = 0; i < coefficients.length; i++){
			double errorRate = Math.abs((coefficients[i] - expectedCoefficients[i]) / expectedCoefficients[i]);
			Assert.assertTrue(errorRate < EPSILON);
		}
	}
}
