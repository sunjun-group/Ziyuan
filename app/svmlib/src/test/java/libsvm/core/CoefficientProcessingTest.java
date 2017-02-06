/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package libsvm.core;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author khanh
 *
 */
public class CoefficientProcessingTest {

	private CoefficientProcessing coefficientProcessing = new CoefficientProcessing();
	private double DELTA = Math.pow(10, -6);
	
	@Test
	public void whenNumberIsVerySmall() {
		double[] input = new double[]{0.5, -0.5, Math.pow(10, -12)};
		double[] result = coefficientProcessing.process(input);
		double[] expected = new double[]{1,  -1, 0};
		Assert.assertArrayEquals(expected, result, DELTA);
	}
	
	@Test
	public void whenRateIsNotOneOne() {
		double[] input = new double[]{1, 4.0/3, 7.0/3};
		double[] result = coefficientProcessing.process(input);
		double[] expected = new double[]{3, 4, 7};
		Assert.assertArrayEquals(expected, result, DELTA);
	}
	

	@Test
	public void whenNumberIsExtremlyNearInteger() {
		double[] input = new double[]{0.5, -0.5, -2.999999999999991};
		double[] result = coefficientProcessing.process(input);
		double[] expected = new double[]{1,  -1, -6};
		Assert.assertArrayEquals(expected, result, DELTA);
	}
	
	@Test
	public void whenNumberIsQuiteNearInteger() {
		double[] input = new double[]{0.501, 1.01, 3.015};
		double[] result = coefficientProcessing.process(input);
		double[] expected = new double[]{1,  2, 7};
		Assert.assertArrayEquals(expected, result, DELTA);
	}
	
	@Test
	public void whenNumberHavingHalf() {
		double[] input = new double[]{-0.5, -0.5, -3.5};
		double[] result = coefficientProcessing.process(input);
		double[] expected = new double[]{-1.0, -1.0, -7.0};
		Assert.assertArrayEquals(expected, result, DELTA);
	}
	
	@Test
	public void whenNumberHavingLongZeroInDecimal() {
		double[] input = new double[]{-0.5, -0.5, -3.500000000000001};
		double[] result = coefficientProcessing.process(input);
		double[] expected = new double[]{-1.0, -1.0, -7.0};
		Assert.assertArrayEquals(expected, result, DELTA);
	}
	
	@Test
	public void whenNumberHavingLongNineInDecimal() {
		double[] input = new double[]{-0.4999999999999, -0.5, -3.5};
		double[] result = coefficientProcessing.process(input);
		double[] expected = new double[]{-1.0, -1.0, -7.0};
		Assert.assertArrayEquals(expected, result, DELTA);
	}
	
	@Test
	public void whenConstantIsVerySmall() {
		double[] input = new double[]{1, 1, 0.3};
		double[] result = coefficientProcessing.process(input);
		double[] expected = new double[]{1, 1, 1};
		Assert.assertArrayEquals(expected, result, DELTA);
	}
	
	

}
