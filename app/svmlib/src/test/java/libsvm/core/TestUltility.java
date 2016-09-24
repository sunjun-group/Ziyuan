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
import java.util.logging.Level;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author khanh
 *
 */
public class TestUltility {

	protected static final Logger LOGGER = LoggerFactory.getLogger(MachineSimpleTests.class);

	protected Machine machine;
	
	protected Machine setupMachine(Machine defaultMachine, int numberOfFeatures) {
		return defaultMachine.setNumberOfFeatures(numberOfFeatures).setParameter(
				new Parameter().setMachineType(MachineType.C_SVC).setKernelType(KernelType.LINEAR)
						.setEps(0.00001).setUseShrinking(false).setPredictProbability(false).setC(Double.MAX_VALUE));
	}
	
	protected void runTest(Machine defaultMachine, int numOfFeatures, InputStream inputStream) {
		machine = setupMachine(defaultMachine, numOfFeatures);
		
		readDataFromFile(inputStream, numOfFeatures);

		final double normalModelAccuracy = machine.train().getModelAccuracy();
		log(normalModelAccuracy);
	}
	
	/**
	 * File structure
	 * numberOfPositives numberOfNegatives
	 * (positive point)*
	 * (negative point)*
	 */
	private void readDataFromFile(InputStream inputStream, int numberOfFeatures){
		Scanner scanner = new Scanner(inputStream);
		int numberOfPositives = scanner.nextInt();
		int numberOfNegatives = scanner.nextInt();
		
		readDataOfCategory(scanner, numberOfFeatures, numberOfPositives, Category.POSITIVE);
		readDataOfCategory(scanner, numberOfFeatures, numberOfNegatives, Category.NEGATIVE);
	}

	private void readDataOfCategory(Scanner scanner, int numberOfFeatures, int numberOfPoints,
			Category category) {
		for (int i = 0; i < numberOfPoints; i++) {
			double [] data = readIntegerArray(scanner, numberOfFeatures);
			machine.addDataPoint(category, data);
		}
	}
	
	private double[] readIntegerArray(Scanner scanner, int length){
		double[] result = new double[length];
		for(int i = 0; i < length; i++){
			result[i] = scanner.nextDouble();
		}
		
		return result;
	}
	
	private void log(final double normalModelAccuracy) {
		LOGGER.debug("SVM:" + normalModelAccuracy);
		
		LOGGER.info("Learned logic:");
		LOGGER.info(machine.getLearnedLogic(false));
		
		System.out.println("\n\n" + machine.getLearnedLogic(true));
	}
	
	protected void checkLastDividerFound(double[] expectedCoefficients) {
		Divider divider = machine.getModel().getExplicitDivider();
		double[] coefficients = new CoefficientProcessing().process(divider.getLinearExpr());
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
	
	protected void checkAccuracy(double expectAccuracy){
		double accuracy = machine.getModelAccuracy();
		Assert.assertTrue("Expected accuracy of " + expectAccuracy + " but only have " + accuracy, accuracy >= expectAccuracy);
	}
	
//	public void templateToGenerateRandomData(){
//		//x + y >= 5;
//		//-5x + 3y >= -10
//		List<int[]> positives = new ArrayList<int[]>();
//		List<int[]> negatives = new ArrayList<int[]>();
//		for(int i = -20; i <= 20; i++){
//			for(int j = -20; j <= 20; j++){
//				if(i + j >= 5 && -5*i + 3*j >= -10){
//					positives.add(new int[]{i, j});
//				}
//				else{
//					negatives.add(new int[]{i, j});
//				}
//			}
//		}
//		
//		logPoints(positives);
//		logPoints(negatives);
//	}
//
//	/**
//	 * @param points
//	 */
//	private void logPoints(List<int[]> points) {
//		LOGGER.info(points.size());
//		for(int[] point: points){
//			StringBuilder line = new StringBuilder();
//			for(int number: point){
//				line.append(number + " ");
//			}
//			LOGGER.info(line);
//		}
//	}
}
