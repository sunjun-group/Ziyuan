/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.iface;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.dto.VariablesExtractorResult;
import icsetlv.common.exception.IcsetlvException;

import java.util.List;

/**
 * @author LLT
 *
 */
public interface IVariableExtractor {

	VariablesExtractorResult execute(List<String> passTestcases,
			List<String> failTestcases, List<BreakPoint> brkps)
			throws IcsetlvException;

}
