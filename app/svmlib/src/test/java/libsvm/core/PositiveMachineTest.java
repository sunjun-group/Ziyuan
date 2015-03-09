/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package libsvm.core;

import java.io.InputStream;

import libsvm.extension.PositiveSeparationMachine;

import org.junit.Test;

/**
 * @author khanh
 *
 */
public class PositiveMachineTest extends TestUltility{

	@Test
	public void whenThereAreTwoFeatures() {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("TwoFeatures.txt");
		runTest(new PositiveSeparationMachine(), 2, new double[]{-2, -3, -15}, inputStream);
	}

	@Test
	public void whenThereAreThreeFeatures() {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ThreeFeatures.txt");
		runTest(new PositiveSeparationMachine(), 3, new double[]{3, 7, 19, 80}, inputStream);
	}
	
	@Test
	public void whenContainsManyDuplicateDividers() {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ManyDuplicatedCuts.txt");
		runTest(new PositiveSeparationMachine(), 3, new double[]{3, 7, 19, 80}, inputStream);
	}
	
//	@Test
//	public void whenRequireTwoDividers() {
//		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("RequireTwoDividers.txt");
//		runTest(new PositiveSeparationMachine(), 2, new double[]{3, 7, 80}, inputStream);
//	}
}
