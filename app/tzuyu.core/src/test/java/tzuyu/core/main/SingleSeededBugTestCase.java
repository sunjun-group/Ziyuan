/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */
package tzuyu.core.main;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import main.FaultLocalization;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import faultLocalization.LineCoverageInfo;

/**
 * This test case is to verify if Tzuyu's algorithm can locate correctly 1 line
 * of code in a program which causes bugs. I.e.: it makes test cases fail. This
 * line is the only 'seeded' bug of the program.<br/>
 * Ideally, the bugs should be seeded dynamically at runtime. I.e.: the Java
 * source codes must be altered at runtime, then compiled and used to run
 * against a fixed set of test cases.<br/>
 * However, due to technical restrictions, we can prepare these data and store
 * it somewhere, then only load and use them when testing.
 * 
 * @author Nguyen Phuoc Nguong Phuc (phuc@sutd.edu.sg)
 * 
 */
//@RunWith(Parameterized.class)
public class SingleSeededBugTestCase extends AbstractTzTest {

	private FaultLocalization analyzer;

	// Parameters
	private List<String> programClasses;
	private List<String> programTestClasses;
	private String expectedBugLine;

	@Before
	public void setup() {
		this.analyzer = new FaultLocalization(testContext);
	}

	public SingleSeededBugTestCase(final List<String> programClasses,
			final List<String> programTestClasses, final String expectedBugLine) {
		this.programClasses = programClasses;
		this.programTestClasses = programTestClasses;
		this.expectedBugLine = expectedBugLine;
	}

	@Parameters
	public static Collection<Object[]> getInputs() {
		Collection<Object[]> data = new ArrayList<Object[]>();
		// TODO NPN Build this list automatically, ex.: read it from a resource file
		data.add(new Object[] {
				Arrays.asList("sav.commons.testdata.SamplePrograms"),
				Arrays.asList("sav.commons.testdata.SampleProgramTestPass",
						"sav.commons.testdata.SampleProgramTestFail"),
				"sav.commons.testdata.SamplePrograms:26" });
		return data;
	}

//	@Test
	public void run() throws Exception {
		// TODO NPN correct CLASSPATH here?
		final List<LineCoverageInfo> infos = analyzer.analyse(programClasses, programTestClasses).getLineCoverageInfos();
		double maxSuspiciousness = -1.0;
		double foundLineSuspiciousness = -1.0;
		for (LineCoverageInfo info : infos) {
			if (maxSuspiciousness < info.getSuspiciousness()) {
				maxSuspiciousness = info.getSuspiciousness();
			}
			if (expectedBugLine.equals(info.getLocId())) {
				foundLineSuspiciousness = info.getSuspiciousness();
			}
		}
		assertTrue("Seeded bug was not found.", foundLineSuspiciousness > 0);
		assertTrue("Seeded bug was found but not with highest suspiciousness.",
				foundLineSuspiciousness == maxSuspiciousness);
	}

}
