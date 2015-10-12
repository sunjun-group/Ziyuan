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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sav.common.core.SavException;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.ClassLocation;
import sav.strategies.junit.JunitResult;
import sav.strategies.junit.JunitRunner;
import sav.strategies.junit.JunitRunnerParameters;
import sav.strategies.junit.SavJunitRunner;
import sav.strategies.mutanbug.DebugLineInsertionResult;
import sav.strategies.mutanbug.IMutator;
import sav.strategies.mutanbug.MutationResult;
import sav.strategies.vm.VMConfiguration;
import faultLocalization.FaultLocalizationReport;
import faultLocalization.LineCoverageInfo;
import faultLocalization.MutationBasedSuspiciousnessCalculator;

/**
 * @author LLT
 *
 */
public class MutanBug {
	private static final int JUNIT_TIMEOUT = 2; // 2s
	private static Logger log = LoggerFactory.getLogger(MutanBug.class);
	private AppJavaClassPath app;
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
		Map<String, MutationResult> mutatedResult = mutator.mutate(bkps);
		Recompiler compiler = new Recompiler(new VMConfiguration(app));
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
			List<File> classFiles = ClassUtils.getCompiledClassFiles(
					app.getTarget(), bkp.getClassCanonicalName());
			// backup
			fileBackup.backup(classFiles);
			VMConfiguration vmConfig = SavJunitRunner.createVmConfig(app);
			for (File mutatedFile : mutatedFiles) {
				try{
					compiler.recompileJFile(app.getTarget(), mutatedFile);
					JunitResult jresult = JunitRunner.runTestcases(vmConfig, params);
					result.add(bkp, jresult.getTestResult());
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
			// restore the org class
			fileBackup.restore(classFiles);
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
		Map<String, DebugLineInsertionResult> result = mutator.insertDebugLine(classLocationMap);
		Recompiler recompiler = new Recompiler(new VMConfiguration(app));
		for (DebugLineInsertionResult classResult : result.values()) {
			List<File> classFiles = ClassUtils.getCompiledClassFiles(app.getTarget(), classResult.getClassName());
			filesBackup.backup(classFiles);
			recompiler.recompileJFile(app.getTarget(),
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
	
	public void setAppData(AppJavaClassPath appData) {
		this.app = appData;
	}
	
	public void setMutator(IMutator mutator) {
		this.mutator = mutator;
	}
}
