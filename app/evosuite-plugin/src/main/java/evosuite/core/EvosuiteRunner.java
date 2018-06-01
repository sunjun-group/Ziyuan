/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package evosuite.core;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.evosuite.result.BranchInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import evosuite.core.process.ByteConverter;
import evosuite.core.process.EvosuiteInvoker;
import evosuite.core.process.EvosuiteTestResult;
import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;
import sav.strategies.vm.VMConfiguration;
import sav.strategies.vm.VMRunner;

/**
 * @author LLT
 *
 */
public class EvosuiteRunner {
	private static Logger log = LoggerFactory.getLogger(EvosuiteRunner.class);
	private static ExecutorService executorService = Executors.newCachedThreadPool(); 
	
	@SuppressWarnings("unchecked")
	public static EvosuiteResult run(VMConfiguration vmConfig, EvosuitParams params)
			throws SavException, IOException, ClassNotFoundException {
		
		
		final VMRunner vmRunner = new VMRunner() {
			@Override
			public void setupInputStream(final InputStream is, final StringBuffer sb, boolean error) {
				executorService.execute(new Runnable() {
					public void run() {
						final InputStreamReader streamReader = new InputStreamReader(is);
						BufferedReader br = new BufferedReader(streamReader);
						try {
							String line = null;
							try {
								boolean stop = false;
								while (!stop && (line = br.readLine()) != null) {
									log.debug(line);
									if (EvosuiteInvoker.END_TOKEN.equals(line)) {
										stop = true;
										process.destroyForcibly();
										break;
									}
								}
							} catch (IOException e) {
								
							}
						} finally {
							IOUtils.closeQuietly(streamReader);
							IOUtils.closeQuietly(br);
							IOUtils.closeQuietly(is);
						}
						
					}
				});
			}
		};
		vmConfig.setLaunchClass(EvosuiteInvoker.class.getName());
		File dumpFile = File.createTempFile("evosuiteResult", ".txt");
		vmConfig.getProgramArgs().clear();
		vmConfig.addProgramArgs(dumpFile.getAbsolutePath());
		vmConfig.addProgramArgs(StringUtils.join(EvosuiteInvoker.CMD_SEPRATOR, (Object[]) params.getCommandLine()));
		System.out.println(vmRunner.getCommandLinesString(vmConfig));
		vmRunner.startAndWaitUntilStop(vmConfig);

		DataInputStream reader = null;
		InputStream stream = null;
		try {

			stream = new FileInputStream(dumpFile);
			reader = new DataInputStream(new BufferedInputStream(stream));
			int length = reader.readInt();
			byte[] bytes = new byte[length];
			reader.readFully(bytes);

			List<List<EvosuiteTestResult>> result = (List<List<EvosuiteTestResult>>) ByteConverter
					.convertFromBytes(bytes);
			EvosuiteResult evoResult = new EvosuiteResult();
			for (List<EvosuiteTestResult> list : result) {
				for (EvosuiteTestResult testGenerationResult : list) {
					Set<BranchInfo> uncoveredBranches = testGenerationResult.getUncoveredBranches();
					Set<BranchInfo> coveredBranches = testGenerationResult.getCoveredBranches();
					System.out.println(testGenerationResult);
					evoResult.targetClass = testGenerationResult.getTargetClass();
					evoResult.targetMethod = getTargetMethod(params, uncoveredBranches, coveredBranches);
					evoResult.uncoveredBranches = uncoveredBranches;
					evoResult.coveredBranches = coveredBranches;
					evoResult.branchCoverage = getBranchCoverage(coveredBranches, uncoveredBranches);
					System.out.println("branch coverage: " + evoResult.branchCoverage);
				}
			}
			if (evoResult.targetMethod == null) {
				evoResult.targetMethod = params.getMethod();
			}
			dumpFile.delete();
			return evoResult;
		} finally {
			close(reader);
		}
	}
	
	private static void close(Closeable closeble) {
		if (closeble != null) {
			try {
				closeble.close();
			} catch (IOException e) {
				// do nothing
			}
		}
	}
	
	private static String getTargetMethod(EvosuitParams params, Set<BranchInfo> uncoveredBranches, Set<BranchInfo> coveredBranches) {
		List<BranchInfo> allInfos = new ArrayList<BranchInfo>();
		if (CollectionUtils.isNotEmpty(coveredBranches)) {
			allInfos.addAll(coveredBranches);
		}
		if (CollectionUtils.isNotEmpty(uncoveredBranches)) {
			allInfos.addAll(uncoveredBranches);
		}
		int methodStartLIne = params.getMethodPosition()[0];
		int methodEndLine = params.getMethodPosition()[1];
		for (BranchInfo info : allInfos) {
			if (params.getTargetClass().equals(info.getClassName())) {
				if (info.getLineNo() >= methodStartLIne && info.getLineNo() <= methodEndLine) {
					return StringUtils.dotJoin(info.getClassName(), info.getMethodName()); 
				}
			}
		}
		
		return null;
	}

	private static double getBranchCoverage(Set<BranchInfo> coveredBranches, Set<BranchInfo> uncoveredBranches) {
		double covered = CollectionUtils.getSize(coveredBranches);
		double total = covered + CollectionUtils.getSize(uncoveredBranches);
		return covered / (double) total;
	}
	
	public static class EvosuiteResult {
		public String targetClass;
		public String targetMethod; // full name
		public Set<BranchInfo> uncoveredBranches;
		public Set<BranchInfo> coveredBranches;
		public double branchCoverage;
		public List<String> coverageInfo;
		
		
	}
}
