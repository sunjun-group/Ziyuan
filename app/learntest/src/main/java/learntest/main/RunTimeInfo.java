package learntest.main;

import sav.common.core.utils.TextFormatUtils;

public class RunTimeInfo {
	private long time;
	private double coverage;
	private int testCnt;

	public RunTimeInfo(long time, double coverage, int testCnt) {
		this.time = time;
		this.coverage = coverage;
		this.testCnt = testCnt;
	}
	
	public RunTimeInfo() {
		
	}

	public void add(RunTimeInfo subRunInfo) {
		time += subRunInfo.time;
		coverage += subRunInfo.coverage;
		testCnt += subRunInfo.testCnt;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public double getCoverage() {
		return coverage;
	}

	public void setCoverage(double coverage) {
		this.coverage = coverage;
	}

	public int getTestCnt() {
		return testCnt;
	}

	public void setTestCnt(int testCnt) {
		this.testCnt = testCnt;
	}

	public boolean isZero() {
		return time == 0 && coverage == 0 && testCnt == 0;
	}
	
	public boolean isNotZero() {
		return !isZero();
	}

	public void reduceByTimes(int times) {
		coverage /= times;
		time /= times;
		testCnt /= times;
	}

	@Override
	public String toString() {
		return "[time=" + TextFormatUtils.printTimeString(time) + ", coverage=" + coverage + ", testCnt=" + testCnt + "]";
	}
}
