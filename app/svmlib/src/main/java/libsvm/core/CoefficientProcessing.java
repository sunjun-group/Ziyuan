/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package libsvm.core;

import java.util.Arrays;


/**
 * @author khanh
 *
 */
public class CoefficientProcessing {

	private static final int BOUND_MIN_COEFFICIENT = 100; //min coefficient is bounded
	private static final double MAX_DIFFERENCE_TO_NEAREST_INTEGER = Math.pow(10, -1);
	private static final double EPSILON = Math.pow(10, -6);
	private static final double NUMBER_DECIMAL_TO_KEEP = 1000000000;
	private static final int BOUND_RATE_MIN_MAX_COEFFICIENT = 100; //max coefficient / min coefficient is bounded
		
	public double[] process(double[] thetas) {
		double[] backup = Arrays.copyOf(thetas, thetas.length);
		
		thetas = integerRounding(thetas);
		
		if(isValidated(thetas)){
			return thetas;
		}
		else{
			return backup;
		}
	}
	
	private double[] integerRounding(double[] thetas) {
		thetas = detectZeroCoefficient(thetas);
		thetas = truncateDecimal(thetas);
		thetas = pivotMinCoefficient(thetas);
 		thetas = integerRound(thetas);
		
		return thetas;
	}

	/**
	 * Value less than EPSILON considered as zero
	 * But if after that having all zero, then just return original
	 */
	private double[] detectZeroCoefficient(double[] thetas){
		double[] result = new double[thetas.length];
		int countZero = 0;
		for(int i = 0; i < thetas.length; i++){
			if(Math.abs(thetas[i]) < EPSILON){
				result[i] = 0;
				countZero++;
			}
			else{
				result[i] = thetas[i];
			}
		}
		
		if(countZero == thetas.length){
			return thetas;
		}
		return result;
	}

	private double[] truncateDecimal(double[] thetas){
		double[] result = new double[thetas.length];
		for(int i = 0; i < thetas.length; i++){
			result[i] = Math.round(thetas[i] * NUMBER_DECIMAL_TO_KEEP)/ NUMBER_DECIMAL_TO_KEEP;
		}
		return result;
	}
	
	private double[] pivotMinCoefficient(double[] thetas) {
		double min = Double.MAX_VALUE;
		
		//only find min for coefficient of variables, not constant
		for(int i = 0; i < thetas.length - 1; i++){
			double absCoefficient = Math.abs(thetas[i]);
			if(absCoefficient > 0 && absCoefficient < min){
				min = absCoefficient;
			}
		}
		
		double[] result = new double[thetas.length];
		double maxRate = Double.MIN_VALUE;
		for(int i = 0; i < thetas.length; i++){
			result[i] = thetas[i] / min;
			
			double absCoefficient = Math.abs(result[i]);
			if(absCoefficient > maxRate){
				maxRate = absCoefficient;
			}
			
		}
		
		if(maxRate > BOUND_RATE_MIN_MAX_COEFFICIENT){
			//if the value is too small compared with others, we reset it is as zero
			for(int i = 0; i < thetas.length; i++){
				double absCoefficient = Math.abs(result[i]);
				if(Double.compare(absCoefficient, 1) == 0){
					result[i] = 0;
				}
			}
			
			return integerRounding(result);
		}
		return result;
	}
	
	/**
	 * Only make the coefficient of variables as integer
	 * Constant will rounded accordingly
	 */
	private double[] integerRound(double[] coefficients){
		double[] result = new double[coefficients.length];
		
		for(int i = 1; i <= BOUND_MIN_COEFFICIENT; i++){
			boolean allCoefficientsInteger = true;
			//try to make coefficient of variables as integer
			for(int j = 0; j < coefficients.length - 1; j++){
				double newCoefficient = coefficients[j] * i;
				if(isApproximateInteger(newCoefficient)){
					result[j] = Math.round(newCoefficient);
				}
				else{
					allCoefficientsInteger = false;
					break;
				}
			}
			
			if(allCoefficientsInteger){
				//update constant accordingly, we must take the ceiling because the divider for positive is in the form
				//ax+by >= c, unless the difference is extremmely small, 1.99999999999999
				double value = coefficients[coefficients.length - 1] * i;
				if(value - Math.floor(value) < EPSILON){
					result[coefficients.length - 1] = Math.floor(value);
				}
				else{
					result[coefficients.length - 1] = Math.ceil(value);
				}
				return result;
			}
		}
		
		return coefficients;
	}
	
	private boolean isApproximateInteger(double number){
		long roundingInteger = Math.round(number);
		return Math.abs(number - roundingInteger) < MAX_DIFFERENCE_TO_NEAREST_INTEGER;
	}
	
	private boolean isValidated(double[] numbers){
		for(int i = 0; i < numbers.length - 1; i++){
			double number = numbers[i];
			if(Double.compare(number, 0) != 0){
					return true;
			}
		}
		
		return false;
	}
}
