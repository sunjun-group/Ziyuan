/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package libsvm.core;

import java.io.InputStream;
import libsvm.extension.ByDistanceNegativePointSelection;
import libsvm.extension.MultiAttemptMachine;
import libsvm.extension.MultiCutMachine;
import libsvm.extension.NegativePointSelection;
import libsvm.extension.PositiveSeparationMachine;
import sav.settings.SAVExecutionTimeOutException;

import org.junit.Test;

/**
 * @author khanh
 *
 */
public class PositiveMachineTest extends TestUltility{

	private NegativePointSelection negativeSelection = new ByDistanceNegativePointSelection();
	
	@Test
	public void whenThereAreMyTwoFeatures() throws SAVExecutionTimeOutException {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("MyTwoFeatures.txt");
		runTest(new PositiveSeparationMachine(negativeSelection), 2, inputStream);
		// runTest(new MultiAttemptMachine(), 2, inputStream);
		
		checkAccuracy(1);
	}
	
	@Test
	public void whenRequireTwoDividers() throws SAVExecutionTimeOutException {
		// -1.0*x0 -1.0*x1 >= -10.0 && 1.0*x0 -1.0*x1 >= -10.0
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("RequireTwoDividers.txt");
		runTest(new PositiveSeparationMachine(negativeSelection), 2, inputStream);
		
		checkAccuracy(1);
	}
	
	@Test
	public void whenThereAre120PointsLessPositive() throws SAVExecutionTimeOutException {
		//x0 + x1 <= 5 && x0 - x1 >= 1
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("2Div50Pos70Neg.txt");
		runTest(new PositiveSeparationMachine(negativeSelection), 2, inputStream);
		
		checkAccuracy(1);
	}
	
	@Test
	public void whenThereAre120PointsMorePositive() throws SAVExecutionTimeOutException {
		//x0 + x1 <= 5 && x0 - x1 <=5
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("2Div90Pos30Neg.txt");
		runTest(new PositiveSeparationMachine(negativeSelection), 2, inputStream);
		
		checkAccuracy(1);
	}
	
	@Test
	public void whenThereAre60Points() throws SAVExecutionTimeOutException {
		//x0 + x1 <= 2 && x0 - x1 <= 3
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("2Div30PosNeg.txt");
		runTest(new PositiveSeparationMachine(negativeSelection), 2, inputStream);
		
		checkAccuracy(1);
	}
	
	@Test
	public void when60PointsSparse() throws SAVExecutionTimeOutException {
		//i + 2*j >= 15 && i - j <= 10
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("2Div25Pos38NegSparse.txt");
		runTest(new PositiveSeparationMachine(negativeSelection), 2, inputStream);
		
		checkAccuracy(1);
	}
	
	@Test
	public void when30PointsSparse() throws SAVExecutionTimeOutException {
		//i + 2*j >= 15 && i - j <= 10
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("2Div9Pos19NegSparse.txt");
		runTest(new PositiveSeparationMachine(negativeSelection), 2, inputStream);
		
		checkAccuracy(1);
	}
	
	@Test
	public void whenSparseAndOddCoefficient50Points() throws SAVExecutionTimeOutException {
		//2*i + 3*j >= 10 && 3*i - 8*j >= 14
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("2DivSparse50PointsOddCoefficient.txt");
		runTest(new PositiveSeparationMachine(negativeSelection), 2, inputStream);
		
		checkAccuracy(1);
	}
	
	@Test
	public void whenSparseAndOddCoefficient40Points() throws SAVExecutionTimeOutException {
		//2*i + 3*j >= 10 && 3*i - 8*j >= 14
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("2DivSparse40PointsOddCoefficient.txt");
		runTest(new PositiveSeparationMachine(negativeSelection), 2, inputStream);
		
		checkAccuracy(1);
	}
}
