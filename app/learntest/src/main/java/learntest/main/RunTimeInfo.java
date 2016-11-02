package learntest.main;

public class RunTimeInfo {
	private long time;
	private double coverage;

	public RunTimeInfo(long time, double coverage) {
		super();
		this.time = time;
		this.coverage = coverage;
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

}
