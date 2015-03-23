/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.mutantbug;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author LLT
 *
 */
public class LineMutanResult {
	private List<Set<Integer>> passTestsInMutants;
	private List<Set<Integer>> failTestsInMutants;

	public void add(List<Boolean> testResult) {
		Set<Integer> passTests = new HashSet<Integer>();
		Set<Integer> failTests = new HashSet<Integer>(); 
		
		for (int i = 0; i < testResult.size(); i++) {
			boolean pass = testResult.get(i);
			if (pass) {
				passTests.add(i);
			} else {
				failTests.add(i);
			}
		}
		
		passTestsInMutants.add(passTests);
		failTestsInMutants.add(failTests);
	}
	
}
