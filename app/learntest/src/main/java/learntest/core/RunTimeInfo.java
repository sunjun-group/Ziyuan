package learntest.core;

import sav.common.core.utils.TextFormatUtils;

public class RunTimeInfo {
	private long time;
	private double coverage;
	private int testCnt;
	
	private String coverageInfo;

	public RunTimeInfo(long time, double coverage, int testCnt) {
		this.time = time;
		this.coverage = coverage;
		this.testCnt = testCnt;
	}
	
	public RunTimeInfo(long time, double coverage, int testCnt, String coverageInfo) {
		this.time = time;
		this.coverage = coverage;
		this.testCnt = testCnt;
		this.coverageInfo = coverageInfo;
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

	public static RunTimeInfo average(RunTimeInfo info1, RunTimeInfo info2) {
		if (isEmpty(info1)) {
			return info2;
		}
		if (isEmpty(info2)) {
			return info1;
		}
		long avgTime = (info1.time + info2.time) / 2;
		double avgCoverage = (info1.coverage + info2.coverage) / 2;
		int avgTestCnt = (info1.testCnt + info2.testCnt) / 2;
		return new RunTimeInfo(avgTime, avgCoverage, avgTestCnt);
	}

	public static boolean isEmpty(RunTimeInfo info) {
		return info == null || info.isZero();
	}

	public static double getBestCoverage(double bestL2tRtCoverage, RunTimeInfo info2) {
		return Math.max(bestL2tRtCoverage, getCoverage(info2));
	}

	private static double getCoverage(RunTimeInfo info) {
		if (isEmpty(info)) {
			return 0.0;
		}
		return info.getCoverage();
	}

	public String getCoverageInfo() {
		return coverageInfo;
	}

	public void setCoverageInfo(String coverageInfo) {
		this.coverageInfo = coverageInfo;
	}
	
	
}
