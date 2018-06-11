/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.utils;

import cfg.CFG;
import cfg.CfgNode;
import cfg.utils.CfgConstructorUtils;

/**
 * @author LLT
 *
 */
public class CfgUtils {
	private CfgUtils() {}
	
	public static CfgNode getVeryFirstDecisionNode(CFG cfg) {
		return CfgConstructorUtils.getVeryFirstDecisionNode(cfg.getDecisionNodes());
	}

}
