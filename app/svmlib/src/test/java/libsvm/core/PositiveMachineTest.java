/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package libsvm.core;

import java.io.InputStream;

import libsvm.extension.NegativePointSelection;
import libsvm.extension.PositiveSeparationMachine;
import libsvm.extension.RandomNegativePointSelection;

import org.junit.Test;

/**
 * @author khanh
 *
 */
public class PositiveMachineTest extends TestUltility{

	private NegativePointSelection negativeSelection = new RandomNegativePointSelection();
	
	@Test
	public void whenRequireTwoDividers() {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("RequireTwoDividers.txt");
		runTest(new PositiveSeparationMachine(negativeSelection), 2, inputStream);
		
		checkAccuracy(1);
	}
	
	@Test
	public void whenThereAre120PointsLessPositive() {
		//x0 + x1 <= 5 && x0 - x1 >= 1
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("2Div50Pos70Neg.txt");
		runTest(new PositiveSeparationMachine(negativeSelection), 2, inputStream);
		
		checkAccuracy(1);
	}
	
	@Test
	public void whenThereAre120PointsMorePositive() {
		//x0 + x1 <= 5 && x0 - x1 <=5
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("2Div90Pos30Neg.txt");
		runTest(new PositiveSeparationMachine(negativeSelection), 2, inputStream);
		
		checkAccuracy(1);
	}
	
	@Test
	public void whenThereAre60Points() {
		//x0 + x1 <= 2 && x0 - x1 <= 3
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("2Div30PosNeg.txt");
		runTest(new PositiveSeparationMachine(negativeSelection), 2, inputStream);
		
		checkAccuracy(1);
	}
}
