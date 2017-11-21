/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.test.gan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import learntest.core.commons.test.gan.evaltrial.GanTrial;
import learntest.core.commons.test.gan.evaltrial.GanTrial.GanAccuracy;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class GanExportData extends AbstractRowData {
	private String methodId;
	private int lastColNum = -1;
	private GanTrial trial;
	private String initCvgs;
	private String cvgs;
	private double avgCvg;

	public String getMethodId() {
		return methodId;
	}

	public void setMethodId(String methodId) {
		this.methodId = methodId;
		setId(methodId);
	}

	public GanTrial getTrial() {
		return trial;
	}

	public void setTrial(GanTrial trial) {
		this.trial = trial;
	}

	public int getLastColNum() {
		return lastColNum;
	}

	public void setLastColNum(int lastColNum) {
		this.lastColNum = lastColNum;
	}

	public String getInitCvgs() {
		if (initCvgs == null) {
			initCvgs = String.valueOf(trial.getInitCoverage());
		} else {
			initCvgs = StringUtils.join(", ", initCvgs, trial.getInitCoverage());
		}
		return initCvgs;
	}

	public void setInitCvgs(String initCvgs) {
		this.initCvgs = initCvgs;
	}

	public String getCvgs() {
		if (cvgs == null) {
			cvgs = String.valueOf(trial.getCoverage());
		} 
		else {
			cvgs = StringUtils.join(", ", cvgs, trial.getCoverage());
		}
		return cvgs;
	}

	public void setCvgs(String cvgs) {
		this.cvgs = cvgs;
	}

	public double getAvgCvg() {
		return avgCvg;
	}

	public void setAvgCvg(double avgCvg) {
		this.avgCvg = avgCvg;
	}

	public String getGanAccuracyStr() {
		List<Integer> nodeIdx = new ArrayList<>(trial.getAccMap().keySet());
		Collections.sort(nodeIdx);
		List<String> list1 = new ArrayList<>();
		for (Integer idx : nodeIdx) {
			GanAccuracy ganAccuracy = trial.getAccMap().get(idx);
			list1.add(ganAccuracy.toString());
		}
		return StringUtils.join(list1, "\r\n");
	}
	
}
