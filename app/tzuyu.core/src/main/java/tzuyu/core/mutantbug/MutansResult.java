/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.mutantbug;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sav.strategies.dto.ClassLocation;

/**
 * @author LLT
 *
 */
public class MutansResult {
	private Map<ClassLocation, LineMutanResult> mutanResults;
	
	public MutansResult() {
		mutanResults = new HashMap<ClassLocation, LineMutanResult>();
	}
	
	public void add(ClassLocation bkp, List<Boolean> testResult) {
		LineMutanResult lineMutanResult = mutanResults.get(bkp);
		if (lineMutanResult == null) {
			lineMutanResult = new LineMutanResult();
			mutanResults.put(bkp, lineMutanResult);
		}
		lineMutanResult.add(testResult);
	}
	
	public LineMutanResult getMutantResult(ClassLocation location){
		return mutanResults.get(location);
	}
	
}
