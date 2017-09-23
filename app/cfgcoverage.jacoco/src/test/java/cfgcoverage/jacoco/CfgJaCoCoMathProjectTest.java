/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package cfgcoverage.jacoco;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.experimental.categories.Categories.ExcludeCategory;

import cfgcoverage.jacoco.analysis.data.BranchRelationship;
import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import cfgcoverage.jacoco.analysis.data.CfgNode;
import cfgcoverage.jacoco.analysis.data.NodeCoverage;
import cfgcoverage.jacoco.test.utils.ProjClassLoader;
import sav.common.core.SystemVariables;
import sav.commons.AbstractTest;
import sav.strategies.dto.AppJavaClassPath;

/**
 * @author LLT
 *
 */
@ExcludeCategory
public class CfgJaCoCoMathProjectTest extends AbstractTest {
	private static final String MATH_PROJ_BIN_FOLDER = "/Users/lylytran/apache-common-math-2.2/apache-common-math-2.2/bin";
	private static final String HAMCREST_CORE_JAR_PATH = "/Applications/Eclipse.app/Contents/Eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar";
	private static final String JUNIT_JAR_PATH = "/Applications/Eclipse.app/Contents/Eclipse/plugins/org.junit_4.12.0.v201504281640/junit.jar";
	
	
	public void run(List<String> targetMethods, List<String> testingClassNames, List<String> junitClassNames)
			throws Exception {
		AppJavaClassPath appClasspath = initAppClasspath();
		appClasspath.clearClasspath();
		appClasspath.addClasspath(HAMCREST_CORE_JAR_PATH);
		appClasspath.addClasspath(JUNIT_JAR_PATH);
		appClasspath.addClasspath(MATH_PROJ_BIN_FOLDER);
		appClasspath.getPreferences().set(CfgJaCoCoConfigs.DUPLICATE_FILTER, true);
		appClasspath.getPreferences().set(SystemVariables.PROJECT_CLASSLOADER,
				ProjClassLoader.getClassLoader(appClasspath.getClasspaths()));
		appClasspath.setTarget(MATH_PROJ_BIN_FOLDER);
		appClasspath.getPreferences().set(SystemVariables.TESTCASE_TIMEOUT, -1l);
		CfgJaCoCo jacoco = new CfgJaCoCo(appClasspath);
		Map<String, CfgCoverage> result = jacoco.runBySimpleRunner(targetMethods, testingClassNames, junitClassNames);
//		System.out.println(TextFormatUtils.printMap(result));
		printDecisionNodes(result.values().iterator().next());
		
	}

	private void printDecisionNodes(CfgCoverage cfgCoverage) {
		for (CfgNode node : cfgCoverage.getCfg().getDecisionNodes()) {
			StringBuilder sb = new StringBuilder();
			NodeCoverage nodeCvg = cfgCoverage.getCoverage(node);
			Set<BranchRelationship> coveredBranches = new HashSet<BranchRelationship>(2);
			for (int branchIdx : nodeCvg.getCoveredBranches()) {
				BranchRelationship branchRelationship = node.getBranchRelationship(branchIdx);
				coveredBranches.add(branchRelationship == BranchRelationship.TRUE ? branchRelationship : 
										BranchRelationship.FALSE);
			}
			sb.append("NodeCoverage [").append(node).append(", coveredTcs=").append(nodeCvg.getCoveredTcsTotal())
						.append(", coveredBranches=").append(nodeCvg.getCoveredBranches().size()).append(", ")
						.append(coveredBranches).append("]");
			System.out.println(sb.toString());
		}
	}
	
	@Test
	public void runFastMathSinh() throws Exception {
		List<String> targetClasses = Arrays.asList("org.apache.commons.math.util.FastMath");
		List<String> targetMethods = Arrays.asList("org.apache.commons.math.util.FastMath.asinh");
		List<String> junitClassNames = Arrays.asList("testdata.testcase.FastMath8", "testdata.testcase.FastMath41");
		run(targetMethods, targetClasses, junitClassNames);
	}

	@Test
	public void runLoessInterpolator() throws Exception {
		List<String> targetClasses = Arrays.asList("org.apache.commons.math.analysis.interpolation.LoessInterpolator");
		List<String> targetMethods = Arrays.asList("org.apache.commons.math.analysis.interpolation.LoessInterpolator.smooth");
		List<String> junitClassNames = Arrays.asList("testdata.bug.i87.l2t.init.loessinterpolator.smooth.LoessInterpolator1");
		run(targetMethods, targetClasses, junitClassNames);
	}
}
