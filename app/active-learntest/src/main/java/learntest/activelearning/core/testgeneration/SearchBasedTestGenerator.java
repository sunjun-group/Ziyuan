package learntest.activelearning.core.testgeneration;

import java.util.ArrayList;
import java.util.List;

import learntest.activelearning.core.data.MethodInfo;
import learntest.activelearning.core.data.TestInputData;
import learntest.activelearning.core.data.UnitTestSuite;
import learntest.activelearning.core.handler.Tester;
import learntest.activelearning.core.settings.LearntestSettings;
import learntest.activelearning.core.testgeneration.localsearch.GradientBasedSearch;
import microbat.instrumentation.cfgcoverage.graph.Branch;
import microbat.instrumentation.cfgcoverage.graph.cdg.CDG;
import microbat.instrumentation.cfgcoverage.graph.cdg.CDGNode;
import sav.strategies.dto.AppJavaClassPath;

public class SearchBasedTestGenerator extends TestGenerator{

	public SearchBasedTestGenerator(Tester tester, UnitTestSuite testsuite, AppJavaClassPath appClasspath,
			MethodInfo targetMethod, LearntestSettings settings) {
		this.testsuite = testsuite;
		this.tester = tester;
		this.appClasspath = appClasspath;
		this.targetMethod = targetMethod;
		this.settings = settings;
	}

	public void cover(CDG cdg) {
		this.branchInputMap = buildBranchTestInputMap(testsuite.getInputData(), testsuite.getCoverageGraph());
		for (CDGNode node : cdg.getStartNodes()) {
			traverseLearning(node);
		}
		
	}

	private void traverseLearning(CDGNode branchCDGNode) {
		GradientBasedSearch searchStategy = new GradientBasedSearch(this.branchInputMap, this.testsuite, this.tester,
			this.appClasspath, this.targetMethod, this.settings);
		
		List<CDGNode> decisionChildren = new ArrayList<>();
		for (CDGNode child : branchCDGNode.getChildren()) {
			if (isAllChildrenCovered(child)) {
				continue;
			}

			if (child.getCfgNode().isConditionalNode()) {
				decisionChildren.add(child);
			}

			Branch branch = new Branch(branchCDGNode.getCfgNode(), child.getCfgNode());
			List<TestInputData> inputs = this.branchInputMap.get(branch);
			if (inputs != null) {
				if (inputs.isEmpty()) {
					searchStategy.generateInputByGradientSearch(branch, branchCDGNode);
				}
			}
		}

		for (CDGNode decisionChild : decisionChildren) {
//			Branch b = findParentBranch(parentNode, decisionChild);
			traverseLearning(decisionChild);
		}
	}
}
