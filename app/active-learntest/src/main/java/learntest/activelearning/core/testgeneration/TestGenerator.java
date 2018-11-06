package learntest.activelearning.core.testgeneration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import learntest.activelearning.core.data.MethodInfo;
import learntest.activelearning.core.data.TestInputData;
import learntest.activelearning.core.data.UnitTestSuite;
import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.settings.LearntestSettings;
import microbat.instrumentation.cfgcoverage.graph.Branch;
import microbat.instrumentation.cfgcoverage.graph.cdg.CDGNode;
import sav.strategies.dto.AppJavaClassPath;

public class TestGenerator {
	protected Map<Branch, List<TestInputData>> branchInputMap = new HashMap<>();
	protected UnitTestSuite testsuite;
	protected Tester tester;
	protected AppJavaClassPath appClasspath;
	protected MethodInfo targetMethod;
	protected LearntestSettings settings;
	
	public TestGenerator() {}
	
	public TestGenerator(Map<Branch, List<TestInputData>> branchInputMap, UnitTestSuite testsuite, Tester tester,
			AppJavaClassPath appClasspath, MethodInfo targetMethod, LearntestSettings settings) {
		super();
		this.branchInputMap = branchInputMap;
		this.testsuite = testsuite;
		this.tester = tester;
		this.appClasspath = appClasspath;
		this.targetMethod = targetMethod;
		this.settings = settings;
	}
	
	public boolean isAllChildrenCovered(CDGNode node) {
		if (!node.getCfgNode().isCovered()) {
			return false;
		}

		for (CDGNode child : node.getChildren()) {
			boolean covered = isAllChildrenCovered(child);
			if (!covered) {
				return false;
			}
		}

		return true;
	}

	public Map<Branch, List<TestInputData>> getBranchInputMap() {
		return branchInputMap;
	}

	public void setBranchInputMap(Map<Branch, List<TestInputData>> branchInputMap) {
		this.branchInputMap = branchInputMap;
	}

	public UnitTestSuite getTestsuite() {
		return testsuite;
	}

	public void setTestsuite(UnitTestSuite testsuite) {
		this.testsuite = testsuite;
	}

	public Tester getTester() {
		return tester;
	}

	public void setTester(Tester tester) {
		this.tester = tester;
	}

	public AppJavaClassPath getAppClasspath() {
		return appClasspath;
	}

	public void setAppClasspath(AppJavaClassPath appClasspath) {
		this.appClasspath = appClasspath;
	}

	public MethodInfo getTargetMethod() {
		return targetMethod;
	}

	public void setTargetMethod(MethodInfo targetMethod) {
		this.targetMethod = targetMethod;
	}

	public LearntestSettings getSettings() {
		return settings;
	}

	public void setSettings(LearntestSettings settings) {
		this.settings = settings;
	}
	
	
}
