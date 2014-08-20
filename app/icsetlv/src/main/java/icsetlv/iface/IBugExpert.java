/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.iface;

import icsetlv.common.dto.BreakpointValue;

import java.util.List;

/**
 * @author LLT
 *
 */
public interface IBugExpert {

	public boolean isRootCause(List<BreakpointValue> passValues,
			List<BreakpointValue> failValues);
}
