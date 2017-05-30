/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.utils;

import java.util.ArrayList;
import java.util.List;

import icsetlv.common.dto.BreakpointValue;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.execute.value.ExecValue;

/**
 * @author LLT
 *
 */
public class ExecValueUtils {

	public static List<ExecValue> flattern(BreakpointValue bkpValue) {
		List<ExecValue> values = new ArrayList<ExecValue>();
		for (ExecValue val : CollectionUtils.nullToEmpty(bkpValue.getChildren())) {
			append(values, val);
		}
		return values;
	}

	private static void append(List<ExecValue> values, ExecValue curValue) {
		if (CollectionUtils.isEmpty(curValue.getChildren())) {
			values.add(curValue);
		} else {
			for (ExecValue child : curValue.getChildren()) {
				append(values, child);
			}
		}
	}
}
