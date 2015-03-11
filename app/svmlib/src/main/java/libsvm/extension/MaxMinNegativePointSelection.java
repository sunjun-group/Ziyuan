/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package libsvm.extension;

import java.util.ArrayList;
import java.util.List;

import libsvm.core.Machine;
import libsvm.core.Machine.DataPoint;

/**
 * @author khanh
 *
 */
public class MaxMinNegativePointSelection implements NegativePointSelection{

	public DataPoint select(List<DataPoint> negatives){
		List<DataPoint> minMaxPoints = findMaxMin(negatives);
		
		int randomIndex = (int) (minMaxPoints.size() * Math.random());
		return minMaxPoints.get(randomIndex);
	}
	
	private List<DataPoint> findMaxMin(List<DataPoint> negatives){
		int numberOfFeatures = negatives.get(0).getNumberOfFeatures();
		
		double [] minOfFeatures = new double[numberOfFeatures];
		DataPoint[] minPoints = new DataPoint[numberOfFeatures];
		double [] maxOfFeatures = new double[numberOfFeatures];
		DataPoint[] maxPoints = new DataPoint[numberOfFeatures];
		
		for(int i = 0; i < numberOfFeatures; i++){
			minOfFeatures[i] = Double.MAX_VALUE;
			maxOfFeatures[i] = Double.MIN_VALUE;
			
			for(DataPoint point: negatives){
				double value = point.getValue(i);
				if(value < minOfFeatures[i]){
					minOfFeatures[i] = value;
					minPoints[i] = point;
				}
				if(value > maxOfFeatures[i]){
					maxOfFeatures[i] = value;
					maxPoints[i] = point;
				}
			}
		}
		
		List<DataPoint> result =  new ArrayList<Machine.DataPoint>(numberOfFeatures * 2);
		for(int i = 0; i < numberOfFeatures; i++){
			result.add(minPoints[i]);
			result.add(maxPoints[i]);
		}
		
		return result;
	}
}
