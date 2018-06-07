/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cfg.BranchRelationship;
import cfg.CFG;
import cfg.CfgNode;
import cfg.utils.CfgConstructorUtils;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 *
 */
public class CfgUtils {
	private CfgUtils() {}
	
	public static CfgNode getVeryFirstDecisionNode(CFG cfg) {
		return CfgConstructorUtils.getVeryFirstDecisionNode(cfg.getDecisionNodes());
	}

	@SuppressWarnings("unchecked")
	public static Collection<CfgNode> getPrecondInherentDominatee(CfgNode node) {
		if (node.getDominators() == null) {
			return Collections.EMPTY_LIST;
		}
		List<CfgNode> result = new ArrayList<CfgNode>(node.getDominators().size());
		for (CfgNode dominatee : CollectionUtils.nullToEmpty(node.getDominators())) {
			BranchRelationship branchRelationship = node.getBranchRelationship(dominatee.getIdx());
			if (branchRelationship != BranchRelationship.TRUE_FALSE) {
				result.add(dominatee);
			}
		}
		return result;
	}
	
	public static boolean implyTrueBranch(BranchRelationship type) {
		return type == BranchRelationship.TRUE || type == BranchRelationship.TRUE_FALSE;
	}
	
	public static boolean implyFalseBranch(BranchRelationship type) {
		return type == BranchRelationship.FALSE || type == BranchRelationship.TRUE_FALSE;
	}
}
