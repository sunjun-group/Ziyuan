package learntest.activelearning.core.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cfg.CfgNode;
import cfgcoverage.jacoco.analysis.data.CfgCoverage;
import gentest.core.data.Sequence;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.utils.BreakpointDataUtils;
import learntest.core.gan.InputDatapointMapping;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFlowGraph;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.JunitUtils;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class UnitTestSuite {
	private List<String> junitClassNames;
	private List<File> junitfiles;
	private List<String> junitTestcases;
	private List<BreakpointValue> inputData;
	private List<ExecVar> inputVars;
	private Map<String, Sequence> testcaseSequenceMap;
	private CfgCoverage coverage;
	private InputDatapointMapping inputDpMapping;
	private CoverageSFlowGraph coverageGraph;

	public void addInputData(BreakpointValue value) {
		if (inputData == null) {
			inputData = new ArrayList<BreakpointValue>();
		}
		inputData.add(value);
	}
	
	public List<double[]> getUnCoveredInputData(CfgNode branch) {
		List<Integer> coveredTcs = coverage.getCoverage(branch).getAllCoveredTcs();
		List<BreakpointValue> inputValue = new ArrayList<>(inputData.size() - CollectionUtils.getSize(coveredTcs));
		for (int tcIdx = 0; tcIdx < inputData.size(); tcIdx++) {
			if (!coveredTcs.contains(tcIdx)) {
				inputValue.add(inputData.get(tcIdx));
			}
		}
		return inputDpMapping.getDatapoints(inputValue);
	}

	public List<double[]> getCoveredInputData(CfgNode branch) {
		List<Integer> coveredTcs = coverage.getCoverage(branch).getAllCoveredTcs();
		List<BreakpointValue> inputValue = new ArrayList<>(CollectionUtils.getSize(coveredTcs));
		for (int tcIdx : coveredTcs) {
			inputValue.add(inputData.get(tcIdx));
		}
		return inputDpMapping.getDatapoints(inputValue);
	}

	public void setInputData(List<BreakpointValue> inputData) {
		this.inputData = inputData;
		if (inputDpMapping == null) {
			inputVars = BreakpointDataUtils.collectAllVars(inputData);
			inputDpMapping = new InputDatapointMapping(inputVars);
		}
	}

	public Map<String, Sequence> getTestcaseSequenceMap() {
		return testcaseSequenceMap;
	}

	public void setTestcaseSequenceMap(Map<String, Sequence> testcaseSequenceMap) {
		this.testcaseSequenceMap = testcaseSequenceMap;
	}

	public CfgCoverage getCoverage() {
		return coverage;
	}

	public void setCoverage(CfgCoverage coverage) {
		this.coverage = coverage;
	}
	
	public void setCoverageGraph(CoverageSFlowGraph coverageGraph) {
		this.coverageGraph = coverageGraph;
	}
	
	public CoverageSFlowGraph getCoverageGraph() {
		return coverageGraph;
	}
	
	public List<String> getJunitClassNames() {
		return junitClassNames;
	}

	public void setJunitClassNames(List<String> junitClasses, ClassLoader classLoader) {
		this.junitClassNames = junitClasses;
		this.junitTestcases = JunitUtils.extractTestMethods(junitClasses, classLoader);
	}

	public List<File> getJunitfiles() {
		return junitfiles;
	}

	public void setJunitfiles(List<File> junitfiles) {
		this.junitfiles = junitfiles;
	}
	
	public List<String> getJunitTestcases() {
		return junitTestcases;
	}
	
	public List<ExecVar> getInputVars() {
		return inputVars;
	}

	public void addTestCases(UnitTestSuite newTestSuite) {
		this.junitClassNames.addAll(newTestSuite.getJunitClassNames());
		this.junitfiles.addAll(newTestSuite.junitfiles);
		this.junitTestcases.addAll(newTestSuite.junitTestcases);
		this.inputData.addAll(newTestSuite.inputData);
		if (this.testcaseSequenceMap == null) {
			this.testcaseSequenceMap = newTestSuite.testcaseSequenceMap;
		} else if (newTestSuite.testcaseSequenceMap != null) {
			this.testcaseSequenceMap.putAll(newTestSuite.testcaseSequenceMap);
		}
		this.coverage.merge(newTestSuite.coverage);
	}
}
