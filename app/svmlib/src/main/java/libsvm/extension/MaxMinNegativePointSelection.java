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

	/**
	 * Return Pareto points
	 * No other points has value greater/smaller
	 */
	public DataPoint select(List<DataPoint> negatives){
		List<DataPoint> minMaxPoints = findMaxMin(negatives);
		
		int randomIndex = (int) (minMaxPoints.size() * Math.random());
		return minMaxPoints.get(randomIndex);
	}
	
	private List<DataPoint> findMaxMin(List<DataPoint> negatives){
		int numberOfFeatures = negatives.get(0).getNumberOfFeatures();
		
		List<DataPoint> result =  new ArrayList<Machine.DataPoint>(numberOfFeatures * 2);
		
		for(DataPoint point: negatives){
			
			if(isAdded(point, negatives, numberOfFeatures)){
				result.add(point);
			}
		}
		
		return result;
	}

	private boolean isAdded(DataPoint point, List<DataPoint> negatives,
			int numberOfFeatures){
		return isMax(point, negatives, numberOfFeatures) || isMin(point, negatives, numberOfFeatures);
	}
	
	/**
	 * no other point larger in all features
	 */
	private boolean isMax(DataPoint point, List<DataPoint> negatives,
			int numberOfFeatures) {
		boolean isMax = true;
		for(DataPoint other: negatives){
			if(point != other){
				boolean isOtherGreaterAll = true;
				for(int i = 0; i < numberOfFeatures; i++){
					if(other.getValue(i) < point.getValue(i)){
						isOtherGreaterAll = false;
						break;
					}
				}
				
				if(isOtherGreaterAll){
					isMax = false;
					break;
				}
			}
		}
		return isMax;
	}
	
	/**
	 * no other point smaller in all features
	 */
	private boolean isMin(DataPoint point, List<DataPoint> negatives,
			int numberOfFeatures) {
		boolean isMin = true;
		for(DataPoint other: negatives){
			if(point != other){
				boolean isOtherSmallerAll = true;
				for(int i = 0; i < numberOfFeatures; i++){
					if(other.getValue(i) > point.getValue(i)){
						isOtherSmallerAll = false;
						break;
					}
				}
				
				if(isOtherSmallerAll){
					isMin = false;
					break;
				}
			}
		}
		return isMin;
	}
}
