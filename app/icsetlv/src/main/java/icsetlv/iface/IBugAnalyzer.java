/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.iface;

import icsetlv.common.exception.IcsetlvException;

import java.util.List;

import sav.common.core.SavException;
import sav.strategies.dto.BreakPoint;

/**
 * @author LLT
 *
 */
public interface IBugAnalyzer {
	public List<BreakPoint> analyze(List<BreakPoint> breakpoints) throws IcsetlvException, SavException;
}
