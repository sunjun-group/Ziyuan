/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package libsvm.core;

/**
 * @author khanh
 *
 */
public class CoefficientProcessing {

	//keep 9 digit in decimal
	private static int NUMBER_DECIMAL_TO_KEEP = 1000000000;
	private static final int BOUND_MIN_COEFFICIENT = 100;
	private static final double EPSILON = Math.pow(10, -1);
	
	public double[] process(Divider divider){
		double[] thetas = getFullThetas(divider);
		thetas = round(thetas);
		thetas = pivotMinCoefficient(thetas);
		thetas = integerRound(thetas);
		
		return thetas;
	}

	private double[] getFullThetas(Divider divider) {
		double[] oldThetas = divider.getThetas();
		//the last element is the theta0
		double[] thetas = new double[oldThetas.length + 1];
		for(int i = 0; i < thetas.length - 1; i++){
			thetas[i] = oldThetas[i];
		}
		thetas[thetas.length - 1] = divider.getTheta0();
		return thetas;
	}
	
	private double[] round(double[] thetas){
		double[] result = new double[thetas.length];
		for(int i = 0; i < thetas.length; i++){
			result[i] = Math.floor(thetas[i] * NUMBER_DECIMAL_TO_KEEP) / NUMBER_DECIMAL_TO_KEEP;
		}
		
		return result;
	}
	
	private double[] pivotMinCoefficient(double[] thetas) {
		double min = Double.MAX_VALUE;
		for(int i = 0; i < thetas.length; i++){
			double absCoefficient = Math.abs(thetas[i]);
			if(absCoefficient > 0 && absCoefficient < min){
				min = absCoefficient;
			}
		}
		
		double[] result = new double[thetas.length];
		for(int i = 0; i < thetas.length; i++){
			result[i] = thetas[i] / min;
		}
		
		return result;
	}
	
	/**
	 * Only make the coefficient of variables as integer
	 * Constant will rounded accordingly
	 * @param coefficients
	 * @return
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
				//update constant accordingly
				result[coefficients.length - 1] = Math.round(coefficients[coefficients.length - 1] * i);
				return result;
			}
		}
		
		return coefficients;
	}
	
	private boolean isApproximateInteger(double number){
		long roundingInteger = Math.round(number);
		return Math.abs(number - roundingInteger) < EPSILON;
	}
}
