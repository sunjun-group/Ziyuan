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

import main.ProgramAnalyzer;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import sav.commons.TzuyuTestCase;
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
@RunWith(Parameterized.class)
public class SingleSeededBugTestCase extends AbstractTzTest {

	private ProgramAnalyzer analyzer;

	// Parameters
	private List<String> programClasses;
	private List<String> programTestClasses;
	private String expectedBugLine;

	@Before
	public void init() {
		this.analyzer = new ProgramAnalyzer(getDataProvider());
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
				Arrays.asList("faultLocaliation.sample.SamplePrograms"),
				Arrays.asList("faultLocaliation.sample.SampleProgramTestPass",
						"faultLocaliation.sample.SampleProgramTestFail"),
				"faultLocaliation.sample.SamplePrograms:26" });
		return data;
	}

	@Test
	@Category(TzuyuTestCase.class)
	public void run() throws Exception {
		// TODO NPN correct CLASSPATH here?
		final List<LineCoverageInfo> infos = analyzer.analyse(programClasses, programTestClasses);
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
