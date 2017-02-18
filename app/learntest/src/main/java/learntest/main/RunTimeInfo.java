package learntest.main;

public class RunTimeInfo {
	public long time;
	public double coverage;
	public int testCnt;

	public RunTimeInfo(long time, double coverage, int testCnt) {
		super();
		this.time = time;
		this.coverage = coverage;
		this.testCnt = testCnt;
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

}
