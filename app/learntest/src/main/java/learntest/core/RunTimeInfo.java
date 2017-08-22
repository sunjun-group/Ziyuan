package learntest.core;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cfgcoverage.jacoco.analysis.data.CfgNode;
import icsetlv.common.dto.BreakpointValue;
import learntest.core.machinelearning.CfgNodeDomainInfo;
import learntest.core.machinelearning.FormulaInfo;
import learntest.core.machinelearning.IInputLearner;
import sav.common.core.utils.TextFormatUtils;

public class RunTimeInfo {
	private long time;
	private double coverage;
	private int testCnt;
	
	private String coverageInfo;
	protected int learnState = 0; /** if only learn valid formula 1, also has rubbish learned formula 2; only rubbish -1, no formula 0 */
	List<FormulaInfo> learnedFormulas = new LinkedList<>();
	private double validCoverage;
	public String l2tWorseThanRand = "", randWorseThanl2t = "";

	private HashMap<String, Collection<BreakpointValue>> trueSample = new HashMap<>(),
			falseSample = new HashMap<>();
	private HashMap<CfgNode, CfgNodeDomainInfo> domainMap = new HashMap<>(1);
	private String logFile;
	
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
	
	public RunTimeInfo(long time, double coverage, int testCnt, double validCoverage) {
		this.time = time;
		this.coverage = coverage;
		this.testCnt = testCnt;
		this.validCoverage = validCoverage;
	}
	
	public RunTimeInfo() {
		
	}

	public void add(RunTimeInfo subRunInfo) {
		time += subRunInfo.time;
		coverage += subRunInfo.coverage;
		testCnt += subRunInfo.testCnt;
		learnedFormulas.addAll(subRunInfo.learnedFormulas);
		learnState = subRunInfo.learnState;
		trueSample.putAll(subRunInfo.trueSample);
		falseSample.putAll(subRunInfo.falseSample);
		domainMap = subRunInfo.domainMap;
		logFile = subRunInfo.logFile;
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

	public double getValidCoverage() {
		return validCoverage;
	}

	public List<FormulaInfo> getLearnedFormulas() {
		return learnedFormulas;
	}

	public int getLearnState() {
		return learnState;
	}

	public void setSample(IInputLearner learner) {
		this.trueSample.putAll(learner.getTrueSample());
		this.falseSample.putAll(learner.getFalseSample());		
	}

	public HashMap<String, Collection<BreakpointValue>> getTrueSample() {
		return trueSample;
	}

	public HashMap<String, Collection<BreakpointValue>> getFalseSample() {
		return falseSample;
	}

	public HashMap<CfgNode, CfgNodeDomainInfo> getDomainMap() {
		return domainMap;
	}

	public void setDomainMap(HashMap<CfgNode, CfgNodeDomainInfo> domainMap) {
		this.domainMap = domainMap;
	}

	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public static void write(String file, String log) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file, true);
			writer.write(log);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
}
