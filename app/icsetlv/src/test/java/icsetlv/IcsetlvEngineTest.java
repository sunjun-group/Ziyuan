/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package icsetlv;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.dto.BreakPoint.Variable;
import icsetlv.common.dto.VariablesExtractorResult;
import icsetlv.common.dto.VariablesExtractorResult.BreakpointResult;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.svm.DatasetBuilder;
import icsetlv.svm.LibSVM;
import icsetlv.variable.AssertionDetector;
import icsetlv.variable.VariablesExtractor;
import icsetlv.vm.VMConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.featureselection.ranking.RecursiveFeatureEliminationSVM;

import org.junit.Before;
import org.junit.Test;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;

/**
 * @author LLT
 *
 */
public class IcsetlvEngineTest extends AbstractTest {
	private IcsetlvEngine engine;
	
	@Before
	public void beforeTest() {
		engine = new IcsetlvEngine();
	}

	@Test
	public void testAnalyze() throws IcsetlvException, IOException,
			InterruptedException, IncompatibleThreadStateException,
			AbsentInformationException {
		IcsetlvInput input = initInput();
		List<BreakPoint> bkps = AssertionDetector.scan(input.getAssertionSourcePaths());
		
		BreakPoint bkp4 = new BreakPoint("testdata.slice.FindMax", "findMax");
		bkp4.addVars(new Variable("max"));
		bkp4.addVars(new Variable("i"));
		bkp4.setLineNo(15);
		bkps.add(bkp4);	
		
		BreakPoint bkp3 = new BreakPoint("testdata.slice.FindMax", "findMax"); 
		bkp3.addVars(new Variable("max"));
		bkp3.setLineNo(11);
		bkps.add(bkp3);
		
		printBkps(bkps);
		VariablesExtractor extractor = new VariablesExtractor(input.getConfig());
		VariablesExtractorResult result = extractor.execute(input.getPassTestcases(), input.getFailTestcases(),
				bkps);
		print(result);
		List<BreakpointResult> bprs = result.getResult();
		List<DatasetBuilder> dbs = new ArrayList<DatasetBuilder>();
		for(BreakpointResult bpr : bprs){
			dbs.add(new DatasetBuilder(bpr));
		}
		LibSVM svmrunner = new LibSVM();
		for(DatasetBuilder db : dbs){
			Dataset tmpDS = db.buildDataset();
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
			
		}
	}
	
	private IcsetlvInput initInput() {
		IcsetlvInput input = new IcsetlvInput();
		VMConfiguration vmConfig = initVmConfig();
		input.setConfig(vmConfig);
		input.setAssertionSourcePaths(getTestcasesSourcePaths());
		input.setPassTestcases(Arrays.asList(getPassTestcases()));
		input.setFailTestcases(Arrays.asList(getFailTestcases()));
		return input;
	}
	
	private String[] getPassTestcases() {
		return new String[] {
				"example.MaxFind.test.MaxFindPassTest"
			};
	}
	
	private String[] getFailTestcases() {
		return new String[] {
				"example.MaxFind.test.MaxFindFailTest"
			};
	}
	
	private Map<String, List<String>> getTestcasesSourcePaths() {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		result.put(config.getSourcepath() + "/testdata/slice/FindMax.java",
				//"/testdata/boundedStack/BoundedStack.java", 
				null);
		return result;
	}
}
