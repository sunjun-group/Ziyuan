/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.utils;

import cfgcoverage.jacoco.analysis.data.BranchRelationship;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class CfgUtils {
	private CfgUtils(){}
	
	public static CfgNode getTrueBranch(CfgNode node) {
		return getBranch(node, BranchRelationship.TRUE);
	}
	
	public static CfgNode getFalseBranch(CfgNode node) {
		return getBranch(node, BranchRelationship.FALSE);
	}
	
	private static CfgNode getBranch(CfgNode node, BranchRelationship relationship) {
		if (node.isLeaf()) {
			return null;
		}
		for (CfgNode branch : node.getBranches()) {
			if (CollectionUtils.existIn(node.getBranchRelationship(branch.getIdx()), relationship,
					BranchRelationship.TRUE_FALSE)) {
				return branch;
			}
		}
		return null;
	}
}
