/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package libsvm.extension;

import java.util.List;

import libsvm.core.Machine.DataPoint;

/**
 * @author khanh
 *
 */
public class ByDistanceNegativePointSelection implements NegativePointSelection{
	/**
	 * Return Pareto points
	 * No other points has value greater/smaller
	 */
	public DataPoint select(List<DataPoint> negatives, List<DataPoint> positives){
		DataPoint randomPositive = getRandomPositive(positives);
		
		double minDistance = Integer.MAX_VALUE;
		
		DataPoint result = null;
		for(DataPoint negative: negatives){
			double distance = computeDistance(negative, randomPositive);
			if(distance < minDistance){
				minDistance = distance;
				result = negative;
			}
		}
		
		System.out.print("Selection Negative: " + result);
		return result;
	}
	
	public DataPoint getRandomPositive(List<DataPoint> positives){
		int randomIndex = (int) (positives.size() * Math.random());
		return positives.get(randomIndex);
	}
	private double computeDistance(DataPoint negative, DataPoint randomPositive){
		double result = 0;
		
		for(int i = 0; i < negative.getNumberOfFeatures(); i++){
			result += Math.pow(randomPositive.getValue(i) - negative.getValue(i), 2);
		}
		
		return Math.sqrt(result);
	}
}
