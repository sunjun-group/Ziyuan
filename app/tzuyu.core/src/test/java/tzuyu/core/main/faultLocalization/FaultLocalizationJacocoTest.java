/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main.faultLocalization;

import codecoverage.jacoco.JaCoCoAgentTest;
import faultLocalization.CoverageReport;

/**
 * @author LLT
 *
 */
public class FaultLocalizationJacocoTest extends JaCoCoAgentTest {
	
	@Override
	public void setup() {
		super.setup();
		report = new CoverageReport(); 
	}
	
	@Override
	public void testRunTestdata() throws Exception {
		super.testRunTestdata();
		getCoverageReport().tarantula();
	}
	
	@Override
	public void testSampleProgram() throws Exception {
		super.testSampleProgram();
		getCoverageReport().tarantula();
	}
	
	private CoverageReport getCoverageReport() {
		return (CoverageReport) report;
	}
}
