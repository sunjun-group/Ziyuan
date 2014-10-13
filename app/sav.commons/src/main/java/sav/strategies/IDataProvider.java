/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies;

import sav.strategies.codecoverage.ICodeCoverage;
import sav.strategies.slicing.ISlicer;

/**
 * @author LLT
 *
 */
public interface IDataProvider {

	ISlicer getSlicer();

	ICodeCoverage getCodeCoverageTool();

}
