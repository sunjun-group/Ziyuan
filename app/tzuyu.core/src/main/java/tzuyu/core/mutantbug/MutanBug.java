/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.mutantbug;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mutanbug.commons.utils.FileUtils;
import sav.common.core.Logger;
import sav.common.core.SavException;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.ClassLocation;
import sav.strategies.junit.JunitResult;
import sav.strategies.junit.JunitRunner;
import sav.strategies.junit.JunitRunnerParameters;
import sav.strategies.mutanbug.IMutator;
import sav.strategies.vm.VMConfiguration;
import sav.strategies.vm.VMRunner;
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
			System.out.println(passTestsInMutants);
			System.out.println(failTestsInMutants);
		}
	}
	
	public <T extends ClassLocation> MutansResult mutateAndRunTests(
			List<T> bkps, List<String> junitClassNames) throws Exception {
		MutansResult result = new MutansResult();
		Map<T, List<File>> mutatedResult = mutator.mutate(bkps, appData.getAppSrc());
		Recompiler compiler = new Recompiler(appData);
		JunitRunnerParameters params = new JunitRunnerParameters();
		params.setJunitClasses(junitClassNames);
		// recompile and rerun test cases
		for (T bkp : bkps) {
			List<File> mutatedFiles = mutatedResult.get(bkp);
			if (CollectionUtils.isEmpty(mutatedFiles)) {
				continue;
			}
			File classFile = new File(ClassUtils.getClassFilePath(
					appData.getAppTarget(), bkp.getClassCanonicalName()));
			File tempDir = FileUtils.createTempFolder("backup");
			File backupClassFile = FileUtils.copyFileToDirectory(classFile,
					tempDir, true);
			for (File mutatedFile : mutatedFiles) {
				try{
					compiler.recompileJFile(mutatedFile);
					params.setDestfile(File.createTempFile("testMutant", ".temp").getAbsolutePath());
					JunitResult jresult = runTestcases(params);
					result.add(bkp, jresult.getTestResult());
				} catch (Exception e) {
					log.error(e);
				}
			}
			// restore the org class
			FileUtils.copyFile(backupClassFile, classFile, true);
			backupClassFile.delete();
			tempDir.delete();
		}
		
		return result;
	}
	
	private JunitResult runTestcases(JunitRunnerParameters params)
			throws ClassNotFoundException, IOException, SavException {
		VMRunner runner = new VMRunner();
		VMConfiguration config = appData.getVmConfig();
		config.setLaunchClass(JunitRunner.class.getName());
		config.setDebug(false);
		
		List<String> args = new ArrayList<String>();
		VMRunner.appendProgramArgs(args, JunitRunnerParameters.CLASS_METHODS, params.getClassMethods());
		VMRunner.appendProgramArgs(args, JunitRunnerParameters.DEST_FILE, params.getDestfile());
		
		config.setProgramArgs(args);
		
		runner.startAndWaitUntilStop(config);
		
		return JunitResult.readFrom(params.getDestfile());
	}

	public void setAppData(ApplicationData appData) {
		this.appData = appData;
	}
	
	public void setMutator(IMutator mutator) {
		this.mutator = mutator;
	}
}
