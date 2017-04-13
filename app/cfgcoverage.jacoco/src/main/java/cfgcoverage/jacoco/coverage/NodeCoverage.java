/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco.coverage;

import java.util.HashSet;
import java.util.Set;

/**
 * @author LLT
 *
 */
public class NodeCoverage {
	private Set<String> coveredTcs;
	
	public NodeCoverage() {
		coveredTcs = new HashSet<String>();
	}

	public boolean isCovered(String testMethod) {
		return coveredTcs.contains(testMethod);
	}

	public void setCovered(String testMethod) {
		coveredTcs.add(testMethod);
	}
}
