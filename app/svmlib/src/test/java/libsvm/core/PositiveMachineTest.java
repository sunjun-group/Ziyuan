/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package libsvm.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import libsvm.extension.PositiveSeparationMachine;
import libsvm.extension.RandomNegativePointSelection;

import org.junit.Test;

/**
 * @author khanh
 *
 */
public class PositiveMachineTest extends TestUltility{

	@Test
	public void whenThereAreTwoFeatures() {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("TwoFeatures.txt");
		runTest(new PositiveSeparationMachine(new RandomNegativePointSelection()), 2, inputStream);
		checkLastDividerFound(new double[]{-2, -3, -15});
	}

	@Test
	public void whenThereAreThreeFeatures() {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ThreeFeatures.txt");
		runTest(new PositiveSeparationMachine(new RandomNegativePointSelection()), 3, inputStream);
		checkLastDividerFound(new double[]{3, 7, 19, 80});
	}
	
	@Test
	public void whenContainsManyDuplicateDividers() {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ManyDuplicatedCuts.txt");
		runTest(new PositiveSeparationMachine(new RandomNegativePointSelection()), 3, inputStream);
		checkLastDividerFound(new double[]{3, 7, 19, 80});
	}
	
	@Test
	public void whenRequireTwoDividers() {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("RequireTwoDividers.txt");
		runTest(new PositiveSeparationMachine(new RandomNegativePointSelection()), 2, inputStream);
		
		//TODO: find a way to test result when model includes 2 dividers
	}
	
	@Test
	public void whenThereAre120PointsLessPositive() {
		//x0 + x1 <= 5 && x0 - x1 >= 1
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("2Div40Pos80Neg.txt");
		runTest(new PositiveSeparationMachine(new RandomNegativePointSelection()), 2, inputStream);
		
		//TODO: find a way to test result when model includes 2 dividers
	}
	
	@Test
	public void whenThereAre120PointsMorePositive() {
		//x0 + x1 <= 5 && x0 - x1 <=5
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("2Div90Pos30Neg.txt");
		runTest(new PositiveSeparationMachine(new RandomNegativePointSelection()), 2, inputStream);
		
		//TODO: find a way to test result when model includes 2 dividers
	}
	
	@Test
	public void whenThereAre60Points() {
		//x0 + x1 <= 2 && x0 - x1 <= 3
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("2Div30PosNeg.txt");
		runTest(new PositiveSeparationMachine(new RandomNegativePointSelection()), 2, inputStream);
		
		//TODO: find a way to test result when model includes 2 dividers
	}
}
