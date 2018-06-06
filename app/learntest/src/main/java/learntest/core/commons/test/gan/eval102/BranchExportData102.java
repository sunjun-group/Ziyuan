/*
 * Copyright (C) 2013 by SUTD (Singapore)
 * All rights reserved.
 *
 * 	Author: SUTD
 *  Version:  $Revision: 1 $
 */

package learntest.core.commons.test.gan.eval102;

import java.util.ArrayList;
import java.util.List;

import cfg.DecisionBranchType;
import sav.common.core.utils.CollectionUtils;
import sav.common.core.utils.StringUtils;

/**
 * @author LLT
 *
 */
public class BranchExportData102 {
	private String methodId;
	private int nodeIdx;
	private DecisionBranchType branchType;
	private int lineNum;
	private List<String> vars;
	private CategoryTrainingData trueTrainData = new CategoryTrainingData();
	private CategoryTrainingData falseTrainData = new CategoryTrainingData();
	private String coverageInfo;
	private double cvg;
	private String initCoverageInfo;

	public String getMethodId() {
		return methodId;
	}

	public void setMethodId(String methodId) {
		this.methodId = methodId;
	}

	public int getNodeIdx() {
		return nodeIdx;
	}

	public void setNodeIdx(int nodeIdx) {
		this.nodeIdx = nodeIdx;
	}

	public CategoryTrainingData getTrueTrainData() {
		return trueTrainData;
	}

	public void setTrueTrainData(CategoryTrainingData trueTrainData) {
		this.trueTrainData = trueTrainData;
	}

	public CategoryTrainingData getFalseTrainData() {
		return falseTrainData;
	}

	public void setFalseTrainData(CategoryTrainingData falseTrainData) {
		this.falseTrainData = falseTrainData;
	}

	public int getLineNum() {
		return lineNum;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}
	
	public String getId() {
		return getId(methodId, nodeIdx, branchType);
	}
	
	public void setVars(List<String> vars) {
		this.vars = vars;
	}
	
	public List<String> getVars() {
		return vars;
	}
	
	public static String getId(String methodId, int nodeIdx, DecisionBranchType branchType) {
		return StringUtils.dotJoin(methodId, nodeIdx, branchType);
	}
	
	public String getCoverageInfo() {
		return coverageInfo;
	}

	public void setCoverageInfo(String coverageInfo) {
		this.coverageInfo = coverageInfo;
	}

	public double getCvg() {
		return cvg;
	}

	public void setCvg(double cvg) {
		this.cvg = cvg;
	}
	
	public String getInitCoverageInfo() {
		return initCoverageInfo;
	}

	public void setInitCoverageInfo(String initCoverageInfo) {
		this.initCoverageInfo = initCoverageInfo;
	}
	
	public DecisionBranchType getBranchType() {
		return branchType;
	}

	public void setBranchType(DecisionBranchType branchType) {
		this.branchType = branchType;
	}

	public class CategoryTrainingData {
		private List<double[]> trainingDps = new ArrayList<double[]>();
		private List<double[]> correctGenDps = new ArrayList<double[]>();
		private List<double[]> wrongGenDps = new ArrayList<double[]>();

		public List<double[]> getTrainingDps() {
			return trainingDps;
		}

		public void setTrainingDps(List<double[]> trainingDps) {
			this.trainingDps = trainingDps;
		}

		public List<double[]> getWrongGenDps() {
			return wrongGenDps;
		}

		public String getTrainDpsStr() {
			return getDpsStr(trainingDps);
		}
		
		private String getDpsStr(List<double[]> dps) {
			StringBuilder sb = new StringBuilder();
			int dpsLastIdx = CollectionUtils.getSize(dps) - 1;
			for (int dpIdx = 0; dpIdx <= dpsLastIdx; dpIdx++) {
				double[] dp = dps.get(dpIdx);
				sb.append("[");
				int varLastIdx = vars.size() - 1;
				for (int varIdx = 0; varIdx <= varLastIdx; varIdx++) {
					sb.append(vars.get(varIdx)).append("=").append(dp[varIdx]);
					if (varIdx != varLastIdx) {
						sb.append(",");
					}
				}
				sb.append("]");
				if (dpIdx != dpsLastIdx) {
					sb.append("\n");
				}
				if (sb.length() >= 32500) {
					System.out.println("TOO BIG " + sb.length());
					sb.append("...(too big)");
					break;
				}
			}
			
			return sb.toString();
		}

		public String getCorrectGenDpsStr() {
			return getDpsStr(correctGenDps);
		}

		public String getWrongGenDpsStr() {
			return getDpsStr(wrongGenDps);
		}

		public void updateTrainDps(List<double[]> datapoints) {
			if (CollectionUtils.isNotEmpty(datapoints)) {
				trainingDps.addAll(datapoints);
			}
		}
		
		public void updateCorrectGenDps(List<double[]> dps) {
			if (CollectionUtils.isNotEmpty(dps)) {
				correctGenDps.addAll(dps);
			}
		}

		public void updateWrongGenDps(List<double[]> dps) {
			if (CollectionUtils.isNotEmpty(dps)) {
				wrongGenDps.addAll(dps);
			}
		}
		
		public int getTrainDpsTotal() {
			return CollectionUtils.getSize(trainingDps);
		}

		public int getGenDpsTotal() {
			return CollectionUtils.getSize(correctGenDps) + CollectionUtils.getSize(wrongGenDps);
		}

		public double getAvgAcc() {
			return CollectionUtils.getSize(correctGenDps) / ((double) getGenDpsTotal());
		}
	}

}
