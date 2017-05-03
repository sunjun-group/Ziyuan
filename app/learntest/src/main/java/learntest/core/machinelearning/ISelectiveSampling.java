/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.machinelearning;

import java.util.List;

import cfgcoverage.jacoco.analysis.data.CfgNode;
import learntest.calculator.OrCategoryCalculator;

/**
 * @author LLT
 *
 */
public interface ISelectiveSampling {

	/**
	 * @param node
	 * @param labels
	 * @param preconditions
	 * @param object
	 * @param notCoveredBranch
	 * @param b
	 */
	void selectDataForEmpty(CfgNode node, List<String> labels, OrCategoryCalculator preconditions, Object object,
			boolean trueBranch, boolean b);

}
