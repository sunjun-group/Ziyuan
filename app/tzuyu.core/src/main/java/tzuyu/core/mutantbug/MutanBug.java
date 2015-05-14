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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mutation.mutator.IMutator;
import mutation.mutator.MutationResult;
import mutation.mutator.insertdebugline.DebugLineInsertionResult;
import mutation.utils.FileUtils;
import sav.common.core.Logger;
import sav.common.core.SavException;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.ClassLocation;
import sav.strategies.junit.JunitResult;
import sav.strategies.junit.JunitRunner;
import sav.strategies.junit.JunitRunner.JunitRunnerProgramArgBuilder;
import sav.strategies.junit.JunitRunnerParameters;
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
		Map<String, MutationResult> mutatedResult = mutator.mutate(bkps,
				appData.getAppSrc());
		Recompiler compiler = new Recompiler(appData.getVmConfig());
		JunitRunnerParameters params = new JunitRunnerParameters();
		params.setJunitClasses(junitClassNames);
		// recompile and rerun test cases
		for (T bkp : bkps) {
			List<File> mutatedFiles = mutatedResult.get(
					bkp.getClassCanonicalName()).getMutatedFiles(
					bkp.getLineNo());
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
					compiler.recompileJFile(appData.getAppTarget(), mutatedFile);
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
		List<String> args = new JunitRunnerProgramArgBuilder()
				.methods(params.getClassMethods())
				.destinationFile(params.getDestfile()).build();
		config.setProgramArgs(args);
		runner.startAndWaitUntilStop(config);
		return JunitResult.readFrom(params.getDestfile());
	}

	public Map<String, DebugLineInsertionResult> mutateForMachineLearning(
			List<ClassLocation> locations) throws SavException {
		Map<String, List<ClassLocation>> classLocationMap = createClassLocationMap(locations);
		
		Map<String, DebugLineInsertionResult> result = mutator.insertDebugLine(classLocationMap, appData.getAppSrc());
		Recompiler recompiler = new Recompiler(appData.getVmConfig());
		for (DebugLineInsertionResult classResult : result.values()) {
			recompiler.recompileJFile(appData.getAppTarget(),
					classResult.getMutatedFile());
		}
		
		return result;
	}

	/**
	 * @param locations
	 * @return
	 */
	private Map<String, List<ClassLocation>> createClassLocationMap(
			List<ClassLocation> locations) {
		Map<String, List<ClassLocation>> classLocationMap = new HashMap<String, List<ClassLocation>>();
		for(ClassLocation location: locations){
			String className = location.getClassCanonicalName();
			List<ClassLocation> locationsInCurrentClass = classLocationMap.get(className);
			if(locationsInCurrentClass == null){
				locationsInCurrentClass = new ArrayList<ClassLocation>();
				classLocationMap.put(className, locationsInCurrentClass);
			}
			locationsInCurrentClass.add(location);
		}
		return classLocationMap;
	}
	
	public void setAppData(ApplicationData appData) {
		this.appData = appData;
	}
	
	public void setMutator(IMutator mutator) {
		this.mutator = mutator;
	}
}
