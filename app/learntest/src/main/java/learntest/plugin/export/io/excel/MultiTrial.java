/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.export.io.excel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import learntest.core.RunTimeInfo;
import learntest.core.TestRunTimeInfo;

/**
 * @author LLT
 *
 */
public class MultiTrial extends Trial {
	private double bestL2tRtCoverage;
	private double bestRanRtCoverage;
	private List<Trial> trials = new LinkedList<>();
	private int validNum;

	public void addTrial(Trial trial) {
		if (methodName == null) {
			this.methodName = trial.methodName;
			this.methodLength = trial.methodLength;
			this.methodStartLine = trial.methodStartLine;
		}
		trials.add(trial);
		// this.l2tRtInfo = RunTimeInfo.average(this.l2tRtInfo,
		// trial.l2tRtInfo);
		// this.ranRtInfo = RunTimeInfo.average(this.ranRtInfo,
		// trial.ranRtInfo);
//		this.jdartRtInfo = RunTimeInfo.average(this.jdartRtInfo, trial.jdartRtInfo);
		this.bestL2tRtCoverage = RunTimeInfo.getBestCoverage(this.bestL2tRtCoverage, trial.l2tRtInfo);
		this.bestRanRtCoverage = RunTimeInfo.getBestCoverage(this.bestRanRtCoverage, trial.ranRtInfo);
	}

	public boolean isEmpty() {
		return methodName == null;
	}

	public double getBestL2tRtCoverage() {
		return bestL2tRtCoverage;
	}

	public double getBestRanRtCoverage() {
		return bestRanRtCoverage;
	}

	public double getAdvantage() {
		return bestL2tRtCoverage - bestRanRtCoverage;
	}

	public void setAvgInfo() {
		// int size = trials.size();
		// double l2tCoverage = 0, ranCovergage = 0;
		// double l2tValidCoverage = 0, ranValidCovergage = 0; /** only concern
		// those trials where l2t learn formulas */
		// int validNum = 0;
		// long l2tTime = 0, ranTime = 0;
		// int l2tTestCnt = 0, ranTestCnt = 0;
		// for(Trial trial : trials){
		// if (trial != null) {
		// RunTimeInfo l2TimeInfo = trial.getL2tRtInfo(), ranTimeInfo =
		// trial.getRanRtInfo();
		// l2tCoverage += l2TimeInfo.getCoverage();
		// l2tTime += l2TimeInfo.getTime();
		// l2tTestCnt += l2TimeInfo.getTestCnt();
		//
		// ranCovergage += ranTimeInfo.getCoverage();
		// ranTime += ranTimeInfo.getTime();
		// ranTestCnt += ranTimeInfo.getTestCnt();
		//
		// if (l2TimeInfo instanceof TestRunTimeInfo) {
		// if (((TestRunTimeInfo)l2TimeInfo).getLearnState() > 0) {
		// l2tValidCoverage += l2TimeInfo.getCoverage();
		// ranValidCovergage += ranTimeInfo.getCoverage();
		// validNum++;
		// }
		// }
		// }
		// }
		// this.l2tRtInfo = new TestRunTimeInfo(size==0?0:l2tTime/size,
		// size==0?0:l2tCoverage/size, size==0?0:l2tTestCnt/size, validNum == 0
		// ? 0: l2tValidCoverage/validNum);
		// this.ranRtInfo = new TestRunTimeInfo(size==0?0:ranTime/size,
		// size==0?0:ranCovergage/size, size==0?0:ranTestCnt/size, validNum ==
		// 0? 0:ranValidCovergage/validNum);
		// this.validNum = validNum;

		double l2tValidCoverage = 0, ranValidCovergage = 0; /** only concern those trials where l2t learn formulas */
		int validNum = 0;
		ArrayList<RunTimeInfo> l2tList = new ArrayList<>(trials.size()), 
				ranList = new ArrayList<>(trials.size()),
				jdartList = new ArrayList<>(trials.size());
		
		for (Trial trial : trials) {
			if (trial != null) {
				RunTimeInfo l2TimeInfo = trial.getL2tRtInfo(), ranTimeInfo = trial.getRanRtInfo();
				l2tList.add(l2TimeInfo);
				ranList.add(ranTimeInfo);
				jdartList.add(trial.getJdartRtInfo());
				if (l2TimeInfo instanceof TestRunTimeInfo) {
					if (((TestRunTimeInfo) l2TimeInfo).getLearnState() > 0) {
						l2tValidCoverage += l2TimeInfo.getCoverage();
						ranValidCovergage += ranTimeInfo.getCoverage();
						validNum++;
					}
				}
			}
		}
		this.jdartRtInfo =getSimpleAvgInfo(jdartList);
		this.l2tRtInfo = getSimpleAvgInfo(l2tList);
		this.l2tRtInfo.setCoverage(validNum == 0 ? 0 : l2tValidCoverage / validNum);
		this.ranRtInfo = getSimpleAvgInfo(ranList);		
		this.ranRtInfo.setCoverage(validNum == 0 ? 0 : ranValidCovergage / validNum);
		this.validNum = validNum;
	}

	public TestRunTimeInfo getSimpleAvgInfo(List<RunTimeInfo> infos) {
		int size = infos.size();
		double coverage = 0;
		long time = 0;
		int testCnt = 0;
		for (RunTimeInfo info : infos) {
			coverage += info.getCoverage();
			time += info.getTime();
			testCnt += info.getTestCnt();
		}
		return new TestRunTimeInfo(size == 0 ? 0 : time / size, size == 0 ? 0 : coverage / size,
				size == 0 ? 0 : testCnt / size);
	}

	public List<Trial> getTrials() {
		return trials;
	}

	public int getValidNum() {
		return validNum;
	}

}
