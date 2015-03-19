/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.iface;

import icsetlv.common.dto.TcExecResult;
import icsetlv.common.exception.IcsetlvException;

import java.util.List;

import sav.common.core.SavException;
import sav.strategies.dto.BreakPoint;

/**
 * @author LLT
 *
 */
public interface ITestcasesExecutor {

	TcExecResult execute(List<String> passTestcases,
			List<String> failTestcases, List<BreakPoint> brkps)
			throws IcsetlvException, SavException;
	
}
