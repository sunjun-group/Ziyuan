/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.io.excel;

import learntest.main.RunTimeInfo;

/**
 * @author LLT
 *
 */
public class MultiTrial extends Trial {
	private double bestL2tRtCoverage;
	private double bestRanRtCoverage;
	
	
	public void addTrial(Trial trial) {
		if (methodName == null) {
			this.methodName = trial.methodName;
			this.methodLength = trial.methodLength;
			this.methodStartLine = trial.methodStartLine;
		}
		this.l2tRtInfo = RunTimeInfo.average(this.l2tRtInfo, trial.l2tRtInfo);
		this.ranRtInfo = RunTimeInfo.average(this.ranRtInfo, trial.ranRtInfo);
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
}
