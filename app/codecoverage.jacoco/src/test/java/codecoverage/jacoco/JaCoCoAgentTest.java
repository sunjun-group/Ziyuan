/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package codecoverage.jacoco;

import static sav.common.core.Constants.TZUYU_JACOCO_ASSEMBLY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import sav.common.core.Pair;
import sav.common.core.SavPrintStream;
import sav.common.core.utils.StringUtils;
import sav.commons.TestConfiguration;
import sav.commons.testdata.SampleProgramTest;
import sav.commons.testdata.SamplePrograms;
import sav.strategies.codecoverage.ICoverageReport;
import sav.strategies.dto.BreakPoint;
import sav.strategies.vm.VMConfiguration;
import codecoverage.jacoco.agent.JaCoCoAgent;
import codecoverage.jacoco.testdata.CoverageSample;
import codecoverage.jacoco.testdata.CoverageSampleTest;


/**
 * @author LLT
 *
 */
public class JaCoCoAgentTest extends JacocoAbstractTest {
	protected VMConfiguration vmConfig;
	protected JaCoCoAgent jacoco;
	protected ICoverageReport report;
	private List<String> result;
	private Set<String> coveredLines;
	
	@Before
	public void setup() {
		vmConfig = initVmConfig();
		vmConfig.setClasspath(new ArrayList<String>());
		vmConfig.addClasspath(TestConfiguration.SAV_COMMONS_TEST_TARGET);
		vmConfig.addClasspath(TestConfiguration.getTzAssembly(TZUYU_JACOCO_ASSEMBLY));
		vmConfig.addClasspath(TestConfiguration.getTestResources(MODULE));
		jacoco = new JaCoCoAgent(new String[]{});
		jacoco.setVmConfig(vmConfig);
		jacoco.setOut(new SavPrintStream(System.out));
		result = new ArrayList<String>();
		coveredLines = new HashSet<String>();
		report = new ICoverageReport() {

			@Override
			public void setTestingClassNames(List<String> testingClassNames) {
			}

			@Override
			public void addInfo(int testcaseIndex, String className,
					int lineIndex, boolean isPassed, boolean isCovered) {
				String str = String.format(""
						+ "test%s, %s@%s, pass:%s, isCovered:%s",
						testcaseIndex, className, lineIndex, isPassed,
						isCovered);
				result.add(str);
				if (isCovered) {
					coveredLines.add(className + ":" + lineIndex);
				}
			}

			@Override
			public void addFailureTrace(List<BreakPoint> traces) {
				for (BreakPoint bkp : traces) {
					System.out.println(bkp);
				}
			}

			@Override
			public void setFailTests(List<Pair<String, String>> failTests) {
				
			}
		};
	}

	@Test
	public void testSampleProgram() throws Exception {
		List<String> testingClassNames = Arrays.asList(SamplePrograms.class.getName(),
				String.class.getName());
		List<String> junitClassNames = Arrays.asList(SampleProgramTest.class.getName());
		run(testingClassNames, junitClassNames, TestConfiguration.SAV_COMMONS_TEST_TARGET);
		Collections.sort(new ArrayList<String>(coveredLines));
		System.out.println(StringUtils.join(coveredLines, "\n"));
	}
	
	@Test
	public void coverLineButFailTest() throws Exception {
		List<String> testingClassNames = Arrays.asList(CoverageSample.class.getName(),
				String.class.getName());
		List<String> junitClassNames = Arrays.asList(CoverageSampleTest.class.getName());
		run(testingClassNames, junitClassNames, TestConfiguration.getTestTarget("codecoverage.jacoco"));
		Collections.sort(new ArrayList<String>(coveredLines));
		System.out.println(StringUtils.join(coveredLines, "\n"));
	}

	public void run(List<String> testingClassNames,
			List<String> junitClassNames, String classesFolder)
			throws Exception {
		vmConfig.addClasspath(classesFolder);
		jacoco.run(report, testingClassNames, junitClassNames);
	}
}
