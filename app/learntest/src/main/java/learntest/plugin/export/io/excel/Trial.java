package learntest.plugin.export.io.excel;

import learntest.core.RunTimeInfo;
import learntest.core.TestRunTimeInfo;

public class Trial {
	/**
	 * It should be a full name of method, including declaring class name.
	 */
	protected String methodName;
	protected int methodLength;
	protected int methodStartLine;

	protected RunTimeInfo l2tRtInfo;
	protected RunTimeInfo ranRtInfo;
	protected RunTimeInfo jdartRtInfo;
	protected String l2tWorseThanRand, randWorseThanl2t;
	private int l2tSymbolicTimes = 0;
	private int jdartSymbolicTimes = 0;

	public Trial(){
		
	}
	
	public Trial(String methodName, int methodLength, int methodEntryLineNo, RunTimeInfo l2tAverageInfo,
			RunTimeInfo ranAverageInfo, RunTimeInfo jdartInfo) {
		this.methodName = methodName;
		this.methodLength = methodLength;
		this.methodStartLine = methodEntryLineNo;
		this.l2tRtInfo = l2tAverageInfo;
		this.ranRtInfo = ranAverageInfo;
		this.jdartRtInfo = jdartInfo;
		if (l2tAverageInfo instanceof TestRunTimeInfo) {
			this.l2tWorseThanRand = ((TestRunTimeInfo)l2tAverageInfo).l2tWorseThanRand;
			this.randWorseThanl2t = ((TestRunTimeInfo)l2tAverageInfo).randWorseThanl2t;
			this.l2tSymbolicTimes = ((TestRunTimeInfo)l2tAverageInfo).getSymbolicTimes();
		}
		this.jdartSymbolicTimes = jdartInfo.getSymbolicTimes();
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public int getMethodLength() {
		return methodLength;
	}

	public void setMethodLength(int methodLength) {
		this.methodLength = methodLength;
	}

	public int getMethodStartLine() {
		return methodStartLine;
	}

	public void setMethodStartLine(int methodStartLine) {
		this.methodStartLine = methodStartLine;
	}

	public double getAdvantage() {
		return l2tRtInfo.getCoverage() - ranRtInfo.getCoverage();
	}
	
	public RunTimeInfo getL2tRtInfo() {
		return l2tRtInfo;
	}

	public void setL2tRtInfo(RunTimeInfo l2tRtInfo) {
		this.l2tRtInfo = l2tRtInfo;
	}

	public RunTimeInfo getRanRtInfo() {
		return ranRtInfo;
	}

	public void setRanRtInfo(RunTimeInfo ranRtInfo) {
		this.ranRtInfo = ranRtInfo;
	}

	public RunTimeInfo getJdartRtInfo() {
		return jdartRtInfo;
	}

	public void setJdartRtInfo(RunTimeInfo jdartRtInfo) {
		this.jdartRtInfo = jdartRtInfo;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Trial other = (Trial) obj;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Trial [methodName=" + methodName + ", methodLength=" + methodLength + ", methodStartLine="
				+ methodStartLine + ", l2tRtInfo=" + l2tRtInfo + ", ranRtInfo=" + ranRtInfo + ", jdartRtInfo="
				+ jdartRtInfo + "]";
	}

	public String getL2tWorseThanRand() {
		return l2tWorseThanRand;
	}

	public String getRandWorseThanl2t() {
		return randWorseThanl2t;
	}

	public int getSymbolicTimes() {
		return l2tSymbolicTimes;
	}

	public int getJdartSymbolicTimes() {
		return jdartSymbolicTimes;
	}
	
	
}
