/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package libsvm.core;

import java.io.InputStream;
import java.util.Scanner;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;

/**
 * @author khanh
 *
 */
public class TestUltility {

	protected static final Logger LOGGER = Logger.getLogger(MachineSimpleTests.class);

	protected Machine machine;
	
	protected Machine setupMachine(Machine defaultMachine, int numberOfFeatures) {
		return defaultMachine.setNumberOfFeatures(numberOfFeatures).setParameter(
				new Parameter().setMachineType(MachineType.C_SVC).setKernelType(KernelType.LINEAR)
						.setEps(1.0).setUseShrinking(false).setPredictProbability(false));
	}
	
	protected void runTest(Machine defaultMachine, int numOfFeatures, double[] expectedCoefficients, InputStream inputStream) {
		machine = setupMachine(defaultMachine, numOfFeatures);
		
		readDataFromFile(inputStream, numOfFeatures);

		final double normalModelAccuracy = machine.train().getModelAccuracy();
		log(normalModelAccuracy);
		
		checkLastDivider(expectedCoefficients);
	}

	
	
	private void readDataFromFile(InputStream inputStream, int numberOfFeatures){
		Scanner scanner = new Scanner(inputStream);
		readDataOfCategory(scanner, numberOfFeatures, Category.POSITIVE);
		readDataOfCategory(scanner, numberOfFeatures, Category.NEGATIVE);
	}

	private void readDataOfCategory(Scanner scanner, int numberOfFeatures,
			Category category) {
		int numberOfPositivePoints = scanner.nextInt();
		for (int i = 0; i < numberOfPositivePoints; i++) {
			double [] data = readIntegerArray(scanner, numberOfFeatures);
			machine.addDataPoint(category, data);
		}
	}
	
	private double[] readIntegerArray(Scanner scanner, int length){
		double[] result = new double[length];
		for(int i = 0; i < length; i++){
			result[i] = scanner.nextInt();
		}
		
		return result;
	}
	
	private void log(final double normalModelAccuracy) {
		LOGGER.log(Level.DEBUG, "Normal SVM:" + normalModelAccuracy);
		
		LOGGER.info("Learned logic:");
		LOGGER.info("Normal machine:\n" + machine.getLearnedLogic());
	}
	
	private void checkLastDivider(double[] expectedCoefficients) {
		Divider divider = machine.getModel().getExplicitDivider();
		double[] coefficients = new CoefficientProcessing().process(divider);
		compareCoefficients(expectedCoefficients, coefficients);
	}

	/**
	 * Compare 2 arrays, each pair has small different in EPSILON rate
	 */
	private void compareCoefficients(double[] expectedCoefficients, double[] coefficients) {
		Assert.assertEquals(expectedCoefficients.length, coefficients.length);
		
		double EPSILON = 0.2;
		for(int i = 0; i < coefficients.length; i++){
			double errorRate = Math.abs((coefficients[i] - expectedCoefficients[i]) / expectedCoefficients[i]);
			Assert.assertTrue(errorRate < EPSILON);
		}
	}
}
