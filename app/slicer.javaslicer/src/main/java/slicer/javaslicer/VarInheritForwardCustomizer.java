/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer;

import java.util.HashMap;

import sav.strategies.dto.BreakPoint;

/**
 * @author LLT
 *
 */
public class VarInheritForwardCustomizer implements IBreakpointCustomizer {

	@Override
	public void customize(HashMap<String, BreakPoint> bkpMap) {
		for (String clazz : bkpMap.keySet()) {
			
		}
	}

}
