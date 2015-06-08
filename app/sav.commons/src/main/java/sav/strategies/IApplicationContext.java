/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package sav.strategies;

import sav.strategies.codecoverage.ICodeCoverage;
import sav.strategies.mutanbug.IMutator;
import sav.strategies.slicing.ISlicer;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 *
 */
public interface IApplicationContext {

	ISlicer getSlicer();

	ICodeCoverage getCodeCoverageTool();

	IMutator getMutator();

	VMConfiguration getVmConfig();
}
