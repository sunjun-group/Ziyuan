/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer;

import java.io.IOException;
import java.util.List;

import org.junit.Before;

import sav.common.core.Constants;
import sav.common.core.SavException;
import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.strategies.dto.BreakPoint;
import sav.strategies.vm.VMConfiguration;

/**
 * @author LLT
 * 
 */
public class AbstractJavaSlicerTest extends AbstractTest {
	protected JavaSlicer slicer;
	protected List<String> analyzedClasses;
	protected List<String> testClassMethods;
	protected VMConfiguration vmConfig;

	@Before
	public void setup() {
		slicer = new JavaSlicer();
		vmConfig = initVmConfig();
		vmConfig.addClasspath(TestConfiguration
				.getTzAssembly(Constants.TZUYU_JAVASLICER_ASSEMBLY));
		slicer.setVmConfig(vmConfig);
	}

	protected void run(List<BreakPoint> breakpoints) throws SavException,
			IOException, InterruptedException, ClassNotFoundException {
		slicer.setFiltering(analyzedClasses, null);
		List<BreakPoint> result = slicer.slice(breakpoints, testClassMethods);
		if (result.isEmpty()) {
			System.out.println("EMPTY RESULT!!");
		}
		for (BreakPoint bkp : result) {
			System.out.println(bkp);
		}
	}
}
