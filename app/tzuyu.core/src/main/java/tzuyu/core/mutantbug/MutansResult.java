/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.mutantbug;

import java.util.List;
import java.util.Map;

import sav.strategies.dto.BreakPoint;

/**
 * @author LLT
 *
 */
public class MutansResult {
	private Map<BreakPoint, LineMutanResult> mutanResults;
	
	public void add(BreakPoint bkp, List<Boolean> testResult) {
		LineMutanResult lineMutanResult = mutanResults.get(bkp);
		if (lineMutanResult == null) {
			lineMutanResult = new LineMutanResult();
			mutanResults.put(bkp, lineMutanResult);
		}
		lineMutanResult.add(testResult);
	}
	
}
