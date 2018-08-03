package learntest.core.commons;

import java.util.List;

import learntest.core.commons.data.classinfo.MethodInfo;
import learntest.core.gentest.GentestParams;

public class TimeController {
	private static TimeController timeControler = new TimeController();
	private ExecutionTime gentestExecTime = new ExecutionTime();
	private ExecutionTime coverageExecTime = new ExecutionTime();
	
	public static TimeController getInstance() {
		return timeControler;
	}

	public void logGenTestRunningTime(GentestParams params, long executionTime) {
		gentestExecTime.numberOfExecution += params.getNumberOfTcs();
		gentestExecTime.totalExecTime += executionTime;
	}

	public void logCoverageRunningTime(MethodInfo targetMethod, List<String> junitMethods, long executionTime) {
		coverageExecTime.numberOfExecution ++;
		coverageExecTime.totalExecTime += executionTime;
	}
	
	public void reset() {
		gentestExecTime.reset();
		coverageExecTime.reset();
	}
	
	public ExecutionTime getGentestExecTime() {
		return gentestExecTime;
	}
	
	public ExecutionTime getCoverageExecTime() {
		return coverageExecTime;
	}
	
	public String getLog() {
		StringBuilder sb = new StringBuilder();
		sb.append("");
		return sb.toString();
	}

	public static class ExecutionTime {
		int numberOfExecution;
		long totalExecTime;
		
		public void reset() {
			numberOfExecution = 0;
			totalExecTime = 0;
		}
		
		public long getAverage() {
			return totalExecTime / numberOfExecution;
		}

		public int getNumberOfExecution() {
			return numberOfExecution;
		}

		public long getTotalExecTime() {
			return totalExecTime;
		}
	}

}
