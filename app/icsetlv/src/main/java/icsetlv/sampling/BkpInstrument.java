/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.sampling;

import icsetlv.common.dto.ExecVar;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import sav.common.core.formula.Eq;
import sav.strategies.dto.BreakPoint;

/**
 * @author LLT
 *
 */
public class BkpInstrument {
	
	public void instrument(Map<ExecVar, Object> values) {
		for (Entry<ExecVar, Object> val : values.entrySet()) {
			instrument(val.getKey(), val.getValue());
		}
	}

	private void instrument(ExecVar key, Object value) {
		
	}

	public BreakPoint instrument(List<Eq<?>> assignments) {
		// TODO Auto-generated method stub
		return null;
	}
}
