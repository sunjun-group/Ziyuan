/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.iface;

import icsetlv.common.dto.VariablesExtractorResult.BreakpointResult;

/**
 * @author LLT
 *
 */
public interface IBugExpert {
	public boolean isRootCause(BreakpointResult bkp);
}
