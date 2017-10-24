/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.test.gan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LLT
 *
 */
public class GanTrial {
	private String methodId;
	private int sampleSize;
	private double initCoverage;
	private double coverage;
	private String decsNodeCvgInfo;
	private Map<Integer, GanAccuracy> accMap;

	public GanTrial() {
		accMap = new HashMap<Integer, GanAccuracy>();
	}

	public void updateAcc(int nodeIdx, double acc) {
		GanAccuracy ganAcc = accMap.get(nodeIdx);
		if (ganAcc == null) {
			ganAcc = new GanAccuracy();
			ganAcc.setNodeIdx(nodeIdx);
		}
		ganAcc.getAccList().add(acc);
	}

	public int getSampleSize() {
		return sampleSize;
	}

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	public Map<Integer, GanAccuracy> getAccMap() {
		return accMap;
	}

	public String getMethodId() {
		return methodId;
	}

	public void setMethodId(String methodId) {
		this.methodId = methodId;
	}

	public double getInitCoverage() {
		return initCoverage;
	}

	public void setInitCoverage(double initCoverage) {
		this.initCoverage = initCoverage;
	}

	public double getCoverage() {
		return coverage;
	}

	public void setCoverage(double coverage) {
		this.coverage = coverage;
	}

	public String getDecsNodeCvgInfo() {
		return decsNodeCvgInfo;
	}

	public void setDecsNodeCvgInfo(String decsNodeCvgInfo) {
		this.decsNodeCvgInfo = decsNodeCvgInfo;
	}

	public void setAccMap(Map<Integer, GanAccuracy> accMap) {
		this.accMap = accMap;
	}

	public static class GanAccuracy {
		private int nodeIdx;
		private List<Double> accList = new ArrayList<Double>();

		public int getNodeIdx() {
			return nodeIdx;
		}

		public void setNodeIdx(int nodeIdx) {
			this.nodeIdx = nodeIdx;
		}

		public List<Double> getAccList() {
			return accList;
		}

		public void setAccList(List<Double> accList) {
			this.accList = accList;
		}

		@Override
		public String toString() {
			return "[nodeIdx=" + nodeIdx + ", accList=" + accList + "]";
		}
	}
}
