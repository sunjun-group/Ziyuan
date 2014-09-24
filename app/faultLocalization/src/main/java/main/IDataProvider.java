/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package main;

import icsetlv.iface.ISlicer;

/**
 * @author LLT
 *
 */
public interface IDataProvider {

	ISlicer getSlicer();

	ICodeCoverage getCodeCoverageTool();

}
