package learntest.activelearning.core.distribution;

import org.eclipse.core.runtime.CoreException;

import microbat.instrumentation.cfgcoverage.graph.CoverageSFlowGraph;
import sav.common.core.utils.FileUtils;

public class CoverageDistributionRecorder {

	public void record(String prefix, String outputFolder, CoverageSFlowGraph coverageGraph) throws CoreException {
		String distributionFile = FileUtils.getFilePath(outputFolder, prefix + "_distribution.xlsx");
	}
}
