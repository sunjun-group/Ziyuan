/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.gan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.utils.BreakpointDataUtils;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class InputDatapointMapping {
	private List<ExecVar> orgVars;
	private Map<BreakpointValue, double[]> dpMap = new HashMap<>();

	public InputDatapointMapping(List<ExecVar> originalVars) {
		this.orgVars = originalVars;
	}

	public void clear() {
		dpMap.clear();
	}

	public List<double[]> getDatapoints(List<BreakpointValue> values, List<ExecVar> execVars) {
		if (!CollectionUtils.isEqualList(orgVars, execVars)) {
			return BreakpointDataUtils.toDataPoint(execVars, values);
		}
		List<double[]> result = new ArrayList<double[]>(CollectionUtils.getSize(values));
		for (BreakpointValue value : values) {
			double[] dp = dpMap.get(value);
			if (dp == null) {
				dp = BreakpointDataUtils.toDatapoint(execVars, value);
				dpMap.put(value, dp);
			}
			result.add(dp);
		}
		return result;
	}
	
}
