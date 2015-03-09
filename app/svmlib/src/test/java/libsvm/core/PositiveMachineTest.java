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

import libsvm.extension.PositiveSeparationMachine;

import org.apache.log4j.Level;
import org.junit.Test;

/**
 * @author khanh
 *
 */
public class PositiveMachineTest extends TestUltility{

	private Machine positiveMachine;
	
	@Test
	public void whenThereAreTwoFeatures() {
		positiveMachine = setupMachine(new PositiveSeparationMachine(), 2);
		
		// Separator: ax + by = c
		int x, y;
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("TwoFeatures.txt");
		Scanner scanner = new Scanner(inputStream);
		int numberOfPositivePoints = scanner.nextInt();
		
		for (int i = 0; i < numberOfPositivePoints; i++) {
			Category category = Category.POSITIVE;
			x = scanner.nextInt();
			y = scanner.nextInt();
			positiveMachine.addDataPoint(category, x, y);
		}

		int numberOfNegativePoints = scanner.nextInt();
		for (int i = 0; i < numberOfNegativePoints; i++) {
			Category category = Category.NEGATIVE;
			x = scanner.nextInt();
			y = scanner.nextInt();
			positiveMachine.addDataPoint(category, x, y);
		}

		final double normalModelAccuracy = positiveMachine.train().getModelAccuracy();
		LOGGER.log(Level.DEBUG, "Positive SVM:" + normalModelAccuracy);
		
		LOGGER.info("Learned logic:");
		LOGGER.info("Positive machine:\n" + positiveMachine.getLearnedLogic());
		
		Divider divider = positiveMachine.getModel().getExplicitDivider();
		double[] expectedCoefficients = new double[]{-2, -3, -15};
		
		double[] coefficients = new CoefficientProcessing().process(divider);
		compareCoefficients(expectedCoefficients, coefficients);
	}

	@Test
	public void whenThereAreThreeFeatures() {
		positiveMachine = setupMachine(new PositiveSeparationMachine(), 3);
		
		// Separator: ax + by + cz = d
		int x, y, z;
		
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ThreeFeatures.txt");
		Scanner scanner = new Scanner(inputStream);
		int numberOfPositivePoints = scanner.nextInt();
		
		for (int i = 0; i < numberOfPositivePoints; i++) {
			Category category = Category.POSITIVE;
			x = scanner.nextInt();
			y = scanner.nextInt();
			z = scanner.nextInt();

			positiveMachine.addDataPoint(category, x, y, z);
		}
		
		int numberOfNegativePoints = scanner.nextInt();
		for (int i = 0; i < numberOfNegativePoints; i++) {
			Category category = Category.NEGATIVE;
			x = scanner.nextInt();
			y = scanner.nextInt();
			z = scanner.nextInt();

			positiveMachine.addDataPoint(category, x, y, z);
		}

		final double normalModelAccuracy = positiveMachine.train().getModelAccuracy();
		LOGGER.log(Level.DEBUG, "Positive SVM:" + normalModelAccuracy);
		
		LOGGER.info("Learned logic:");
		LOGGER.info("Positive machine:\n" + positiveMachine.getLearnedLogic());		
		
		Divider divider = positiveMachine.getModel().getExplicitDivider();
		double[] expectedCoefficients = new double[]{3, 7, 19, 80};
		
		double[] coefficients = new CoefficientProcessing().process(divider);
		compareCoefficients(expectedCoefficients, coefficients);
	}
	
	@Test
	public void whenThereAreThreeFeaturesButHavingManyDuplicateCuts() {
		positiveMachine = setupMachine(new PositiveSeparationMachine(), 3);
		
		// Separator: ax + by + cz = d
		int x, y, z;
		
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ManyDuplicatedCuts.txt");
		Scanner scanner = new Scanner(inputStream);
		int numberOfPositivePoints = scanner.nextInt();
		
		for (int i = 0; i < numberOfPositivePoints; i++) {
			Category category = Category.POSITIVE;
			x = scanner.nextInt();
			y = scanner.nextInt();
			z = scanner.nextInt();

			positiveMachine.addDataPoint(category, x, y, z);
		}
		
		int numberOfNegativePoints = scanner.nextInt();
		for (int i = 0; i < numberOfNegativePoints; i++) {
			Category category = Category.NEGATIVE;
			x = scanner.nextInt();
			y = scanner.nextInt();
			z = scanner.nextInt();

			positiveMachine.addDataPoint(category, x, y, z);
		}

		final double normalModelAccuracy = positiveMachine.train().getModelAccuracy();
		LOGGER.log(Level.DEBUG, "Positive SVM:" + normalModelAccuracy);
		
		LOGGER.info("Learned logic:");
		LOGGER.info("Positive machine:\n" + positiveMachine.getLearnedLogic());		
		
		Divider divider = positiveMachine.getModel().getExplicitDivider();
		double[] expectedCoefficients = new double[]{3, 7, 19, 80};
		
		double[] coefficients = new CoefficientProcessing().process(divider);
		compareCoefficients(expectedCoefficients, coefficients);
	}
	
}
