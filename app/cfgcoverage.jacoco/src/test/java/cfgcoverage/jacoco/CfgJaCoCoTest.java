/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cfg.CfgNode;
import cfg.DecisionBranchType;
import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.analysis.data.NodeCoverage;
import cfgcoverage.jacoco.testdata.ForSample;
import cfgcoverage.jacoco.testdata.ForSampleTest;
import cfgcoverage.jacoco.testdata.IfInLoop;
import cfgcoverage.jacoco.testdata.IfInLoopTest;
import cfgcoverage.jacoco.testdata.IfSample;
import cfgcoverage.jacoco.testdata.IfSampleTest;
import cfgcoverage.jacoco.testdata.LoopHeaderSample;
import cfgcoverage.jacoco.testdata.LoopHeaderSampleTest;
import cfgcoverage.jacoco.testdata.LoopSample;
import cfgcoverage.jacoco.testdata.LoopSampleTest;
import cfgcoverage.jacoco.testdata.MultiLevelLoopSample;
import cfgcoverage.jacoco.testdata.MultiLevelLoopSampleTest;
import cfgcoverage.jacoco.testdata.NestedLoopConditionSample;
import cfgcoverage.jacoco.testdata.NestedLoopConditionSampleTest;
import cfgcoverage.jacoco.testdata.SwitchSample;
import cfgcoverage.jacoco.testdata.SwitchSampleTest;
import cfgcoverage.jacoco.testdata.Testing;
import cfgcoverage.jacoco.testdata.Testing_ESTest;
import cfgcoverage.jacoco.utils.CoverageUtils;
import sav.common.core.SystemVariables;
import sav.common.core.utils.ClassUtils;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StopTimer;
import sav.commons.AbstractTest;
import sav.commons.TestConfiguration;
import sav.commons.testdata.SampleProgramTest;
import sav.commons.testdata.SamplePrograms;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 * TODO LLT: to test
 */
public class CfgJaCoCoTest extends AbstractTest {
	private boolean runSimpleRunner = false;
	private long timeout = 5000;
	private AppJavaClassPath appClasspath;
	
	@Before
	public void beforeMethod() throws Exception {
		appClasspath = initAppClasspath();
		appClasspath.getPreferences().set(SystemVariables.TESTCASE_TIMEOUT, timeout);
	}

	public Map<String, CfgCoverage> run(List<String> targetMethods, List<String> testingClassNames,
			List<String> junitClassNames, String classesFolder) throws Exception {
		appClasspath.addClasspath(classesFolder);
		CfgJaCoCo jacoco = new CfgJaCoCo(appClasspath);
		if (runSimpleRunner) {
			return jacoco.runBySimpleRunner(targetMethods, testingClassNames, junitClassNames);
		}
		return jacoco.runJunit(targetMethods, testingClassNames, junitClassNames);
	}
	
	private CfgCoverage runTest(Class<?> targetClass, Class<?> junitClass, String targetMethod) throws Exception {
		StopTimer timer = new StopTimer("test");
		timer.start();
		timer.newPoint("start");
		List<String> testingClassNames = Arrays.asList(targetClass.getName());
		List<String> junitClassNames = Arrays.asList(junitClass.getName());
		List<String> targetMethods = CollectionUtils.listOf(ClassUtils.toClassMethodStr(targetClass.getName(), targetMethod));
		Map<String, CfgCoverage> result = run(targetMethods, testingClassNames, junitClassNames,
				TestConfiguration.getTestTarget("cfgcoverage.jacoco"));
		timer.newPoint("stop");
		CfgCoverage values = result.values().iterator().next();
		System.out.println(values);
		System.out.println(timer.getResults());
		return values;
	}

	@Test
	public void testSampleProgram() throws Exception {
		runSimpleRunner = false;
		appClasspath.setTarget(TestConfiguration.getTestTarget(SAV_COMMONS));
		CfgCoverage cfgCoverage = runTest(SamplePrograms.class, SampleProgramTest.class, "Max");
		Assert.assertEquals(
				"NodeCoverage [node[6,IF_ICMPLE,line 10], decis{T=7,F=14}], coveredTcs={0=1, 1=1, 2=1, 3=1, 4=1}, coveredBranches={7=[1, 4], 14=[0, 2, 3]}]",
				cfgCoverage.getCoverage(6).toString());
		Assert.assertEquals(
				"NodeCoverage [node[16,IF_ICMPLE,line 18], decis{T=17,F=26}], coveredTcs={0=1, 1=1, 2=1, 3=1, 4=1}, coveredBranches={17=[1, 4], 26=[0, 2, 3]}]",
				cfgCoverage.getCoverage(16).toString());
		System.out.println();
	}
	

	@Test
	public void testTest() throws Exception {
		runSimpleRunner = false;
		appClasspath.setTarget(TestConfiguration.getTestTarget("cfgcoverage.jacoco"));
		CfgCoverage cfgCoverage = runTest(Testing.class, Testing_ESTest.class, "test");
		System.out.println();
		System.out.println(CoverageUtils.getBranchCoverageDisplayText(cfgCoverage));
		System.out.println("Coverage: " + CoverageUtils.calculateCoverageByBranch(cfgCoverage));
	}

	@Test
	public void testLoopProgram() throws Exception {
		runSimpleRunner = true;
		CfgCoverage cfgCoverage = runTest(LoopSample.class, LoopSampleTest.class, "run");
		Assert.assertEquals(
				"node[12,IFLT,line 22], decis{T=4,F=13}, loopHeader]", 
				cfgCoverage.getCoverage(12).getCfgNode().toString());
		System.out.println();
	}
	
	@Test
	/**
	 * TODO: How should we handle switch case?
	 */
	public void testSwitch() throws Exception {
		runSimpleRunner = true;
		CfgCoverage cfgCoverage = runTest(SwitchSample.class, SwitchSampleTest.class, "getName");
		System.out.println();
	}
	
	@Test
	public void testFor() throws Exception {
		runSimpleRunner = true;
		CfgCoverage cfgCoverage = runTest(ForSample.class, ForSampleTest.class, "run");
		Assert.assertEquals(
				"NodeCoverage [node[5,IF_ICMPNE,line 19], decis{T=6,F=11}, inloop], coveredTcs={0=3}, coveredBranches={6=[0], 11=[0]}]", 
				cfgCoverage.getCoverage(5).toString());
		Assert.assertEquals(
				"NodeCoverage [node[14,IF_ICMPLT,line 18], decis{T=3,F=15}, loopHeader], coveredTcs={0=3}, coveredBranches={3=[0]}]", 
				cfgCoverage.getCoverage(14).toString());
		System.out.println();
	}
	
	@Test
	public void testIfInLoop() throws Exception {
		runSimpleRunner = true;
		CfgCoverage cfgCoverage = runTest(IfInLoop.class, IfInLoopTest.class, "run");
		Assert.assertEquals(
				"NodeCoverage [node[5,IF_ICMPNE,line 19], decis{T=6,F=9}, inloop], coveredTcs={0=20}, coveredBranches={6=[0], 9=[0]}]", 
				cfgCoverage.getCoverage(5).toString());
		Assert.assertEquals(
				"NodeCoverage [node[17,IF_ICMPLT,line 18], decis{T=3,F=18}, loopHeader], coveredTcs={0=22}, coveredBranches={18=[0], 3=[0]}]", 
				cfgCoverage.getCoverage(17).toString());
		Assert.assertEquals(
				"NodeCoverage [node[19,IFLE,line 25], decis{T=20,F=23}], coveredTcs={0=2}, coveredBranches={20=[0], 23=[0]}]", 
				cfgCoverage.getCoverage(19).toString());
		System.out.println();
	}
	
	@Test
	/**
	 * TODO LLT:
	 * FALSE
	 * first condition is covered which make the result not make sense.
	 * 
	 * */
	public void testIfMultiCondOr() throws Exception {
		runSimpleRunner = true;
		CfgCoverage cfgCoverage = runTest(IfSample.class, IfSampleTest.class, "multiCondOr");
		Assert.assertEquals(
				"NodeCoverage [node[2,IF_ICMPEQ,line 30], decis{T=8,F=3}], coveredTcs={0=1, 3=1, 4=1}, coveredBranches={3=[3, 4], 8=[0]}]", 
				cfgCoverage.getCoverage(2).toString());
		Assert.assertEquals(
				"NodeCoverage [node[4,IFGT,line 30], decis{T=8,F=5}], coveredTcs={3=1, 4=1}, coveredBranches={5=[4], 8=[3]}]", 
				cfgCoverage.getCoverage(4).toString());
		Assert.assertEquals(
				"NodeCoverage [node[7,IF_ICMPLE,line 30], decis{T=15,F=8}], coveredTcs={4=1}, coveredBranches={8=[4]}]", 
				cfgCoverage.getCoverage(7).toString());
		Assert.assertEquals(
				"NodeCoverage [node[12,IF_ICMPGE,line 32], decis{T=15,F=13}], coveredTcs={0=1, 3=1, 4=1}, coveredBranches={13=[3, 4], 15=[0]}]", 
				cfgCoverage.getCoverage(12).toString());
		System.out.println();
	}
	
	public void testIfMultiCondAnd() throws Exception {
		runSimpleRunner = true;
		CfgCoverage cfgCoverage = runTest(IfSample.class, IfSampleTest.class, "multiCondAnd");
		System.out.println();
	}
	
	public void testIfMultiCondAndOr() throws Exception {
		runSimpleRunner = true;
		CfgCoverage cfgCoverage = runTest(IfSample.class, IfSampleTest.class, "multiCondAndOr");
		System.out.println();
	}
	
	@Test
	public void testLoopHeader() throws Exception {
		runSimpleRunner = true;
		CfgCoverage cfgCoverage = runTest(LoopHeaderSample.class, LoopHeaderSampleTest.class, "multiLoopCond");
		cfgCoverage = runTest(LoopHeaderSample.class, LoopHeaderSampleTest.class, "multiLoopCondNeg");
		cfgCoverage = runTest(LoopHeaderSample.class, LoopHeaderSampleTest.class, "singleLoopCond");
		cfgCoverage = runTest(LoopHeaderSample.class, LoopHeaderSampleTest.class, "forLoop");
		cfgCoverage = runTest(LoopHeaderSample.class, LoopHeaderSampleTest.class, "doWhileMultiCond");
		cfgCoverage = runTest(LoopHeaderSample.class, LoopHeaderSampleTest.class, "doWhileSingleCondWithInLoopCond");
	}
	
	@Test
	public void testMultiLevelLoop() throws Exception {
		runSimpleRunner = true;
		runTest(MultiLevelLoopSample.class, MultiLevelLoopSampleTest.class, "run");
	}
	
	@Test
	public void testNestedLoopCondition() throws Exception {
		runSimpleRunner = true;
		CfgCoverage cfgCoverage = runTest(NestedLoopConditionSample.class, NestedLoopConditionSampleTest.class, "run");
		Assert.assertEquals(
				"NodeCoverage [node[14,IFEQ,line 21], decis{T=19,F=15}, inloop], coveredTcs={0=2}, coveredBranches={15=[0]}]"
				, cfgCoverage.getCoverage(14).toString());
		Assert.assertEquals(
				"NodeCoverage [node[17,IF_ICMPLT,line 21], decis{T=7,F=18}, loopHeader], coveredTcs={0=2}, coveredBranches={18=[0], 7=[0]}]", 
				cfgCoverage.getCoverage(17).toString());
		Assert.assertEquals(
				"NodeCoverage [node[21,IF_ICMPLT,line 21], decis{T=7,F=22}, loopHeader], coveredTcs={}, coveredBranches={}]", 
				cfgCoverage.getCoverage(21).toString());
		Assert.assertEquals(
				"NodeCoverage [node[24,IF_ICMPLE,line 26], decis{T=28,F=25}], coveredTcs={0=1}, coveredBranches={25=[0]}]", 
				cfgCoverage.getCoverage(24).toString());
		Assert.assertEquals(
				"NodeCoverage [node[30,IF_ICMPNE,line 29], decis{T=34,F=31}], coveredTcs={0=1}, coveredBranches={34=[0]}]", 
				cfgCoverage.getCoverage(30).toString());
		System.out.println();
	}
}
