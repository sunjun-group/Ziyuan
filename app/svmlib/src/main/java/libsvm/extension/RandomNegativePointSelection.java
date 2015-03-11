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
public class RandomNegativePointSelection implements NegativePointSelection{

	public DataPoint select(List<DataPoint> negatives){
		int randomIndex = (int) (negatives.size() * Math.random());
		return negatives.get(randomIndex);
	}
}
