/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package tzuyu.core.main.faultLocalization;

import java.util.ArrayList;

import sav.strategies.dto.ClassLocation;
import codecoverage.jacoco.JaCoCoAgentTest;
import faultLocalization.CoverageReport;
import faultLocalization.SpectrumBasedSuspiciousnessCalculator.SpectrumAlgorithm;

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
	public void testSampleProgram() throws Exception {
		super.testSampleProgram();
		getCoverageReport().computeSuspiciousness(new ArrayList<ClassLocation>(), SpectrumAlgorithm.TARANTULA);
	}
	
	private CoverageReport getCoverageReport() {
		return (CoverageReport) report;
	}
}
