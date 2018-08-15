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
import sav.common.core.SavException;

public class CoverageProgressRecorder {
	private Logger log = LoggerFactory.getLogger(CoverageProgressRecorder.class);
	private String progressfilePath;
	private String caseNumberfilePath;
	private MethodInfo targetMethod;
	private Set<Branch> currentCoveredBranch = new HashSet<>();
	private Set<Branch> allBranches;
	private List<Double> progressCoverages;
	private List<Integer> tcsNum;
	private int curNumberOfTcs;
	
	public CoverageProgressRecorder(MethodInfo targetMethod, String filePath, String caseNumfilePath) {
		this.targetMethod = targetMethod;
		this.progressfilePath = filePath;
		this.caseNumberfilePath = caseNumfilePath;
		this.progressCoverages = new ArrayList<>();
		this.tcsNum = new ArrayList<>();
	}
	
	public void setCoverageGraph(CoverageSFlowGraph graph) {
		allBranches = CoverageUtils.getAllBranches(graph);
	}
	
	public void updateNewCoverage(CoverageSFlowGraph newCoverage, int numberOfTestcases) {
		Set<Branch> coveredBranches = CoverageUtils.getCoveredBranches(newCoverage, targetMethod.getMethodId());
		currentCoveredBranch.addAll(coveredBranches);
		curNumberOfTcs += numberOfTestcases;
	}
	
	public void updateProgress() {
		double coverage = currentCoveredBranch.size() / (double) allBranches.size();
		log.debug("coverage = " + coverage);
		progressCoverages.add(coverage);
		tcsNum.add(curNumberOfTcs);
	}
	
	public void store() throws SavException {
		try {
			System.out.println("Total tcs: " + tcsNum);
			ProgressExcelWriter writer = new ProgressExcelWriter(new File(progressfilePath));
			ProgressRow trial = new ProgressRow();
			trial.setMethodName(targetMethod.getMethodFullName() + '.' + targetMethod.getLineNum());
			double[] progress = new double[progressCoverages.size()];
			int i = 0;
			for (Double cvg : progressCoverages) {
				progress[i++] = cvg;
			}
			trial.setProgress(progress);
			writer.addRowData(trial);
			
			ProgressExcelWriter writer1 = new ProgressExcelWriter(new File(caseNumberfilePath));
			trial.setMethodName(targetMethod.getMethodFullName() + '.' + targetMethod.getLineNum());
			double[] tcsnum = new double[tcsNum.size()];
			i = 0;
			for (Integer num : tcsNum) {
				tcsnum[i++] = (double)(num.intValue()); 
			}
			trial.setProgress(tcsnum);
			writer1.addRowData(trial);
			
		} catch(Exception ex) {
			throw new SavException(ex);
		}
	}
}
