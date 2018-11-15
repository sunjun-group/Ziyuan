package learntest.activelearning.core.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import learntest.activelearning.core.data.TestInputData;
import microbat.instrumentation.cfgcoverage.graph.Branch;
import sav.settings.SAVTimer;

public class CoverageTimer implements Runnable {

	private volatile Map<Branch, List<TestInputData>> branchInputMap = new HashMap<>();
	private long timeout = 90000;
	private long interval = 10000;

	private volatile List<Double> progressCoverages = new ArrayList<>();
	private volatile List<Integer> tcsNum = new ArrayList<>();

	
	public CoverageTimer(Map<Branch, List<TestInputData>> branchInputMap, long timeout, long interval) {
		super();
		this.branchInputMap = branchInputMap;
		this.timeout = timeout;
		this.interval = interval;
	}

	@Override
	public void run() {
		long executionTime = SAVTimer.getExecutionTime();

		while (executionTime < this.timeout) {
			double coverage = computeTestCoverage();
			int num = computeTestNumber();
			getProgressCoverages().add(coverage);
			getTcsNum().add(num);
			
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			executionTime = SAVTimer.getExecutionTime();
		}
		
		double coverage = computeTestCoverage();
		int num = computeTestNumber();
		getProgressCoverages().add(coverage);
		getTcsNum().add(num);

	}

	public double computeTestCoverage() {
		double total = this.branchInputMap.keySet().size();
		double count = 0;
		for (Branch branch : this.branchInputMap.keySet()) {
			if (this.branchInputMap.get(branch) != null && !this.branchInputMap.get(branch).isEmpty()) {
				count++;
			}
		}

		return count / total;
	}

	public int computeTestNumber() {
		int total = 0;
		for (Branch branch : this.branchInputMap.keySet()) {
			if (this.branchInputMap.get(branch) != null && !this.branchInputMap.get(branch).isEmpty()) {
				total += this.branchInputMap.get(branch).size();
			}
		}

		return total;
	}

	public List<Double> getProgressCoverages() {
		return progressCoverages;
	}

	public void setProgressCoverages(List<Double> progressCoverages) {
		this.progressCoverages = progressCoverages;
	}

	public List<Integer> getTcsNum() {
		return tcsNum;
	}

	public void setTcsNum(List<Integer> tcsNum) {
		this.tcsNum = tcsNum;
	}
}
