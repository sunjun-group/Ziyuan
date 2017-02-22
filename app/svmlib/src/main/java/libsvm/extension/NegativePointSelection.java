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
public interface NegativePointSelection {

	/**
	 * Select a negative point from the given negative points list. The
	 * selection can be made to take into consideration the information of the
	 * given positive points list, but this is not mandatory.
	 * 
	 * @param selectionData
	 *            List of negative points to select from.
	 * @param referenceData
	 *            List of positive points for reference when needed.
	 * @return One negative point selected from <code>negatives</code>, or
	 *         <code>null</code> if the given list is empty.
	 */
	DataPoint select(List<DataPoint> selectionData, List<DataPoint> referenceData);

	/**
	 * Tell whether the current selection algorithm can give different results
	 * if called at different time. This value is basically determined by the
	 * nature of the underlying algorithm.
	 * 
	 * @return either <code>true</code> or <code>false</code>
	 */
	boolean isConsistent();
}
