/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.testcase.data;

import java.util.List;

import icsetlv.common.dto.BreakpointValue;

/**
 * @author LLT
 *
 */
public interface IBreakpointData {

	List<BreakpointValue> getFalseValues();

	List<BreakpointValue> getTrueValues();

	List<BreakpointValue> getMoreTimesValues();

	List<BreakpointValue> getOneTimeValues();

}
