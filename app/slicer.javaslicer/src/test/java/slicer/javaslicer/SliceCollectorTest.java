/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package slicer.javaslicer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import sav.common.core.SavException;
import sav.common.core.SystemVariables;
import sav.common.core.utils.JunitUtils;
import sav.strategies.common.VarInheritCustomizer.InheritType;
import sav.strategies.dto.AppJavaClassPath;
import sav.strategies.dto.BreakPoint;
import slicer.javaslicer.testdata.SliceCollectorTestdata;

/**
 * @author LLT
 * 
 */
public class SliceCollectorTest extends AbstractJavaSlicerTest {
	
	@Override
	public void setup() throws Exception {
		slicer = new JavaSlicer() {
//			@Override
//			public List<BreakPoint> slice(AppJavaClassPath appClassPath,
//					List<BreakPoint> bkps, List<String> junitClassMethods)
//					throws SavException, IOException, InterruptedException,
//					ClassNotFoundException {
//				init(appClassPath);
//				String traceFilePath = "C:/Users/DELL50~1/AppData/Local/Temp/javaSlicer2228226225342205556.trace";
//				List<BreakPoint> result = sliceFromTraceFile(traceFilePath ,
//						new HashSet<BreakPoint>(bkps), junitClassMethods);
//				return result;
//			}
		};
		appClasspath = initAppClasspath();
		appClasspath.getPreferences().putBoolean(SystemVariables.SLICE_COLLECT_VAR, true);
		appClasspath.getPreferences().put(SystemVariables.SLICE_BKP_VAR_INHERIT, InheritType.FORWARD.name());
	}
	
	@Test
	public void testSampleProgram() throws Exception {
		String targetClass = SliceCollectorTestdata.class.getName();
		String testClass = SliceCollectorTestdata.class.getName();
//		BreakPoint bkp = new BreakPoint(testClass, "testSum", 58);
		BreakPoint bkp = new BreakPoint(testClass, "getSum", 45);
		List<BreakPoint> breakpoints = Arrays.asList(bkp);
		analyzedClasses = Arrays.asList(targetClass);
		testClassMethods = JunitUtils.extractTestMethods(Arrays
				.asList(testClass), null);
		run(breakpoints);
	}
}
