package learntest.evaluation.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import learntest.activelearning.core.coverage.CoverageUtils;
import learntest.activelearning.core.progress.ProgressExcelWriter;
import learntest.activelearning.core.progress.ProgressRow;
import learntest.core.commons.data.classinfo.MethodInfo;
import microbat.instrumentation.cfgcoverage.graph.Branch;
import microbat.instrumentation.cfgcoverage.graph.CoverageSFlowGraph;

public class CoverageProgressRecorder {
	private Logger log = LoggerFactory.getLogger(CoverageProgressRecorder.class);
	private MethodInfo targetMethod;
	private Set<Branch> currentCoveredBranch = new HashSet<>();
	private Set<Branch> allBranches;
	private List<Double> progressCoverages;
	
	public CoverageProgressRecorder(MethodInfo targetMethod) {
		this.targetMethod = targetMethod;
		this.progressCoverages = new ArrayList<>();
	}
	
	public void setCoverageGraph(CoverageSFlowGraph graph) {
		allBranches = CoverageUtils.getAllBranches(graph);
	}
	
	public void updateNewCoverage(CoverageSFlowGraph newCoverage) {
		Set<Branch> coveredBranches = CoverageUtils.getCoveredBranches(newCoverage, targetMethod.getMethodId());
		currentCoveredBranch.addAll(coveredBranches);
		double coverage = coveredBranches.size() / (double) allBranches.size();
		log.debug("coverage = " + coverage);
		progressCoverages.add(coverage);
	}
	
	public void store() throws Exception {
		ProgressExcelWriter writer = new ProgressExcelWriter(new File("D:/progress.xlsx"));
		ProgressRow trial = new ProgressRow();
		trial.setMethodName(targetMethod.getMethodFullName() + '.' + targetMethod.getLineNum());
		double[] progress = new double[progressCoverages.size()];
		int i = 0;
		for (Double cvg : progressCoverages) {
			progress[i++] = cvg;
		}
		trial.setProgress(progress);
		writer.addRowData(trial);
	}
}
