/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.iface;

import icsetlv.common.exception.IcsetlvException;
import sav.strategies.slicing.ISlicer;

/**
 * @author LLT
 *
 */
public interface IManager {

	ISlicer getSlicer() throws IcsetlvException;

	IBugExpert getBugExpert();

	ITestcasesExecutor getTestcasesExecutor();

}
