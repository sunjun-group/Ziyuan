/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package faultLocaliation;

import faultLocaliation.sample.SampleProgramTest;
import faultLocaliation.sample.SamplePrograms;
import faultLocalization.dto.CoverageReport;
import faultLocalization.dto.LineCoverageInfo;
import icsetlv.AbstractTest;
import icsetlv.BugExpert;
import icsetlv.IcsetlvInput;
import icsetlv.common.dto.BreakPoint;
import icsetlv.common.dto.TcExecResult;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.svm.LibSVM;
import icsetlv.variable.TestcasesExecutor;
import icsetlv.vm.VMConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javacocoWrapper.JavaCoCo;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.featureselection.ranking.RecursiveFeatureEliminationSVM;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import commons.TestConfiguration;
import sav.common.core.utils.CollectionUtils;

/**
 * @author LLT
 * 
 */
public class ProgramTest extends AbstractTest {
	
	public ProgramTest() {
		module = TestConfiguration.FALTLOCALISATION;
	}

	@Test
	@Category(sg.edu.sutd.test.core.TzuyuTestCase.class)
	public void testLineCounter() throws Exception {
		String testingClassName1 = SamplePrograms.class.getName();

		ArrayList<String> testingClassNames = new ArrayList<String>();
		testingClassNames.add(testingClassName1);

		JavaCoCo javacoco = new JavaCoCo();
		CoverageReport result = javacoco.run(testingClassNames,
				Arrays.asList(SampleProgramTest.class.getName()));

		System.out.println(result.getFailureTraces());
		result.tarantula();
	}
	
	@Test
	public void testIntegrateSlicing() throws Exception {
			
	}
	
	@Test
	public void testIntegrateSvm() throws Exception {
		String testingClassName1 = SamplePrograms.class.getName();
		ArrayList<String> testingClassNames = new ArrayList<String>();
		testingClassNames.add(testingClassName1);
		
		JavaCoCo javacoco = new JavaCoCo();
		CoverageReport result = javacoco.run(testingClassNames,
				Arrays.asList(SampleProgramTest.class.getName()));
		List<LineCoverageInfo> lineCoverageInfo = result.tarantula();
		if (lineCoverageInfo.isEmpty()) {
			return;
		}
		double maxSpi = lineCoverageInfo.get(0).getSuspiciousness();
		List<LineCoverageInfo> maxSpiList = new ArrayList<LineCoverageInfo>();
		for (LineCoverageInfo info : lineCoverageInfo) {
			if (info.getSuspiciousness() == maxSpi) {
				maxSpiList.add(info);
			} else {
				break;
			}
		}
		List<BreakPoint> bkps = toBreakpoints(maxSpiList);
		IcsetlvInput input = initInput();
		runAnalysis(input, bkps);
	}
	
	private IcsetlvInput initInput() {
		IcsetlvInput input = new IcsetlvInput();
		VMConfiguration vmConfig = initVmConfig();
		input.setConfig(vmConfig);
		input.setPassTestcases(CollectionUtils.listOf("faultLocaliation.sample.SampleProgramTestPass"));
		input.setFailTestcases(CollectionUtils.listOf("faultLocaliation.sample.SampleProgramTestFail"));
		input.getConfig().addClasspath(config.getTestTarget(TestConfiguration.FALTLOCALISATION));
		return input;
	}
	
	private void runAnalysis(IcsetlvInput input, List<BreakPoint> bkps)
			throws IcsetlvException {
		TestcasesExecutor extractor = new TestcasesExecutor(input.getConfig(), 4);
		TcExecResult result = extractor.execute(input.getPassTestcases(), input.getFailTestcases(),
				bkps);
		System.out.println(result.toString(bkps));
		LibSVM svmrunner = new LibSVM();
		for (BreakPoint bkp : bkps) {
			System.out.println("line: " + bkp.getLineNo());
			Dataset tmpDS = buildDataSet(result, bkp);
			svmrunner.buildClassifier(tmpDS);
			System.out.println(svmrunner.getExplicitDivider().toString());
			System.out.println(svmrunner.modelAccuracy());
			/* Create a feature ranking algorithm */
			RecursiveFeatureEliminationSVM svmrfe = new RecursiveFeatureEliminationSVM(0.2);
			/* Apply the algorithm to the data set */
			svmrfe.build(tmpDS);
			/* Print out the rank of each attribute */
			for (int i = 0; i < svmrfe.noAttributes(); i++)
			    System.out.println(svmrfe.rank(i));
			System.out.println("--------------------------------------------");
		}
	}

	private Dataset buildDataSet(TcExecResult result, BreakPoint bkp) {
		Dataset data = BugExpert.buildDataset(result.getPassValues(bkp),
				result.getFailValues(bkp));
		return data;
	}

	private List<BreakPoint> toBreakpoints(List<LineCoverageInfo> maxSpiList) {
		List<BreakPoint> bkps = new ArrayList<BreakPoint>(maxSpiList.size());
//		for (LineCoverageInfo info : maxSpiList) {
//			String clazz = info.getClassName().replace("/", ".");
//			bkps.add(new BreakPoint(clazz, info.getLineIndex()));
//		}
		
		String clazz = "faultLocaliation.sample.SamplePrograms";
		bkps.add(new BreakPoint(clazz, 16));
		bkps.add(new BreakPoint(clazz, 20));
//		bkps.add(new BreakPoint(clazz, 13, new Variable("result"), new Variable("b")));
//		bkps.add(new BreakPoint(clazz, 18, new Variable("result"), new Variable("b")));
		return bkps;
	}
}
