package icsetlv.vm;

import java.util.ArrayList;
import java.util.List;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.dto.VariablesExtractorResult;
import icsetlv.common.dto.VariablesExtractorResult.BreakpointResult;
import icsetlv.common.exception.IcsetlvException;
import icsetlv.iface.IBugAnalyzer;
import icsetlv.svm.DatasetBuilder;
import icsetlv.svm.LibSVM;
import icsetlv.variable.VariablesExtractor;

public class BugAnalyzer implements IBugAnalyzer {

	private VMConfiguration vmConfig;
	private List<String> passTestcases;
	private List<String> failTestcases;

	public BugAnalyzer(List<String> passTestcases, List<String> failTestcases, VMConfiguration vmConfig){
		this.passTestcases = passTestcases;
		this.failTestcases = failTestcases;
		this.vmConfig = vmConfig;
	}

	@Override
	public List<BreakPoint> analyze(List<BreakPoint> breakpoints) throws IcsetlvException {
		List<BreakPoint> bprsout = new ArrayList<BreakPoint>();
		for(BreakPoint bp : breakpoints){
			boolean bugIsFound = false;
			List<BreakpointResult> bprs = CollectData(bp);
			assert bprs.size() == 1;
			DatasetBuilder db = new DatasetBuilder(bprs.get(0));
			LibSVM svmer = new LibSVM();
			svmer.buildClassifier(db.buildDataset());
			Metric metric = new Metric(svmer.modelAccuracy());
			bugIsFound = bugFoundOrNot(metric);
			if(bugIsFound){
				bprsout.add(bp);
				break;
			}
		}
		return bprsout;
	}

	/*
	 * Metric for assertion generation using svm
	 * For now, we use classification accuracy 
	 * and set a hard threshold
	 * */
	private class Metric{
		double modelAccuracy;
		public Metric(double macc){
			this.modelAccuracy = macc;
		}
	}

	/*
	 * Check if we can still generate assertions.
	 * Set the threshold for classification accuracy
	 * */
	private boolean bugFoundOrNot(Metric metric) {
		if(metric.modelAccuracy > 0.7){
			return false;
		}
		return true;
	}

	/*
	 * Collect data values at a certain BreakPoint
	 * for svm
	 * */
	private List<BreakpointResult> CollectData(BreakPoint bp) throws IcsetlvException {
		List<BreakPoint> breakpoints = new ArrayList<BreakPoint>();
		breakpoints.add(bp);
		VariablesExtractor varExtr = new VariablesExtractor(vmConfig, passTestcases, failTestcases, breakpoints);
		VariablesExtractorResult extractedResult = varExtr.execute();
		return extractedResult.getResult();
	}

	public List<String> getPassTestcases() {
		return passTestcases;
	}

	public void setPassTestcases(List<String> passTestcases) {
		this.passTestcases = passTestcases;
	}

	public List<String> getFailTestcases() {
		return failTestcases;
	}

	public void setFailTestcases(List<String> failTestcases) {
		this.failTestcases = failTestcases;
	}

	public VMConfiguration getVmConfig() {
		return vmConfig;
	}

	public void setVmConfig(VMConfiguration vmConfig) {
		this.vmConfig = vmConfig;
	}
}
