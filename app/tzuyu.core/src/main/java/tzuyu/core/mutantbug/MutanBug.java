/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.mutantbug;

import java.io.File;
import java.util.List;
import java.util.Map;

import sav.strategies.dto.BreakPoint;
import sav.strategies.junit.JunitResult;
import sav.strategies.junit.JunitRunner;
import sav.strategies.junit.JunitRunnerParameters;
import sav.strategies.mutanbug.IMutator;
import tzuyu.core.inject.ApplicationData;

import com.google.inject.Inject;

/**
 * @author LLT
 *
 */
public class MutanBug {
	@Inject
	private ApplicationData appData;
	@Inject
	private IMutator mutator;
	
	public void filter(List<BreakPoint> bkps, List<String> junitClassNames) throws Exception {
		MutansResult mutanResult = mutateAndRunTests(bkps, junitClassNames);
	}
	
	public MutansResult mutateAndRunTests(List<BreakPoint> bkps, List<String> junitClassNames) throws Exception {
		MutansResult result = new MutansResult();
		Map<BreakPoint, List<File>> mutatedResult = mutator.mutate(bkps, appData.getScrFolder());
		Recompiler compiler = new Recompiler(appData.getAppClasspathStr(), appData.getScrFolder());
		JunitRunnerParameters params = new JunitRunnerParameters();
		params.setJunitClasses(junitClassNames);
		// recompile and rerun test cases
		for (BreakPoint bkp : bkps) {
			bkp.getClassCanonicalName();
			List<File> mutatedFiles = mutatedResult.get(bkp);
			for (File mutatedFile : mutatedFiles) {
				if (compiler.recompile(mutatedFile)) {
					JunitResult jresult = JunitRunner.runTestcases(params);
					result.add(bkp, jresult.getTestResult());
				}
			}
		}
		
		return result;
	}
	
	public void setAppData(ApplicationData appData) {
		this.appData = appData;
	}
	
	public void setMutator(IMutator mutator) {
		this.mutator = mutator;
	}
}
