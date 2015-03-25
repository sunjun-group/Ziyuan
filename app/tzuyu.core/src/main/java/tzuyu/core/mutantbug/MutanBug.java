/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.mutantbug;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sav.common.core.Logger;
import sav.strategies.dto.ClassLocation;
import sav.strategies.junit.JunitResult;
import sav.strategies.junit.JunitRunner;
import sav.strategies.junit.JunitRunnerParameters;
import sav.strategies.mutanbug.IMutator;
import tzuyu.core.inject.ApplicationData;

import com.google.inject.Inject;

import faultLocalization.FaultLocalizationReport;
import faultLocalization.LineCoverageInfo;
import faultLocalization.MutationBasedSuspiciousnessCalculator;

/**
 * @author LLT
 *
 */
public class MutanBug {
	private Logger<?> log = Logger.getDefaultLogger();
	@Inject
	private ApplicationData appData;
	@Inject
	private IMutator mutator;

	public void mutateAndRunTests(FaultLocalizationReport report, List<String> junitClassNames) throws Exception {
		List<ClassLocation> breakpoints = new ArrayList<ClassLocation>();
		List<LineCoverageInfo> firstRanks = report.getFirstRanks(1);
		 
		for(LineCoverageInfo lineCoverageInfo: firstRanks){
			breakpoints.add(lineCoverageInfo.getLocation());
		}
		
		MutansResult mutansResult = mutateAndRunTests(breakpoints, junitClassNames);
		Set<Integer> passTests = report.getCoverageReport().getPassTestIndexes();
		Set<Integer> failTests = report.getCoverageReport().getFailTestIndexes();
		
		for(int i = 0; i < breakpoints.size(); i++){
			Set<Integer> passCoverTests = new HashSet<Integer>(firstRanks.get(i).getPassedTestcaseIndexesCover());
			Set<Integer> failCoverTests = new HashSet<Integer>(firstRanks.get(i).getFailedTestcaseIndexesCover());
			
			ClassLocation location = breakpoints.get(i);
			LineMutanResult resultForSingleLine = mutansResult.getMutantResult(location);
			List<Set<Integer>> passTestsInMutants = resultForSingleLine.getPassTestsInMutants();
			List<Set<Integer>> failTestsInMutants = resultForSingleLine.getFailTestsInMutants();
			
			MutationBasedSuspiciousnessCalculator calculator = new MutationBasedSuspiciousnessCalculator(passTests, failTests, 
								passCoverTests, failCoverTests, passTestsInMutants, failTestsInMutants);
			
			System.out.println(location.getId() + ":" + calculator.compute());
		}
	}
	
	public <T extends ClassLocation> MutansResult mutateAndRunTests(List<T> bkps, List<String> junitClassNames) throws Exception {
		MutansResult result = new MutansResult();
		Map<T, List<File>> mutatedResult = mutator.mutate(bkps, appData.getAppSrc());
		Recompiler compiler = new Recompiler(appData);
		JunitRunnerParameters params = new JunitRunnerParameters();
		params.setJunitClasses(junitClassNames);
		// recompile and rerun test cases
		for (T bkp : bkps) {
			bkp.getClassCanonicalName();
			List<File> mutatedFiles = mutatedResult.get(bkp);
			for (File mutatedFile : mutatedFiles) {
				try{
					compiler.recompileJFile(mutatedFile);
					JunitResult jresult = JunitRunner.runTestcases(params);
					result.add(bkp, jresult.getTestResult());
				} catch (Exception e) {
					log.error(e);
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
