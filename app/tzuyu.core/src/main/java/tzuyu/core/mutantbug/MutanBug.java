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
import java.util.concurrent.TimeUnit;

import sav.common.core.Logger;
import sav.common.core.SavException;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.ClassLocation;
import sav.strategies.junit.JunitResult;
import sav.strategies.junit.JunitRunner;
import sav.strategies.junit.JunitRunnerParameters;
import sav.strategies.mutanbug.DebugLineInsertionResult;
import sav.strategies.mutanbug.IMutator;
import sav.strategies.mutanbug.MutationResult;
import sav.strategies.vm.VMConfiguration;
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
	private static final int JUNIT_TIMEOUT = 2; // 2s
	private Logger<?> log = Logger.getDefaultLogger();
	@Inject
	private ApplicationData appData;
	@Inject
	private IMutator mutator;
	private FilesBackup filesBackup;
	
	public void mutateAndRunTests(FaultLocalizationReport report,
			int rankToMutate, List<String> junitClassNames) throws Exception {
		List<ClassLocation> breakpoints = new ArrayList<ClassLocation>();
		List<LineCoverageInfo> firstRanks = report.getFirstRanks(rankToMutate);
		
		//reset suspicisouness before mutation
		report.setSuspiciousnessForAll(Double.MIN_VALUE);
		
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
			List<Set<Integer>> passTestsInMutants;
			List<Set<Integer>> failTestsInMutants;
			/* if the line cannot be mutated, we get the original result for it*/
			if (resultForSingleLine == null) {
				passTestsInMutants = CollectionUtils.listOf(passTests);
				failTestsInMutants = CollectionUtils.listOf(failTests);
			} else {
				passTestsInMutants = resultForSingleLine.getPassTestsInMutants();
				failTestsInMutants = resultForSingleLine.getFailTestsInMutants();
			}
			
			MutationBasedSuspiciousnessCalculator calculator = new MutationBasedSuspiciousnessCalculator(passTests, failTests, 
								passCoverTests, failCoverTests, passTestsInMutants, failTestsInMutants);
			double suspiciousness = calculator.compute();
			
			//update suspiciousness
			firstRanks.get(i).setSuspiciousness(suspiciousness);
		}
		
		//sort after updating suspiciousness
		report.sort();
	}
	
	public <T extends ClassLocation> MutansResult mutateAndRunTests(
			List<T> bkps, List<String> junitClassNames) throws Exception {
		MutansResult result = new MutansResult();
		Map<String, MutationResult> mutatedResult = mutator.mutate(bkps,
				appData.getAppSrc());
		VMConfiguration vmConfig = appData.getVmConfig();
		Recompiler compiler = new Recompiler(vmConfig);
		JunitRunnerParameters params = new JunitRunnerParameters();
		params.setJunitClasses(junitClassNames);
		params.setTimeout(JUNIT_TIMEOUT, TimeUnit.SECONDS);
		// recompile and rerun test cases
		
		FilesBackup fileBackup = FilesBackup.startBackup();
		for (T bkp : bkps) {
			List<File> mutatedFiles = mutatedResult.get(
					bkp.getClassCanonicalName()).getMutatedFiles(
					bkp.getLineNo());
			if (CollectionUtils.isEmpty(mutatedFiles)) {
				continue;
			}
			File classFile = new File(ClassUtils.getClassFilePath(
					appData.getAppTarget(), bkp.getClassCanonicalName()));
			// backup
			fileBackup.backup(classFile);
			for (File mutatedFile : mutatedFiles) {
				try{
					compiler.recompileJFile(appData.getAppTarget(), mutatedFile);
					JunitResult jresult = JunitRunner.runTestcases(vmConfig, params);
					result.add(bkp, jresult.getTestResult());
				} catch (Exception e) {
					log.error(e);
				}
			}
			// restore the org class
			fileBackup.restore(classFile);
		}
		fileBackup.close();
		
		return result;
	}

	private FilesBackup startBackup() {
		if (filesBackup == null) {
			filesBackup = FilesBackup.startBackup(); 
		} else if (filesBackup.isClose()) {
			filesBackup.open();
		}
		return filesBackup;
	}
	
	public <T extends ClassLocation> Map<String, DebugLineInsertionResult> mutateForMachineLearning(
			Map<String, List<T>> classLocationMap)
			throws SavException {
		startBackup();
		Map<String, DebugLineInsertionResult> result = mutator.insertDebugLine(classLocationMap, appData.getAppSrc());
		Recompiler recompiler = new Recompiler(appData.getVmConfig());
		for (DebugLineInsertionResult classResult : result.values()) {
			filesBackup.backup(ClassUtils.getClassFilePath(appData.getAppTarget(),
					classResult.getClassName()));
			recompiler.recompileJFile(appData.getAppTarget(),
					classResult.getMutatedFile());
		}
		
		return result;
	}

	public void restoreFiles() {
		if (filesBackup != null && !filesBackup.isClose()) {
			filesBackup.restoreAll();
			filesBackup.close();
		}
	}
	
	public void setAppData(ApplicationData appData) {
		this.appData = appData;
	}
	
	public void setMutator(IMutator mutator) {
		this.mutator = mutator;
	}
}
