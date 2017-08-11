/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.plugin.export.io.excel;

import java.util.LinkedList;
import java.util.List;

import learntest.core.RunTimeInfo;

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
//		this.l2tRtInfo = RunTimeInfo.average(this.l2tRtInfo, trial.l2tRtInfo);
//		this.ranRtInfo = RunTimeInfo.average(this.ranRtInfo, trial.ranRtInfo);
		this.jdartRtInfo = RunTimeInfo.average(this.jdartRtInfo, trial.jdartRtInfo);
		this.bestL2tRtCoverage = RunTimeInfo.getBestCoverage(this.bestL2tRtCoverage, trial.l2tRtInfo);
		this.bestRanRtCoverage = RunTimeInfo.getBestCoverage(this.bestRanRtCoverage, trial.ranRtInfo);
	}
	
	public boolean isEmpty() {
		return methodName == null;
//		return methodName == null || RunTimeInfo.isEmpty(l2tRtInfo);
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
		int size = trials.size();
		double l2tCoverage = 0, ranCovergage = 0; 
		double l2tValidCoverage = 0, ranValidCovergage = 0; /** only concern those trials where l2t learn formulas */
		int validNum = 0;
		long l2tTime = 0, ranTime = 0;
		int l2tTestCnt = 0, ranTestCnt = 0;
		for(Trial trial : trials){
			if (trial != null) {
				RunTimeInfo l2TimeInfo = trial.getL2tRtInfo(), ranTimeInfo = trial.getRanRtInfo();
				l2tCoverage += l2TimeInfo.getCoverage();
				l2tTime += l2TimeInfo.getTime();
				l2tTestCnt += l2TimeInfo.getTestCnt();
				
				ranCovergage += ranTimeInfo.getCoverage();
				ranTime += ranTimeInfo.getTime();
				ranTestCnt += ranTimeInfo.getTestCnt();
				
				if (l2TimeInfo.learnFormula() > 0) {
					l2tValidCoverage += l2TimeInfo.getCoverage();
					ranValidCovergage += ranTimeInfo.getCoverage();
					validNum++;
				}
			}
		}
	   this.l2tRtInfo = new RunTimeInfo(size==0?0:l2tTime/size, size==0?0:l2tCoverage/size, size==0?0:l2tTestCnt/size, validNum == 0 ? 0: l2tValidCoverage/validNum);
	   this.ranRtInfo = new RunTimeInfo(size==0?0:ranTime/size, size==0?0:ranCovergage/size, size==0?0:ranTestCnt/size, validNum == 0? 0:ranValidCovergage/validNum);
	   this.validNum = validNum;
	}

	public List<Trial> getTrials() {
		return trials;
	}

	public int getValidNum() {
		return validNum;
	}
	
	
}
