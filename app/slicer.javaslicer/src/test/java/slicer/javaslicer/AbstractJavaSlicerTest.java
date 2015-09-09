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

import sav.common.core.SavException;
import sav.commons.AbstractTest;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;

/**
 * @author LLT
 * 
 */
public class AbstractJavaSlicerTest extends AbstractTest {
	protected JavaSlicer slicer;
	protected List<String> analyzedClasses;
	protected List<String> testClassMethods;
	protected AppJavaClassPath appClasspath;
	
	@Before
	public void setup() {
		slicer = new JavaSlicer();
		appClasspath = initAppClasspath();
	}

	protected void run(List<BreakPoint> breakpoints) throws SavException,
			IOException, InterruptedException, ClassNotFoundException {
		slicer.setFiltering(analyzedClasses, null);
		List<BreakPoint> result = slicer.slice(initAppClasspath(), breakpoints,
				testClassMethods);
		printSlicingResult(result);
	}

	private void printSlicingResult(List<BreakPoint> result) {
		if (result.isEmpty()) {
			System.out.println("EMPTY RESULT!!");
		}
		for (BreakPoint bkp : result) {
			System.out.println(bkp);
		}
	}
}
