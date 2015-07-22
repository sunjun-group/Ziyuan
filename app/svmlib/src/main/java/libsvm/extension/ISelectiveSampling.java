/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package libsvm.extension;

import java.util.List;

import libsvm.core.Machine;
import libsvm.core.Machine.DataPoint;

/**
 * @author LLT
 *
 */
public interface ISelectiveSampling {

	List<DataPoint> selectData(Machine machine);

}
