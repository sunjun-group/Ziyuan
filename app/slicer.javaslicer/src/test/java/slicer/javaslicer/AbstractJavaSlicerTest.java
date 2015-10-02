/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;

import sav.common.core.SavException;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.ObjectUtils;
import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
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
	public void setup() throws Exception {
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
	
	@Override
	protected AppJavaClassPath initAppClasspath() {
		AppJavaClassPath appCp = super.initAppClasspath();
		appCp.addClasspath(TestConfiguration.getTestTarget("slicer.javaslicer"));
		return appCp;
	}

	private void printSlicingResult(List<BreakPoint> result) {
		if (result.isEmpty()) {
			System.out.println("EMPTY RESULT!!");
		}
		Collections.sort(result, new Comparator<BreakPoint>() {

			@Override
			public int compare(BreakPoint o1, BreakPoint o2) {
				return ObjectUtils.compare(o1.getLineNo(), o2.getLineNo());
			}
		});
		for (BreakPoint bkp : result) {
			System.out.println(bkp);
		}
	}
}
