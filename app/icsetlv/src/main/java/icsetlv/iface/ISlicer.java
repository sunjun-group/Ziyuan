/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv.iface;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.slicer.SlicerInput;

import java.util.List;

/**
 * @author LLT
 *
 */
public interface ISlicer {

	List<BreakPoint> slice(SlicerInput input) throws IcsetlvException;
	
}
