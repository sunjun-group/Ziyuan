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
 * Select a negative point on the border
 * Work well with sparse data
 * @author khanh
 *
 */
public class ByDistanceNegativePointSelection implements NegativePointSelection{

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
