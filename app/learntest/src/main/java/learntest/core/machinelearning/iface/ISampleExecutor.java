/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.machinelearning.iface;

import java.util.List;

import sav.common.core.SavException;
import sav.common.core.formula.Eq;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public interface ISampleExecutor<T extends ISampleResult> {

	T runSamples(List<List<Eq<?>>> assignments, List<ExecVar> originVars) throws SavException;

}
