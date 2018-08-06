package learntest.activelearning.core.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gentest.core.data.Sequence;
import icsetlv.common.dto.BreakpointValue;
import icsetlv.common.utils.BreakpointDataUtils;
import learntest.core.gan.InputDatapointMapping;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFlowGraph;
import sav.common.core.utils.JunitUtils;
import sav.strategies.dto.execute.value.ExecVar;

/**
 * @author LLT
 *
 */
public class UnitTestSuite {
	private List<String> junitClassNames;
	private String mainClass;
	private List<File> junitfiles;
	private List<String> junitTestcases;
	private List<TestInputData> inputData; //inputData.get(i) might be null in case target method isn't even reached. 
	private List<ExecVar> inputVars;
	private Map<String, Sequence> testcaseSequenceMap;
	private InputDatapointMapping inputDpMapping;
	private CoverageSFlowGraph coverageGraph;

	public void setInputData(List<TestInputData> inputData) {
		this.inputData = inputData;
		if (inputDpMapping == null) {
			List<BreakpointValue> bkpValues = new ArrayList<>(inputData.size());
			for (TestInputData input : inputData) {
				if (input != null) {
					bkpValues.add(input.getInputValue());
				}
			}
			inputVars = BreakpointDataUtils.collectAllVars(bkpValues);
			inputDpMapping = new InputDatapointMapping(inputVars);
		}
	}

	public Map<String, Sequence> getTestcaseSequenceMap() {
		return testcaseSequenceMap;
	}

	public void setTestcaseSequenceMap(Map<String, Sequence> testcaseSequenceMap) {
		this.testcaseSequenceMap = testcaseSequenceMap;
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
		// TODO-LLT: Merge coverage.
//		this.coverage.merge(newTestSuite.coverage);
	}

	public String getMainClass() {
		return mainClass;
	}

	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}
	
}
