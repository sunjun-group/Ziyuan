/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.common.dto;

import sav.common.core.utils.CollectionUtils;


/**
 * @author LLT
 *
 */
public class BreakpointValue extends ExecValue {
	
	public BreakpointValue(String id) {
		super(id);
	}
	
	public String getBkpId() {
		return getVarId();
	}
	
	@Override
	public String getChildId(String childCode) {
		return childCode;
	}
	
	@Override
	protected boolean needToRetrieveValue() {
		return false;
	}
	
	public Double getValue(final String variableId) {
		for (ExecValue child : CollectionUtils.nullToEmpty(children)) {
			if (child.getVarId().equals(variableId)) {
				return Double.valueOf(child.getDoubleVal());
			}
		}
		return null;
	}

}
